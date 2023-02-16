#!/bin/sh

depmod
systemctl start wifi.service
systemctl start wpa_supplicant.service
sleep 3
