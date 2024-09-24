#!/bin/sh -x

# check chip name
CHIP_NAME=""
if cat /proc/device-tree/compatible | grep "t7c_a311d2_kvim4n"; then
    CHIP_NAME="t7c"
elif cat /proc/device-tree/compatible | grep "t7c_a311d2_an400-hdmitx-only"; then
    CHIP_NAME="t7c"
elif cat /proc/device-tree/compatible | grep "t7_a311d2_kvim4"; then
    CHIP_NAME="t7"
elif cat /proc/device-tree/compatible | grep "g12b_w400_a"; then
    CHIP_NAME="g12b"
fi

PATH="/lib/modules/isp"
if [[ $CHIP_NAME == "t7c" ]]; then
    #/sbin/insmod $PATH/imx415.ko
    /sbin/insmod $PATH/amlsens.ko
    /sbin/insmod $PATH/amlcam.ko
    /sbin/insmod $PATH/dw9714.ko
elif [[ $CHIP_NAME == "t7" ]]; then
    /sbin/insmod $PATH/iv009_isp_iq.ko
    /sbin/insmod $PATH/iv009_isp_lens.ko
    /sbin/insmod $PATH/iv009_isp_sensor.ko
    /sbin/insmod $PATH/iv009_isp.ko dcam=2
    /usr/bin/iv009_isp -n 2
elif [[ $CHIP_NAME == "g12b" ]]; then
    /sbin/modprobe v4l2-async
    /sbin/insmod $PATH/iv009_isp_iq.ko
    /sbin/insmod $PATH/iv009_isp_lens.ko
    /sbin/insmod $PATH/iv009_isp_sensor.ko
    /sbin/insmod $PATH/iv009_isp.ko dcam=2
    /usr/bin/iv009_isp -n 2
fi
