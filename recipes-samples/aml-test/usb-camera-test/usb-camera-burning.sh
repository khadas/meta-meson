#!/bin/bash


## include config
source ../common/common-function


DEVICE="video0"
LOOP_CNT=1000000
if [ $# == 1 ];then
    DEVICE=$1
elif [ $# == 2 ];then
    DEVICE=$1
    LOOP_CNT=$2
fi

SHELL_FILE_NAME=${BASH_SOURCE}
SHELL_FILE_NAME=$(basename "$SHELL_FILE_NAME")
TEST_CASE_NAME=${SHELL_FILE_NAME%.*}

TEST_RESULT_PATH=~/test_result

rm $TEST_RESULT_PATH/test_report.txt


i=1

while [ $i -le  $LOOP_CNT ]
do
    echo "loop i="$i
    time_mesure_start

    ./usb-camera-capture-checklist.sh
    ./usb-camera-preview-checklist.sh
    ./usb-camera-recorder-mjpeg-checklist.sh
    ./usb-camera-recorder-yuv-checklist.sh
    ./usb-camera-nn-checklist.sh


    time_mesure_end $TEST_CASE_NAME

    # check system memory information
    echo "free" && free
    echo "cat /proc/meminfo" && cat /proc/meminfo
    echo "vmstat -s" && vmstat -s

    let i++
done


if [ $i -le  $LOOP_CNT ]; then
    echo "burning test failed, total=$LOOP_CNT, current=$i"
else
    echo "burning test pass, total=$LOOP_CNT, current=$i"
fi

