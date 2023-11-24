#!/bin/sh

mkdir -p /dev/binderfs
mount -t binder binder /dev/binderfs
ln -sf /dev/binderfs/binder /dev/binder
