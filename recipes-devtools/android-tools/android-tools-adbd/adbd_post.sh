#!/bin/sh

usb_net_ipconfig()
{
    ifconfig usb0 192.168.5.1 up
}

#the ep1&ep2 is created by adbd, adbd start need time, here use node check instead of sleep
# if adbd start complete, ep1&ep2 will create
i=1
while [ $i -le 20 ] && [ ! -f /dev/usb-ffs/adb/ep1 ]
do sleep 0.5;i=`expr $i + 1`;
done

if [ -f /etc/adb_udc_file ]; then
    echo $(cat /etc/adb_udc_file) > /sys/kernel/config/usb_gadget/amlogic/UDC
else
    echo ff400000.dwc2_a > /sys/kernel/config/usb_gadget/amlogic/UDC
fi
/usr/bin/usb_monitor &

usb_net_ipconfig

echo "------------------------------------"
echo "usb rndis & adb start: OK!"
echo "------------------------------------"
