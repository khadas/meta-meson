#!/bin/sh

source /usr/bin/sandbox-setup.env

for D in /data/persistent; do
  mkdir -p ${D}
  if [ -d "${D}" ];then
    chgrp $GROUP_SYSTEM "${D}"
    chmod g+rw "${D}"
  fi
done

echo 0 > /sys/module/drm/parameters/vblankoffdelay
echo 1 > /sys/module/aml_drm/parameters/video_axis_zoom

