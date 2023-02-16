#!/bin/sh

FRACTION=25
MEMORY=`free | grep "Mem:" | awk '{print $2}'`
SIZE=$((MEMORY * FRACTION / 100 * 1024))

case "$1" in
    start)
        echo $SIZE > /sys/block/zram0/disksize
        mkswap /dev/zram0
        swapon /dev/zram0 -p 10
        ;;
    stop)
        swapoff /dev/zram0
        ;;
    *)
        echo "Usage: $(basename $0) (start | stop)"
        exit 1
        ;;
esac
