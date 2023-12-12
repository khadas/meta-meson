#!/bin/sh -x

PATH="/lib/modules/isp"
/sbin/insmod $PATH/iv009_isp_iq.ko
/sbin/insmod $PATH/iv009_isp_lens.ko
/sbin/insmod $PATH/iv009_isp_sensor.ko
/sbin/insmod $PATH/iv009_isp.ko dcam=2
/usr/bin/iv009_isp -n 2
#/sbin/insmod $PATH/imx415.ko
/sbin/insmod $PATH/amlsens.ko
/sbin/insmod $PATH/amlcam.ko
#/sbin/insmod $PATH/dw9714.ko