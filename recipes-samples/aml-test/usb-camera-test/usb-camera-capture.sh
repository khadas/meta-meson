#!/bin/bash


FILE_EXT="jpg"

DEVICE="video0"
SRC_WIDTH=1920
SRC_HEIGHT=1080
SRC_FORMAT="YUY2"


if [ $# == 4 ];then
    DEVICE=$1
    SRC_WIDTH=$2
    SRC_HEIGHT=$3
    SRC_FORMAT=$4
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
    *.sh video0 1920 1080 YUY2
    *.sh 
	"
}

display_help

CMD="gst-launch-1.0 -v v4l2src device=/dev/$DEVICE num-buffers=1 ! video/x-raw,width=$SRC_WIDTH,height=$SRC_HEIGHT,format=$SRC_FORMAT ! amljpegenc ! filesink location=$FULL_FILE_PATH"
echo $CMD
eval $CMD

if [ -s $FULL_FILE_PATH ]; then
    RESULT=($(gst-discoverer-1.0 $FULL_FILE_PATH | grep JPEG))
    if [ -n "$RESULT" ]; then
        echo "$TEST_CASE_NAME test pass"
        touch $TEST_RESULT_PATH/$TEST_CASE_NAME.pass
    else
        echo "$TEST_CASE_NAME test failed, file is not empty, bug it is not a JPEG file"
        touch $TEST_RESULT_PATH/$TEST_CASE_NAME.failed
    fi
else
    echo "$TEST_CASE_NAME test failed, file not exist, or it's empty"
    touch $TEST_RESULT_PATH/$TEST_CASE_NAME.failed
fi

#wait all child process done
wait
