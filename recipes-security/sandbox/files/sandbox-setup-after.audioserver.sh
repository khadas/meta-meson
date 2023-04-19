#!/bin/sh

source /usr/bin/sandbox-setup.env

for F in /dev/shm/AudioServiceShmem /run/audio_socket; do
  if [ -e "${F}" ];then
    chgrp $GROUP_SESSION "${F}"
    chmod g+rw "${F}"
  fi
done
