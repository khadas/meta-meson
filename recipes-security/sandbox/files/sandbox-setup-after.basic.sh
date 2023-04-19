#!/bin/sh

source /usr/bin/sandbox-setup.env

chgrp $GROUP_SYSTEM /data
chmod g+rw /data

if [ -c /dev/nand_env ];then
  chgrp disk /dev/nand_env
  chmod g+rw /dev/nand_env
fi

chgrp $GROUP_SYSTEM /run
chmod g+rw /run

chgrp -R $GROUP_SYSTEM /dev/v4l/
chmod -R g+rw /dev/v4l/

