#!/bin/sh

echo "$(date +%Y-%m-%d\ %H:%M:%S): trying to check hci0...."

function hci0_rfkill()
{
    for f in $(ls /sys/class/rfkill/rfkill*/name 2> /dev/null); do
        rfk_name=$(cat $f)
        if [ $rfk_name = "hci0" ];then
            rfkill unblock ${f//[^0-9]/}
        fi
    done
}

cnt=20
while [ $cnt -gt 0 ]; do
    if ! hciconfig hci0 2>&1 > /dev/null;then
        echo "checking hci0 $cnt......."
        usleep 200000
        cnt=$((cnt - 1))
        if [ $cnt -eq 0 ];then
            echo "hci0 bring up failed!!!"
            exit 1
        fi
     else
        echo "$(date +%Y-%m-%d\ %H:%M:%S): hci0 ready, try to load bluetoothd"
        hci0_rfkill
        usleep 200000
        exit 0
     fi
done

