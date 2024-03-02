#!/bin/bash


## include config
source ../common/common-function
source config/test-config


DEVICE="video12"
if [ $# == 1 ];then
    DEVICE=$1
fi



TEST_RESULT_PATH=~/test_result
declare -i TOTAL_CACSE_CNT=0
declare -i FAILED_CACSE_CNT=0


SRC_FORMAT_ARRAY=("NV12" "BGR" "RGB")


COMMAND_FILE="screen-preview"




export timeStart=
export timeEnd=

time_mesure_start(){
	timeStart=`date +%s`
}

time_mesure_end() {
    Name=$1
	timeEnd=`date +%s`

    local timeCost=$timeEnd-$timeStart
	local hour min
	hour=$((timeCost/3600))
	min=$(((timeCost-hour*60)/60))
	sec=$((timeCost-hour*3600-min*60))
	echo "$Name Total Time elapsed : $hour:$min:$sec (H:M:S)"
}

time_mesure_start

for SRC_SIZE in "${SRC_SIZE_ARRAY[@]}"; do
    for SRC_FORMAT in "${SRC_FORMAT_ARRAY[@]}"; do
        for SRC_FRAMERATE in "${SRC_FRAMERATE_ARRAY[@]}"; do
            for IO_MODE in "${IO_MODE_ARRAY[@]}"; do
                for TVIN_PORT in "${TVIN_PORT_ARRAY[@]}"; do
                    TOTAL_CACSE_CNT=$TOTAL_CACSE_CNT+1

                    CMD="./$COMMAND_FILE.sh $DEVICE $SRC_SIZE $SRC_FORMAT $SRC_FRAMERATE $IO_MODE 10000 $TVIN_PORT"
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


test_summart $TOTAL_CACSE_CNT  $FAILED_CACSE_CNT
time_mesure_end $COMMAND_FILE
