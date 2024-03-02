#!/bin/bash


## include config
source ../common/common-function


DEVICE="media0"
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

echo "=============mipi camera test start..."

echo "free" && free
echo "cat /proc/meminfo" && cat /proc/meminfo
echo "vmstat -s" && vmstat -s


rm $TEST_RESULT_PATH/test_report.txt

i=1

while [ $i -le  $LOOP_CNT ]
do
    echo "loop i="$i
    time_mesure_start

    ./mipi-camera-capture-checklist.sh $DEVICE
    ./mipi-camera-preview-checklist.sh $DEVICE
    ./mipi-camera-convert-preview-checklist.sh $DEVICE
    ./mipi-camera-recorder-checklist.sh $DEVICE
    ./mipi-camera-convert-recorder-checklist.sh $DEVICE
    ./mipi-camera-nn-checklist.sh $DEVICE

    
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

