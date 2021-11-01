#!/bin/sh

if [ -f "/data/software.swu" ]; then
    check_result=$(cpio -t -F /data/software.swu | grep sw-description.sig)
    if [ "${check_result}" == "sw-description.sig" ]; then
        echo "find software.swu in data, now start update......"
        reboot recovery
    else
        echo "software.swu file error"
    fi
elif [ -f "/run/media/sda1/software.swu" ]; then
    check_result=$(cpio -t -F /run/media/sda1/software.swu | grep sw-description.sig)
    if [ "${check_result}" == "sw-description.sig" ]; then
        echo "find software.swu in u-disk, now start update......"
        reboot recovery
    else
        echo "software.swu file error"
    fi
else
    echo "not find software.swu"
fi
