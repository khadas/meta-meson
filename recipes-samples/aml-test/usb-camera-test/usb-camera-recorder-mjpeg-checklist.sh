#!/bin/bash


## include config
source ../common/common-function
source config/test-config


DEVICE="video0"
if [ $# == 1 ];then
    DEVICE=$1
fi


TEST_RESULT_PATH=~/test_result
declare -i TOTAL_CACSE_CNT=0
declare -i FAILED_CACSE_CNT=0


PARSE_TYPE_ARRAY=("h264" "h265")


COMMAND_FILE="usb-camera-recorder-mjpeg"


time_mesure_start


for PARSE_TYPE in "${PARSE_TYPE_ARRAY[@]}"; do
    TOTAL_CACSE_CNT=$TOTAL_CACSE_CNT+1

    CMD="./$COMMAND_FILE.sh $DEVICE 100 $PARSE_TYPE"
    echo $CMD
    eval $CMD
    if [ -e "$TEST_RESULT_PATH/$COMMAND_FILE.failed" ]; then
        FAILED_CACSE_CNT=$FAILED_CACSE_CNT+1
        echo "$CMD failed : total $TOTAL_CACSE_CNT cases, failed $FAILED_CACSE_CNT."
        rm "$TEST_RESULT_PATH/$COMMAND_FILE.failed"
    fi
    if [ -e "$TEST_RESULT_PATH/$COMMAND_FILE.pass" ]; then
        echo "$CMD pass : total $TOTAL_CACSE_CNT cases, failed $FAILED_CACSE_CNT."
        rm "$TEST_RESULT_PATH/$COMMAND_FILE.pass"
    fi
done


test_summart $TOTAL_CACSE_CNT  $FAILED_CACSE_CNT
time_mesure_end $COMMAND_FILE
