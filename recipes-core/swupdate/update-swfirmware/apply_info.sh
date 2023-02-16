#!/bin/sh
SWUPDATE_PATH=$1/swupdate

cp $SWUPDATE_PATH/etc/* /etc  -a
cp $SWUPDATE_PATH/lib/* /lib  -a

