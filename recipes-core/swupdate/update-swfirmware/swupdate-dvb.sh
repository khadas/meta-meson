#!/bin/sh

SWUPDATE_PATH=/data/swupdate

rm -rf $SWUPDATE_PATH
mkdir -p $SWUPDATE_PATH

#{"sys":0,"freq":794000000,"qam":3,"symb":6875000}
OTA_URI=$1

echo "$OTA_URI" > $SWUPDATE_PATH/enable-dvb-ota

#urlmisc write "$OTA_URI"
reboot recovery
