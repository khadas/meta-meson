#!/bin/sh

if [ $# -ge 1 ]; then
	echo -e "Partition formater on /dev/$1"
	FsType=$(blkid /dev/$1 | sed -n 's/.*TYPE=\"\([^\"]*\)\".*/\1/p')
	if [ "${FsType}" != "ext4" ]; then
		echo -e "Formating /dev/$1 to ext4 ..."
		yes 2>/dev/null | mkfs.ext4 -q -m 0 /dev/$1
		sync
		FsType=$(blkid /dev/$1 | sed -n 's/.*TYPE=\"\([^\"]*\)\".*/\1/p')
		echo -e "After formating FSTYPE of /dev/$1 = ${FsType} ..."
	else
		echo -e "FSTYPE of /dev/$1 is already ext4 ..."
	fi
else
	echo -e "Error !!! Partition formater got no args ..."
fi

