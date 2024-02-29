#!/bin/sh -x

PATH="/lib/modules/isp"
/sbin/modprobe v4l2-async
/sbin/insmod $PATH/iv009_isp_iq.ko
/sbin/insmod $PATH/iv009_isp_lens.ko
/sbin/insmod $PATH/iv009_isp_sensor.ko
/sbin/insmod $PATH/iv009_isp.ko dcam=2
/usr/bin/iv009_isp -n 2