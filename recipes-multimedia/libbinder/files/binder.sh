#!/bin/sh

ln -sf /dev/binderfs/binder /dev/binder
chgrp system /dev/binder
chmod g+rw /dev/binder
