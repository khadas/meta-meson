#!/bin/sh

#get data ubi number
data_mtd_number=$(cat /proc/mtd | grep -E -w "data" | awk -F : '{print $1}' | grep -o '[0-9]\+')

SWUPDATE_PATH=/mnt/swupdate/
SWUPDATE_FILE_PATH=""
OTA_FILE_FLAG=$SWUPDATE_PATH/enable-network-ota
DVB_OTA_FILE_FLAG=$SWUPDATE_PATH/enable-dvb-ota

system_mtd_number=$(cat /proc/mtd | grep  -E "system" | awk -F : '{print $1}' | grep -o '[0-9]\+')
case $system_mtd_number in
    3)
        str="0 1 2"
        ;;
    4)
        str="0 1 2 3"
        ;;
    5)
        str="0 1 2 3 4"
        ;;
    6)
        str="0 1 2 3 4 5"
        ;;
    11)
        str="0 1 2 3 4 5 6 7 8 9 10 13"
        ;;
    *)
        ;;
esac

# Self negotiating between drm and display, don't need to set here.
#fbset -fb /dev/fb0 -g 480 480 480 960 32
echo 0 > /sys/class/graphics/fb0/blank
echo 1 > /sys/class/graphics/fb1/blank

resolution=$(fbset | grep mode | awk -F '"' '{print $2}' | sed 's/-0//g')
width=${resolution%x*}
height=${resolution#*x}

show_swupdateui()
{
    # Clean the default directfbrc file and add new configuration
    # suitable for the upgrade.
    if [ -f "/etc/directfbrc" ]; then
        cat /dev/null > /etc/directfbrc
        cat /etc/ota_directfbrc > /etc/directfbrc
    fi

    if [ -f "/usr/bin/swupdateui" ]; then
        if [ -e "/etc/recovery.bmp" ]; then
            swupdateui /etc/recovery.bmp ${width} ${height} &
        elif [ -e "/etc/recovery.jpg" ]; then
            swupdateui /etc/recovery.jpg ${width} ${height} &
        fi
    fi
}

#Waiting for /dev/data device to become ready
TimedOut=10 #10 second
WaitedTime=0
if [ "${data_mtd_number}" = "" ]; then
    echo "can not get data mtd number, maybe emmc device......"
    while [ "$WaitedTime" -lt "$TimedOut" ]
    do
        if [ -b "/dev/data" ]; then
            echo "/dev/data is ready now."
            break;
        fi
        sleep 1
        WaitedTime=$((WaitedTime+1))
        echo "${root} is not ready.  Waited for ${WaitedTime} second"
    done
    mount -t ext4 /dev/data /mnt
else
    echo "get data mtd number, now mount data partition......"
    ubiattach /dev/ubi_ctrl -m ${data_mtd_number}
    mount -t ubifs /dev/ubi0_0 /mnt
fi

if [[ -f "/mnt/software.swu" ]]; then
    SWUPDATE_FILE_PATH=/mnt/software.swu
elif [[ -f "/mnt/unencrypted/software.swu" ]]; then
    SWUPDATE_FILE_PATH=/mnt/unencrypted/software.swu
fi

if [[ ! -z "$SWUPDATE_FILE_PATH" ]]; then
    echo "find software.swu in data($(dirname $SWUPDATE_FILE_PATH)), now start update......"
    export TMPDIR=/mnt
    show_swupdateui
    if [ "${1}" = "ubifs" ]; then
        swupdate -l 6 -b "$str" -k /etc/swupdate-public.pem -i $SWUPDATE_FILE_PATH
    else
        swupdate -l 6 -k /etc/swupdate-public.pem -i $SWUPDATE_FILE_PATH
    fi
    if [ $? != 0 ]; then
        echo "swupdate software.swu from data failed!"
        urlmisc clean
        umount /mnt
    else
        echo "swupdate software.swu from data sucess!"
        rm $SWUPDATE_FILE_PATH
        umount /mnt
        urlmisc clean
        sync
        sleep 2
        if [ -b /dev/bootloader_up ]; then
            uenv set write_boot 1
        fi
        reboot
        echo "swupdate reboot now!"
    fi
elif [ -f "$OTA_FILE_FLAG" ]; then
    source $SWUPDATE_PATH/apply_info.sh /mnt
    $SWUPDATE_PATH/start_wifi.sh
    wait_time=90
    ping_time=0
    while [ $ping_time -lt $wait_time ]
    do
        echo "Tried $ping_time seconds"
        ping_output=$(ping -c 1 $SWUPDATE_OTA_SERVER 2>&1)
        ping_result=$?
        echo "ping return result: $ping_result"
        case $ping_result in
            0)
                NETWORK_READY=1
                wget --spider -q `cat $OTA_FILE_FLAG`
                if [ $? == 0 ]; then
                    OTA_PACKAGE_READY=1
                fi
                break;
                ;;
            *)
                if echo $ping_output | grep "Network is unreachable" ; then
                    echo "Network is unreachable, cost 1 second"
                    sleep 1
                    let ping_time++
                else
                    let ping_time=ping_time+10
                    echo "Ping failed, this will cost 10 second"
                fi
                ;;
        esac
    done

    #Here we detect network is ready
    #Here we detect if the OTA package exist or not
    if [ -n "$NETWORK_READY" ] && [ -n "$OTA_PACKAGE_READY" ]; then
        show_swupdateui
        if [ "${1}" = "ubifs" ]; then
            swupdate -l 6 -b "$str" -k /etc/swupdate-public.pem -D "-t 60"
        else
            swupdate -l 6 -k /etc/swupdate-public.pem -D "-t 60"
        fi
        swupdate_result=$?
        echo "swupdate return result: $swupdate_result"
        case $swupdate_result in
            0)
                echo "swupdate software.swu from url sucess!"
                rm -f $SWUPDATE_PATH/swupdate_retry
                ;;
            10)
                echo "swupdate software.swu from url failed, need retry!"
                keep_recovery=1
                touch $SWUPDATE_PATH/swupdate_retry
                ;;
            *)
                echo "swupdate software.swu from url failed, return to normal system!"
                ;;
        esac
    else
        if [ -z "$NETWORK_READY" ]; then
            echo "Network is not ready, reboot to normal system"
        else
            if [ -z "$OTA_PACKAGE_READY" ]; then
                echo "OTA_PACKAGE is not ready, reboot to normal system"
            else
                echo "Should not come here, please debug ASAP."
            fi
        fi
        echo "Clean up recovery flag in misc partition, then reboot to normal system"
    fi

    if [ -f $SWUPDATE_PATH/swupdate_retry ]; then
        echo "Because upgrade already in progress, We will try util upgrade OK."
        echo "We will not clean up anything,becaue they are still needed by next trying."
    else
        if [ -z "$keep_recovery" ]; then
            echo "Clean up recovery settings, will NOT enter recovery on next boot."
            urlmisc clean
            rm -fr $SWUPDATE_PATH
        fi
    fi
    sync
    umount /mnt

    read -t 5 -p "Rebooting...^-^..."
    if [ $? != 0 ]; then
        reboot -f
    fi
