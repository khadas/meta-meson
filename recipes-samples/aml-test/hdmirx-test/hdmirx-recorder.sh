#!/bin/bash


DEVICE="video71"
FILE_EXT="mp4"

WIDTH=1920
HEIGHT=1080
PARSE_TYPE="h264parse"

if [ $# == 3 ];then
    WIDTH=$1
    HEIGHT=$2
    PARSE_TYPE=$3"parse"
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
    Please make sure plugin HDMI rx device

    EXAMPLES:
	*.sh 
	"

    ls /dev/video*
}

display_help


MEDIA_FILE=$1


# dmabuf
# MMAP

# 10s, 1920*1080 30fps
CMD="gst-launch-1.0 -v v4l2src device=/dev/$DEVICE io-mode=dmabuf num-buffers=300 ! video/x-raw,width=$WIDTH,height=$HEIGHT,format=NV12,framerate=30/1 ! amlvenc ! $PARSE_TYPE ! qtmux ! filesink location=$FULL_FILE_PATH"
echo $CMD
eval $CMD

if [ -s $FULL_FILE_PATH ]; then 
        echo "$TEST_CASE_NAME test pass"
        touch $TEST_RESULT_PATH/$TEST_CASE_NAME.pass

        echo "Start play the recorded file :"$FULL_FILE_PATH
        ../playback-test/playback-test.sh $FULL_FILE_PATH
else
        echo "$TEST_CASE_NAME test failed"
        touch $TEST_RESULT_PATH/$TEST_CASE_NAME.failed
fi
