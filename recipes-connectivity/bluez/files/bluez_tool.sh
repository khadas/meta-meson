#!/bin/sh

configure_file="/etc/bluetooth/main.conf"

function hci0_rfkill()
{
	for f in $(ls /sys/class/rfkill/rfkill*/name 2> /dev/null); do
		rfk_name=$(cat $f)
		if [ $rfk_name = "hci0" ];then
			rfkill unblock ${f//[^0-9]/}
		fi
	done
}

function get_device_from_conf()
{
	if [ -f $configure_file ];then
		str=`grep "Device=" $configure_file`
		if [ ! $str = "" ];then
			device=`echo $str | awk -F = '{print $2}'`
		else
			echo No device defined in confirue file
		fi
	else
		echo "No configure file"
	fi
}

function get_tty_from_conf()
{
	if [ -f $configure_file ]; then
		str=`grep "TTY=" $configure_file`
		if [ ! $str = "" ]; then
			tty=`echo $str | awk -F = '{print $2}'`
		else
			echo No TTY defined in configuration file
		fi
	else
		echo "No configuration file"
	fi
}


rtk_bdaddr=/opt/bdaddr
aml_bdaddr=/sys/module/kernel/parameters/btmac

realtek_bt_init()
{
	if [[ x$(cat $aml_bdaddr) != x && x$(cat $aml_bdaddr) != x"(null)" ]];then
		cat $aml_bdaddr > $rtk_bdaddr
	else
		rm -f $rtk_bdaddr
	fi

	local cnt=5
	while [ $cnt -gt 0 ]; do
		lsusb | grep "0bda:"
		if [ $? -eq 1 ]; then
			echo "checking lsusb...  $cnt"
			usleep 100000
			cnt=$(($cnt-1))
		else
			modprobe rtk_btusb
			break
		fi
	done

	if [ $cnt -eq 0 ]; then
		modprobe rtk_btuart
		usleep 500000
		rtk_hciattach -n -s 115200 "$tty" rtk_h5 &
	fi
}

qca_bt_init()
{
	modprobe hci_uart
	usleep 300000
	hciattach -s 115200 "$tty" qca
}

aml_bt_init()
{
	if [ -f /sys/bus/mmc/devices/mmc1:0000/mmc1:0000:1/device ]; then
		bt_chip_id=`cat /sys/bus/mmc/devices/mmc1:0000/mmc1:0000:1/device`
	else
		bt_chip_id=`cat /sys/bus/mmc/devices/mmc0:0000/mmc0:0000:1/device`
	fi
	case "${bt_chip_id}" in
	0x8888)  #w1 need insmod ko
		modprobe sdio_bt
		;;
	esac
	usleep 200000
	hciattach -s 115200 "$tty" aml
	usleep 100000
}

brcm_bt_init()
{
	brcm_patchram_plus --enable_hci --baudrate 2000000 --use_baudrate_for_download --patchram /etc/bluetooth/BCM4359C0SR2.hcd /dev/ttyS1 --no2bytes &
}

service_down()
{
	echo "|--stop bluez service--|"
	#killall bluetoothd
	sh bluez-alsa.sh stop
}

service_up()
{
	echo "|--start bluez service--|"
#	grep "Debug=1" $configure_file > /dev/null
#	if [ $? -eq 0 ]; then
#		echo "|--bluetoothd debug log on--|"
#		/usr/libexec/bluetooth/bluetoothd -n -d &
#	else
#		/usr/libexec/bluetooth/bluetoothd -n &
#	fi
	sh bluez-alsa.sh start
}

Blue_start()
{
	echo "|--bluez: device = $device mode = $mode--|"
#	echo 0 > /sys/class/rfkill/rfkill0/state
#	usleep 500000
#	echo 1 > /sys/class/rfkill/rfkill0/state

	echo
	echo "|-----start bluez----|"

	if [ $device = "rtk" ];then
		realtek_bt_init
	elif [ $device = "qca" ];then
		qca_bt_init
	elif [ $device = "aml" ];then
		aml_bt_init
	elif [ $device = "bcm" ];then
		brcm_bt_init
	else
		modprobe hci_uart
		usleep 300000
		hciattach -s 115200 /dev/ttyS1 any
	fi

	local cnt=10
	while [ $cnt -gt 0 ]; do
		hciconfig hci0 2> /dev/null
		if [ $? -eq 1 ];then
			echo "checking hci0 ......."
			sleep 1
			cnt=$((cnt - 1))
		else
			break
		fi
	done

	if [ $cnt -eq 0 ];then
		echo "hci0 bring up failed!!!"
		exit 0
	fi

	#hci0_rfkill

	grep -iq "Debug=1" $configure_file > /dev/null
	if [ $? -eq 0 ]; then
		hcidump -w /etc/bluetooth/btsnoop.cfa &
		cnt=10
		while [ $cnt -gt 0 ]; do
			if [ -f "/sys/kernel/debug/bluetooth/hci0/bt_debug_log_level" ];then
				echo 1 >  /sys/kernel/debug/bluetooth/hci0/bt_debug_log_level
				break
			else
				echo "checking bt_debug_log_level ......."
				usleep 20000
				cnt=$((cnt - 1))
			fi
		done
	fi

	service_up

	echo "|-----bluez is ready----|"
}

Blue_stop()
{
	echo -n "Stopping bluez"
	service_down
	killall rtk_hciattach
	killall hciattach
        killall brcm_patchram_plus
	#rmmod sdio_bt
	#rmmod hci_uart
	#rmmod rtk_btusb
	#sleep 2
	#echo 0 > /sys/class/rfkill/rfkill0/state
	#echo
	echo "|-----bluez is shutdown-----|"
}

if [ $2 ];then
	mode=$2
else
	mode="sink"
fi

if [ $3 ];then
	device=$3
else
	get_device_from_conf
fi

if [ $4 ];then
	tty=$4
else
	get_tty_from_conf
fi

case "$1" in
	start)
		Blue_start
		;;
	restart)
		Blue_stop
		Blue_start
		;;
	stop)
		Blue_stop
		;;
	*)
		echo "Usage: $0 {start|stop}"
		exit 1
esac

exit $?

