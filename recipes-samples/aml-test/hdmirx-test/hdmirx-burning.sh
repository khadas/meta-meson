#!/bin/bash



if [ $# == 1 ];then
    LOOP_CNT=$1
else
    LOOP_CNT=1000000
fi


TEST_RESULT_PATH=~/test_result

rm $TEST_RESULT_PATH/test_report.txt

i=1

while [ $i -le  $LOOP_CNT ]
do
    echo "loop i="$i

    ./hdmirx-capture.sh 1920 1080
    [[ ! -s "$TEST_RESULT_PATH/hdmirx-capture.pass" ]] || break
    rm "$TEST_RESULT_PATH/hdmirx-capture.pass"

    ./hdmirx-recorder.sh 1920 1080 h264
    [[ ! -s "$TEST_RESULT_PATH/hdmirx-recorder.pass" ]] || break
    rm "$TEST_RESULT_PATH/hdmirx-recorder.pass"

    ./hdmirx-recorder.sh 1920 1080 h265
    [[ ! -s "$TEST_RESULT_PATH/hdmirx-recorder.pass" ]] || break
    rm "$TEST_RESULT_PATH/hdmirx-recorder.pass"

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

