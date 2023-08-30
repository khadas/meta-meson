#!/bin/sh

source /usr/bin/sandbox-setup.env

for D in /data/persistent; do
  mkdir -p ${D}
  if [ -d "${D}" ];then
    chgrp $GROUP_SYSTEM "${D}"
    chmod g+rw "${D}"
  fi
done

if [ -f "/sys/module/aml_media/parameters/hdr_policy" ];then
  chgrp $GROUP_SYSTEM "/sys/module/aml_media/parameters/hdr_policy"
fi
if [ -f "/sys/module/aml_media/parameters/dolby_vision_policy" ];then
  chgrp $GROUP_SYSTEM "/sys/module/aml_media/parameters/dolby_vision_policy"
fi

echo 0 > /sys/module/drm/parameters/vblankoffdelay
echo 1 > /sys/module/aml_drm/parameters/video_axis_zoom