elif [ -f "$DVB_OTA_FILE_FLAG" ]; then
    wait_time=90
    ping_time=0
    while [ $ping_time -lt $wait_time ]
    do
        echo "Tried $ping_time seconds"
        if [ -e /dev/dvb0.frontend0 ]; then
            DVB_DEV_READY=1
            break;
        else
            echo "dvb device not ready, cost 1 second"
            sleep 1
            let ping_time++
        fi
    done

    if [ -n "$DVB_DEV_READY" ]; then
        show_swupdateui
        if [ -f /usr/bin/dvb_ota.sh ]; then
            /usr/bin/dvb_ota.sh start /tmp/software.swu $(cat $DVB_OTA_FILE_FLAG 2>/dev/null)
            dvbota_result=$?
            echo "dvbota result: $dvbota_result"
            if [ $dvbota_result ]; then
                if [ "${1}" = "ubifs" ]; then
                    swupdate -l 6 -b "$str" -k /etc/swupdate-public.pem -i /tmp/software.swu
                else
                    swupdate -l 6 -k /etc/swupdate-public.pem -i /tmp/software.swu
                fi
                if [ $? != 0 ]; then
                    echo "dvbota swupdate software.swu from data failed!"
                else
                    echo "dvbota swupdate software.swu from data success!"
                    echo "dvbota swupdate will reboot!"
                fi
            else
                echo "dvbota failed, please check the log:/tmp/log.dvb_ota."
            fi
        else
            echo "/usr/bin/dvb_ota.sh not found for dvb ota."
        fi
    fi

    sync

    read -t 5 -p "Rebooting ..."
    if [ $? != 0 ]; then
        rm $SWUPDATE_FILE_PATH
        rm -rf $SWUPDATE_PATH
        cp -f /tmp/log.dvb_ota /mnt && sync
        umount /mnt
        urlmisc clean
        reboot -f
    fi
else
    #wait for usb device
    echo "can not find software.swu in data, now find usb device......"
    sleep 5
    name="`mount | grep /run/media | cut -d " " -f3 | cut -d "/" -f4`"
    if [ -f "/run/media/$name/software.swu" ]; then
        export TMPDIR=/run/media/$name
        show_swupdateui
        if [ "${1}" = "ubifs" ]; then
            swupdate -l 6 -b "$str" -k /etc/swupdate-public.pem -i /run/media/$name/software.swu
        else
            swupdate -l 6 -k /etc/swupdate-public.pem -i /run/media/$name/software.swu
        fi
        if [ $? != 0 ]; then
            echo "swupdate software.swu from usb failed!"
            umount /run/media/$name
            urlmisc clean
        else
            echo "swupdate software.swu from usb sucess!"
            umount /run/media/$name
            urlmisc clean
            sync
            sleep 2
            if [ -b /dev/bootloader_up ]; then
                uenv set write_boot 1
            fi
            reboot
            echo "swupdate reboot now!"
        fi
    else
        echo "no software.swu found in usb device"
        #execute swupdate for clear misc, no need into recovery after reboot
        swupdate
        urlmisc clean
        #umount /run/media/$name
    fi
fi
