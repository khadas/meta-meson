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


SRC_FORMAT_ARRAY=("NV21" "NV12" "BGR" "RGB")
# DST_FORMAT_ARRAY=("NV21" "NV12" "BGR" "RGB")
DST_FORMAT_ARRAY=("NV21" "NV12")

PARSE_TYPE_ARRAY=("h264" "h265")


COMMAND_FILE="mipi-camera-convert-recorder"


time_mesure_start

for SRC_SIZE in "${SRC_SIZE_ARRAY[@]}"; do
    for SRC_FORMAT in "${SRC_FORMAT_ARRAY[@]}"; do
        for SRC_FRAMERATE in "${SRC_FRAMERATE_ARRAY[@]}"; do
            for DST_SIZE in "${DST_SIZE_ARRAY[@]}"; do
                for DST_FORMAT in "${DST_FORMAT_ARRAY[@]}"; do
                    for DST_FRAMERATE in "${DST_FRAMERATE_ARRAY[@]}"; do
                        for IO_MODE in "${IO_MODE_ARRAY[@]}"; do
                            for PARSE_TYPE in "${PARSE_TYPE_ARRAY[@]}"; do
                                TOTAL_CACSE_CNT=$TOTAL_CACSE_CNT+1

                                CMD="./$COMMAND_FILE.sh $DEVICE $SRC_SIZE $SRC_FORMAT $SRC_FRAMERATE $DST_SIZE $DST_FORMAT $DST_FRAMERATE $IO_MODE 100 $PARSE_TYPE"
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
                done
            done
        done
    done
done


test_summart $TOTAL_CACSE_CNT  $FAILED_CACSE_CNT
time_mesure_end $COMMAND_FILE
