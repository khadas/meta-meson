#!/bin/sh

DISP_MODE=/sys/class/display/mode
echo "panel" > $DISP_MODE
export QT_QPA_PLATFORM=eglfs
export QT_QPA_EGLFS_INTEGRATION="eglfs_mali"

case "$1" in
    stop);;
    restart);;
    start)
        fbset -fb /dev/fb0 -g 720 720 720 1440 32
        #call qt demo as launcher
        /usr/share/examples/widgets/animation/easing/easing &
        #sleep 2s wait for QT rendering to succeed,
        #and close osd1
        #sleep 2s
        echo 0 0 719 719 > /sys/class/graphics/fb0/window_axis
        echo 0 0 719 719 > /sys/class/graphics/fb0/free_scale_axis
        echo 0x10001 > /sys/class/graphics/fb0/free_scale
        echo 0 > /sys/class/graphics/fb0/blank
        echo 1 > /sys/class/graphics/fb1/blank
    ;;
esac

exit $?
