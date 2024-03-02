#!/bin/bash


FILE_EXT="jpg"

DEVICE="video12"
SRC_WIDTH=1920
SRC_HEIGHT=1080
SRC_FORMAT="RGB"
SRC_FRAMERATE="30/1"
IO_MODE="mmap"
TVIN_PORT="0x11000001"

if [ $# == 7 ];then
    DEVICE=$1
    SRC_WIDTH=$2
    SRC_HEIGHT=$3
    SRC_FORMAT=$4
    SRC_FRAMERATE=$5
    IO_MODE=$6
    TVIN_PORT=$7
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
    Screen recorder to a jpeg file

    EXAMPLES:
	*.sh video12 1920 1080 RGB 30/1 mmap 0x11000001
    *.sh  
	"
}

display_help


if [ $IO_MODE == "dmabuf" ];then
    USE_IO_MODE=
else
    USE_IO_MODE="io-mode=$IO_MODE"
fi


#  tvin-port=0x11000000  //vpp0 video only;
#  tvin-port=0x11000001  //vpp0(osd+video);
#  tvin-port=0x11000002  //vpp1 video only; 副屏video only
#  tvin-port=0x11000003  // vpp1(osd+video); 副屏osd+video
if [ $TVIN_PORT == "0x11000001" ];then
    USE_TVIN_PORT=
else
    USE_TVIN_PORT="tvin-port=$TVIN_PORT"
fi


CMD="gst-launch-1.0 -v v4l2src device=/dev/$DEVICE $USE_IO_MODE num-buffers=1 $USE_TVIN_PORT ! video/x-raw,width=$SRC_WIDTH,height=$SRC_HEIGHT,format=$SRC_FORMAT,framerate=$SRC_FRAMERATE,interlace-mode=interleaved ! amljpegenc ! filesink location=$FULL_FILE_PATH"
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
