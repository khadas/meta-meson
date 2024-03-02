#!/bin/bash

FILE_EXT="mp4"

DEVICE="video0"
FRAME_CNT=0
PARSE_TYPE="h264parse"

if [ $# == 3 ];then
    DEVICE=$1
    FRAME_CNT=$2
    PARSE_TYPE=$3"parse"
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
    *.sh video0 100 h264
	"
}

display_help

if [ $# == 1 ];then  # use customer define camera
    DEVICE=$1
fi


if [ $FRAME_CNT == 0 ];then
    USE_FRAME_CNT=
else
    USE_FRAME_CNT=" num-buffers=$FRAME_CNT"
fi

# 10s, 1920*1080 30fps, USB camera output(MJPGE)
CMD="gst-launch-1.0 -v v4l2src device=/dev/$DEVICE  $USE_FRAME_CNT ! jpegdec ! amlvenc ! $PARSE_TYPE ! qtmux ! filesink location=$FULL_FILE_PATH"
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
