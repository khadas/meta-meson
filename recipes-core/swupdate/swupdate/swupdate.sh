#!/bin/sh

#get data ubi number
data_mtd_number=$(cat /proc/mtd | grep  -E "data" | awk -F : '{print $1}' | grep -o '[0-9]\+')

SWUPDATE_PATH=/mnt/swupdate/
OTA_FILE_FLAG=$SWUPDATE_PATH/enable-network-ota

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

fbset -fb /dev/fb0 -g 480 480 480 960 32
echo 0 > /sys/class/graphics/fb0/blank
echo 1 > /sys/class/graphics/fb1/blank

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

if [ -f "/mnt/software.swu" ]; then
    echo "find software.swu in data, now start update......"
    export TMPDIR=/mnt
    if [ -f "/proc/inand" ]; then
        if [ -f "/usr/bin/swupdateui" ]; then
            swupdateui /etc/recovery.bmp &
        fi
        swupdate -l 6 -k /etc/swupdate-public.pem -i /mnt/software.swu
    else
        if [ -f "/usr/bin/swupdateui" ]; then
            swupdateui /etc/recovery.bmp &
        fi
        swupdate -l 6 -b "$str" -k /etc/swupdate-public.pem -i /mnt/software.swu
    fi
    if [ $? != 0 ]; then
        echo "swupdate software.swu from data failed!"
        urlmisc clean
        umount /mnt
    else
        echo "swupdate software.swu from data sucess!"
        rm /mnt/software.swu
        umount /mnt
        urlmisc clean
        sync
        sleep 2
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
        if [ -f "/proc/inand" ]; then
            if [ -f "/usr/bin/swupdateui" ]; then
                swupdateui /etc/recovery.bmp &
            fi
            swupdate -l 6 -k /etc/swupdate-public.pem -D "-t 60"
        else
            if [ -f "/usr/bin/swupdateui" ]; then
                swupdateui /etc/recovery.bmp &
            fi
            swupdate -l 6 -b "$str" -k /etc/swupdate-public.pem -D "-t 60"
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
else
    #wait for usb device
    echo "can not find software.swu in data, now find usb device......"
    sleep 5
    name="sda1"
    if [ ! -e "/dev/sda1" ]; then
        name="sda"
    fi
    if [ -f "/run/media/$name/software.swu" ]; then
        export TMPDIR=/run/media/$name
        if [ -f "/proc/inand" ]; then
            if [ -f "/usr/bin/swupdateui" ]; then
                swupdateui /etc/recovery.bmp &
            fi
            swupdate -l 6 -k /etc/swupdate-public.pem -i /run/media/$name/software.swu
        else
            if [ -f "/usr/bin/swupdateui" ]; then
                swupdateui /etc/recovery.bmp &
            fi
            swupdate -l 6 -b "$str" -k /etc/swupdate-public.pem -i /run/media/$name/software.swu
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
            reboot
            echo "swupdate reboot now!"
        fi
    else
        echo "no software.swu found in usb device"
        #execute swupdate for clear misc, no need into recovery after reboot
        swupdate
        urlmisc clean
        umount /run/media/$name
    fi
fi
