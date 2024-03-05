#!/bin/sh

chmod 660 /dev/binderfs/binder
ln -sf /dev/binderfs/binder /dev/binder
chgrp system /dev/binder
chmod g+rw /dev/binder
