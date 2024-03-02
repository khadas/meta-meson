#!/bin/bash

## Display parameters
display_help() {
	echo "\
	***************************HELP**************************
    set performance mode on/off

    EXAMPLES:
	*.sh on
	*.sh ON
	*.sh off
	*.sh OFF
	"
}


param=$1
ON_OFF=${param,,}

echo "Set Performance mode :"$ON_OFF


## notice about need sudo
need_sudo(){
if [ "$sudo" ]; then
    info_msg "[${FUNCNAME[1]}] This script requires root privileges, trying to use sudo, please enter your password!"
    sudo true || exit
    info_msg "sudo works..."
fi
}
# make sure you have root privileges
need_sudo || true


#####################################################################################
# CPU
echo "before set performance mode, print CPU information:"
cat /sys/devices/system/cpu/cpufreq/policy0/cpuinfo_cur_freq
cat /sys/devices/system/cpu/cpufreq/policy0/scaling_available_frequencies
cat /sys/devices/system/cpu/cpufreq/policy0/scaling_max_freq
cat /sys/devices/system/cpu/cpufreq/policy0/scaling_governor
cat /sys/class/thermal/thermal_zone0/mode
cat /proc/sys/kernel/printk

if [[ $ON_OFF == on ]]; then
    # disable DVFS, use high performance mode
    echo performance > /sys/devices/system/cpu/cpufreq/policy0/scaling_governor
    # disable thermal control
    echo disabled > /sys/class/thermal/thermal_zone0/mode
    # disable kernel log
    echo 0 >/proc/sys/kernel/printk
elif [[ $ON_OFF == off ]]; then
    # disable DVFS, use high performance mode
    echo conservative > /sys/devices/system/cpu/cpufreq/policy0/scaling_governor
    # disable thermal control
    echo enabled > /sys/class/thermal/thermal_zone0/mode
    # disable kernel log
    echo 4 >/proc/sys/kernel/printk
else
    display_help
fi

echo "after set performance mode, print CPU information:"
cat /sys/devices/system/cpu/cpufreq/policy0/cpuinfo_cur_freq
cat /sys/devices/system/cpu/cpufreq/policy0/scaling_available_frequencies
cat /sys/devices/system/cpu/cpufreq/policy0/scaling_max_freq
cat /sys/devices/system/cpu/cpufreq/policy0/scaling_governor
cat /sys/class/thermal/thermal_zone0/mode
cat /proc/sys/kernel/printk
#####################################################################################


#####################################################################################
# memory cache free
# To free pagecache:
echo 1 > /proc/sys/vm/drop_caches

#To free reclaimable slab objects (includes dentries and inodes):
echo 2 > /proc/sys/vm/drop_caches

#To free slab objects and pagecache:
echo 3 > /proc/sys/vm/drop_caches
#####################################################################################

