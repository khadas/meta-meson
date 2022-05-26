#!/bin/sh

WAIT_BLUETOOTHD()
{
	for i in `seq 1 10`
	do
		sleep 1
		ps -A | grep bluetoothd 2> /dev/null
		if [ $? -eq 0 ]
		then
			echo $0 bluetoothd already running
			break;
		else
			if [ $i -eq 2 ]
			then
				echo "bluetoothd no running"
				return -1
			fi
		fi
	done
	return 0
}

A2DP_BOTH_SERVICE()
{
	echo "|--bluez a2dp-sink/a2dp-source service--|"

	bluealsa -p a2dp-sink -p a2dp-source &
	usleep 200000
	bt-halplay &

	for i in `seq 1 10`
	do
		sleep 2
		hciconfig hci0 piscan
		echo $(hciconfig) | grep PSCAN
		if [ $? -eq 0 ]
		then
			echo "hci0 already open scan"
			break;
		else
			if [ $i -eq 10 ]
			then
				echo "hci0 open scan fail!"
			fi
		fi
	done
	hciconfig hci0 inqparms 18:1024
	hciconfig hci0 pageparms 18:1024
}


A2DP_SINK_SERVICE()
{
	echo "|--bluez a2dp-sink service--|"

	bluealsa -p a2dp-sink &
	usleep 200000
	bt-halplay &

	for i in `seq 1 10`
	do
		sleep 2
		hciconfig hci0 piscan
		echo $(hciconfig) | grep PSCAN
		if [ $? -eq 0 ]
		then
			echo "hci0 already open scan"
			break;
		else
			if [ $i -eq 10 ]
			then
				echo "hci0 open scan fail!"
			fi
		fi
	done
	hciconfig hci0 inqparms 18:1024
	hciconfig hci0 pageparms 18:1024
}

A2DP_SOURCE_SERVICE()
{
	echo "|--bluez a2dp-source service--|"
	bluealsa -p a2dp-source &
}

Blue_start()
{
	echo "|-----start bluez-alsa----|"
	WAIT_BLUETOOTHD
	if [ $? -ne 0 ]; then
		echo "|-----bluez-alsa failed to start----|"
		return -1
	fi

	if [ "$mode" = "sink" ];then
		A2DP_SINK_SERVICE
	elif [ "$mode" = "source" ];then
		A2DP_SOURCE_SERVICE
	else
		A2DP_BOTH_SERVICE
	fi

	default_agent > /dev/null &

	echo "|-----bluez-alsa is ready----|"
}

#Blue_start()
#{
#	echo "|-----start bluez-alsa----|"
#	WAIT_BLUETOOTHD
#	if [ $? -ne 0 ]; then
#		echo "|-----bluez-alsa failed to start----|"
#		return -1
#	fi
#
#	bluealsa &
#	usleep 200000
#	bt-halplay &
#	default_agent > /dev/null &
#
#	for i in `seq 1 10`
#	do
#		sleep 1
#		hciconfig hci0 piscan
#		echo $(hciconfig) | grep PSCAN
#		if [ $? -eq 0 ]
#		then
#			echo "hci0 already open scan"
#			break;
#		else
#			if [ $i -eq 5 ]
#			then
#				echo "hci0 open scan fail!"
#			fi
#		fi
#	done
#
#	hciconfig hci0 inqparms 18:1024
#	hciconfig hci0 pageparms 18:1024
#
#	echo "|-----bluez-alsa is ready----|"
#}


Blue_stop()
{
	killall bluealsa
	killall bt-halplay
	killall default_agent
	echo "|-----bluez-alsa is shutdown-----|"
}

if [ $2 ];then
	mode=$2
else
	mode="both"
fi

case "$1" in
	start)
		Blue_start &
		;;
	stop)
		Blue_stop
		;;
	*)
		echo "Usage: $0 {start|stop}"
		exit 1
esac

exit $?

