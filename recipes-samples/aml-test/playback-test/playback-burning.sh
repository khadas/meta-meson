#!/bin/bash



if [ $# == 1 ];then
    LOOP_CNT=$1
else
    LOOP_CNT=1000000
fi


if [ $# == 0 ];then # no parameters
	echo "please select a media file"
else
    MEDIA_FILE=$1
fi

i=1

while [ $i -le  $LOOP_CNT ]
do
    echo "loop i="$i

    ./playback-test.sh $MEDIA_FILE

    # check system memory information
    echo "free" && free
    echo "cat /proc/meminfo" && cat /proc/meminfo
    echo "vmstat -s" && vmstat -s

    let i++
done


if [ $i -le  $LOOP_CNT ]; then
    echo "burning test failed, total=$LOOP_CNT, current=$i"
else
    echo "burning test pass, total=$LOOP_CNT, current=$i"
fi

