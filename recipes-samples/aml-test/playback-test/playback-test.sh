#!/bin/bash


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
	please run with a mediafile full path as parameters
	*.sh full-file-path
	"
}


SINK=
if [ -d /etc/gdm3 ]; then
    # gnome
    SINK=clutterautovideosink
else
	# playback should use waylandsink
    SINK=waylandsink
fi


if [ $# == 0 ];then # no parameters
	display_help
else
    MEDIA_FILE=$1

    CMD="gst-play-1.0 --videosink=$SINK $MEDIA_FILE"
	echo $CMD
	eval $CMD
fi

#wait all child process done
wait
