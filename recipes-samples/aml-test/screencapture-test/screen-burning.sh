#!/bin/bash


## include config
source ../common/common-function


LOOP_CNT=1000000
if [ $# == 1 ];then
    LOOP_CNT=$1
fi


SHELL_FILE_NAME=${BASH_SOURCE}
SHELL_FILE_NAME=$(basename "$SHELL_FILE_NAME")
TEST_CASE_NAME=${SHELL_FILE_NAME%.*}

TEST_RESULT_PATH=~/test_result

echo "=============screen test start..."

echo "free" && free
echo "cat /proc/meminfo" && cat /proc/meminfo
echo "vmstat -s" && vmstat -s


rm $TEST_RESULT_PATH/test_report.txt

i=1

while [ $i -le  $LOOP_CNT ]
do
    echo "loop i="$i

    time_mesure_start

    ./screen-capture-checklist.sh
    ./screen-preview-checklist.sh
    ./screen-recorder-checklist.sh


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

