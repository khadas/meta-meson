#!/bin/bash


## include config
source ../common/common-function
source config/test-config


DEVICE="media0"
if [ $# == 1 ];then
    DEVICE=$1
fi


TEST_RESULT_PATH=~/test_result
declare -i TOTAL_CACSE_CNT=0
declare -i FAILED_CACSE_CNT=0



# JPEG encode HW support NV21/NV12/YUY2 directly, High performance! 
# JPEG encode HW do not support RGB directly, Low performance! 
# SRC_COLOR_FORMAT_ARRAR=("NV21" "NV12" "YUY2" "RGB")
SRC_FORMAT_ARRAY=("RGB")


COMMAND_FILE="mipi-camera-capture"


time_mesure_start


for SRC_SIZE in "${SRC_SIZE_ARRAY[@]}"; do
    for SRC_FORMAT in "${SRC_FORMAT_ARRAY[@]}"; do
        for IO_MODE in "${IO_MODE_ARRAY[@]}"; do
            TOTAL_CACSE_CNT=$TOTAL_CACSE_CNT+1

            CMD="./$COMMAND_FILE.sh $DEVICE $SRC_SIZE $SRC_FORMAT 30/1 $IO_MODE"
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
    done
done


test_summart $TOTAL_CACSE_CNT  $FAILED_CACSE_CNT
time_mesure_end $COMMAND_FILE

