#!/bin/bash


DEVICE="video71"

WIDTH=1920
HEIGHT=1080

if [ $# == 2 ];then
    WIDTH=$1
    HEIGHT=$2
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

    ls /dev/media*
    ls /dev/video*
}

display_help

SINK=
if [ -d /etc/gdm3 ]; then
    # gnome
    SINK=clutterautovideosink
else
    SINK=glimagesink
fi

CMD="gst-launch-1.0 -v v4l2src device=/dev/$DEVICE io-mode=dmabuf ! video/x-raw,width=$WIDTH,height=$HEIGHT,format=NV12,framerate=30/1 ! videoconvert ! $SINK"
echo $CMD
eval $CMD

