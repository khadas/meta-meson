#!/bin/bash



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



EXEC_CMD() {
	CMD=$1
	echo "Command:"$CMD
	eval $CMD
}


EXEC_CMD "sync"
EXEC_CMD "echo 3 > /proc/sys/vm/drop_caches"
EXEC_CMD "echo 3 > /proc/sys/vm/drop_caches"
EXEC_CMD "echo 3 > /proc/sys/vm/drop_caches"
EXEC_CMD "echo 3 > /proc/sys/vm/drop_caches"



#summary
EXEC_CMD "free -hm"
EXEC_CMD "free"

EXEC_CMD "cat /proc/sys/vm/min_free_kbytes"


EXEC_CMD "cat /proc/meminfo"
EXEC_CMD "cat /proc/zoneinfo"
EXEC_CMD "cat /proc/buddyinfo"
EXEC_CMD "cat /proc/slabinfo"
EXEC_CMD "cat /proc/vmstat"

vmstat

#Kernel memory:
EXEC_CMD "cat /proc/pagetrace"


EXEC_CMD "cat /sys/kernel/debug/dma_buf/bufinfo"
EXEC_CMD "cat /sys/class/codec_mm/codec_mm_dump"

EXEC_CMD "cat /proc/vmallocinfo"

EXEC_CMD "procrank"
EXEC_CMD "ps_mem"



