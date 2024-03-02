#!/bin/bash

datename=$(date +%Y%m%d)-$(date +%H%M%S)

file_ext=

if [ $# == 0 ];then # no parameters, 
	file_ext=$datename
else
	file_ext=$1
fi


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


EXEC_CMD "ps aux "
EXEC_CMD "top -b -n 5 "
EXEC_CMD "top -b -H -n 5"
EXEC_CMD "df -H "



