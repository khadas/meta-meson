#!/bin/bash

FILE_EXT="mp4"

DEVICE="media0"
SRC_WIDTH=1920
SRC_HEIGHT=1080
SRC_FORMAT="NV21"
SRC_FRAMERATE="30/1"
IO_MODE="dmabuf"
FRAME_CNT=0
PARSE_TYPE="h264parse"

if [ $# == 8 ];then
    DEVICE=$1
    SRC_WIDTH=$2
    SRC_HEIGHT=$3
    SRC_FORMAT=$4
    SRC_FRAMERATE=$5
    IO_MODE=$6
    FRAME_CNT=$7
    PARSE_TYPE=$8"parse"
fi

if [ $# == 1 ];then
    DEVICE=$1
fi

# fixed
###############################################################
SHELL_FILE_NAME=${BASH_SOURCE}
SHELL_FILE_NAME=$(basename "$SHELL_FILE_NAME")
TEST_CASE_NAME=${SHELL_FILE_NAME%.*}
SHORT_FILE_PATH=$TEST_CASE_NAME.$FILE_EXT
echo $SHORT_FILE_PATH

TEST_RESULT_PATH=~/test_result
mkdir -p $TEST_RESULT_PATH
FULL_FILE_PATH=$TEST_RESULT_PATH/$SHORT_FILE_PATH
echo $FULL_FILE_PATH
###############################################################

## Display parameters
display_help() {
	echo "\
	***************************HELP**************************
    TEST CASE : $TEST_CASE_NAME
    Please make sure the only one camera is plugin
    if no parameters, $DEVICE will be act as the default camera

    EXAMPLES:
    *.sh media0 1920 1080 RGB 30/1 100 dmabuf h264
    *.sh 
	"
}

display_help


if [ $IO_MODE == "dmabuf" ];then
    USE_IO_MODE=
else
    USE_IO_MODE="io-mode=$IO_MODE"
fi

if [ $FRAME_CNT == 0 ];then
    USE_FRAME_CNT=
else
    USE_FRAME_CNT=" num-buffers=$FRAME_CNT"
fi

# 10s, 1920*1080 30fps
CMD="gst-launch-1.0 -v v4l2src device=/dev/$DEVICE $USE_IO_MODE $USE_FRAME_CNT ! video/x-raw,width=$SRC_WIDTH,height=$SRC_HEIGHT,format=$SRC_FORMAT,framerate=$SRC_FRAMERATE ! amlvenc ! $PARSE_TYPE ! qtmux ! filesink location=$FULL_FILE_PATH"
echo $CMD
eval $CMD



if [ -s $FULL_FILE_PATH ]; then
    RESULT=($(gst-discoverer-1.0 $FULL_FILE_PATH | grep video))
    if [ -n "$RESULT" ]; then
        echo "$TEST_CASE_NAME test pass"
        touch $TEST_RESULT_PATH/$TEST_CASE_NAME.pass

        # echo "Start play the recorded file :"$FULL_FILE_PATH
        # ../playback-test/playback-test.sh $FULL_FILE_PATH
    else
        echo "$TEST_CASE_NAME test failed, file is not empty, bug it is not a video file"
        touch $TEST_RESULT_PATH/$TEST_CASE_NAME.failed
    fi
else
    echo "$TEST_CASE_NAME test failed, file not exist, or it's empty"
    touch $TEST_RESULT_PATH/$TEST_CASE_NAME.failed
fi

#wait all child process done
wait
