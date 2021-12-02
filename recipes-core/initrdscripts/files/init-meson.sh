#!/bin/sh

PATH=/sbin:/bin:/usr/sbin:/usr/bin

ROOT_MOUNT="/rootfs"
ROOT_ROMOUNT="/rom"
ROOT_RWMOUNT="/data/overlay"
INIT="/sbin/init"
ROOT_DEVICE="/dev/system"
ROOT_RWDEVICE="/dev/data"
MOUNT="/bin/mount"
UMOUNT="/bin/umount"
FIRMWARE=""
VENDOR_DEVICE="/dev/vendor"
DM_VERITY_STATUS="disabled"
DM_DEV_COUNT=0
ACTIVE_SLOT=""
root_fstype="ext4"

# Copied from initramfs-framework. The core of this script probably should be
# turned into initramfs-framework modules to reduce duplication.
udev_daemon() {
	OPTIONS="/sbin/udev/udevd /sbin/udevd /lib/udev/udevd /lib/systemd/systemd-udevd"

	for o in $OPTIONS; do
		if [ -x "$o" ]; then
			echo $o
			return 0
		fi
	done

	return 1
}

_UDEV_DAEMON=`udev_daemon`

early_setup() {
    mkdir -p /proc
    mkdir -p /sys
    mount -t proc proc /proc
    mount -t sysfs sysfs /sys
    mount -t devtmpfs none /dev

    mkdir -p /run
    mkdir -p /var/run

    $_UDEV_DAEMON --daemon
    udevadm trigger --action=add
}

read_args() {
    [ -z "$CMDLINE" ] && CMDLINE=`cat /proc/cmdline`
    for arg in $CMDLINE; do
        optarg=`expr "x$arg" : 'x[^=]*=\(.*\)'`
        case $arg in
            root=*)
                ROOT_DEVICE=$optarg ;;
            rootfstype=*)
                root_fstype=$optarg
                modprobe $optarg 2> /dev/null ;;
            androidboot.slot_suffix=*)
                ACTIVE_SLOT=$optarg ;;
            LABEL=*)
                label=$optarg ;;
            video=*)
                video_mode=$arg ;;
            vga=*)
                vga_mode=$arg ;;
            console=*)
                if [ -z "${console_params}" ]; then
                    console_params=$arg
                else
                    console_params="$console_params $arg"
                fi ;;
            firmware=*)
                FIRMWARE=$optarg ;;
            init=*)
                init=$optarg ;;
            debugshell*)
                if [ -z "$optarg" ]; then
                        shelltimeout=30
                else
                        shelltimeout=$optarg
                fi
        esac
    done
}

#on read-only rootfs, .autorelabel can not be deleted, so move this file under /data, in this situation,
#if do factory reset, selinux relabel will happen again
selinux_relabel() {
    if [ -e $ROOT_MOUNT/read-only ]; then
        if [ ! -e $ROOT_MOUNT/data/.autorelabel ];then
            echo "selinux relabel"
            touch $ROOT_MOUNT/data/.autorelabel
            chroot ${ROOT_MOUNT} /sbin/setfiles -F /etc/selinux/standard/contexts/files/file_contexts /data
            #chroot ${ROOT_MOUNT} /sbin/setfiles -F /etc/selinux/standard/contexts/files/file_contexts /etc/
            chroot ${ROOT_MOUNT} /sbin/setfiles -F /etc/selinux/standard/contexts/files/file_contexts /data/etc/machine-id
            #chroot ${ROOT_MOUNT} /sbin/setfiles -F /etc/selinux/standard/contexts/files/file_contexts /lost+found
            chroot ${ROOT_MOUNT} /sbin/setfiles -F /etc/selinux/standard/contexts/files/file_contexts /data/.autorelabel
        fi
    else
        if [ -f ${ROOT_MOUNT}/.autorelabel ]; then
            echo "selinux relabel"
            chroot ${ROOT_MOUNT} /sbin/setfiles -F /etc/selinux/standard/contexts/files/file_contexts /data
            chroot ${ROOT_MOUNT} /sbin/setfiles -F /etc/selinux/standard/contexts/files/file_contexts /etc/
            chroot ${ROOT_MOUNT} /sbin/setfiles -F /etc/selinux/standard/contexts/files/file_contexts /etc/machine-id
            chroot ${ROOT_MOUNT} /sbin/setfiles -F /etc/selinux/standard/contexts/files/file_contexts /lost+found

            rm ${ROOT_MOUNT}/.autorelabel
        fi
    fi
}

check_set_machine_id() {
    if [ -e $ROOT_MOUNT/read-only ]; then
        if [ ! -e ${ROOT_MOUNT}/data/etc/machine-id ];then
            mkdir -p $ROOT_MOUNT/data/etc
            if [ -f  ${ROOT_MOUNT}/etc/machine-id ]; then
                cp ${ROOT_MOUNT}/etc/machine-id $ROOT_MOUNT/data/etc/
            else
                touch $ROOT_MOUNT/data/etc/machine-id
            fi
        fi
        mount --bind $ROOT_MOUNT/data/etc/machine-id ${ROOT_MOUNT}/etc/machine-id
    fi

    if [ -f  ${ROOT_MOUNT}/etc/machine-id ]; then
        mid=$(cat ${ROOT_MOUNT}/etc/machine-id)
    fi
    if [ -z "$mid" ]; then
        mid=$(cat /proc/cpuinfo | grep "^Serial" | md5sum | awk '{print $1}')
        echo $mid > ${ROOT_MOUNT}/etc/machine-id
    fi
}

boot_root() {
    # Watches the udev event queue, and exits if all current events are handled
    udevadm settle

    check_set_machine_id
    # The rootfs does not yet contain kernel modules.  Copy it!
    if [ ! -d ${ROOT_MOUNT}/lib/modules ];
    then
        cp -rf /lib/modules ${ROOT_MOUNT}/lib/
        cp -rf /lib/firmware ${ROOT_MOUNT}/lib/
        cp -rf /etc/modprobe.d ${ROOT_MOUNT}/etc/
        cp -rf /etc/modules-load.d ${ROOT_MOUNT}/etc/
        cp -rf /etc/modules ${ROOT_MOUNT}/etc/
	fi

    mount -n --move /proc ${ROOT_MOUNT}/proc
    mount -n --move /sys ${ROOT_MOUNT}/sys
    mount -n --move /dev ${ROOT_MOUNT}/dev

    if [ ! -e $ROOT_MOUNT/read-only ] && [ "${ACTIVE_SLOT}" != "" ]; then
        slot=$(cat ${ROOT_MOUNT}/etc/fstab | grep -E "/dev/vendor" | awk '{print $1}' | cut -c 12-)
        if [ "${ACTIVE_SLOT}" != "${slot}" ]; then
            echo "switch vendor${slot} to vendor${ACTIVE_SLOT}"
            sed -i "s/vendor\\${slot}/vendor\\${ACTIVE_SLOT}/" ${ROOT_MOUNT}/etc/fstab
        fi
    fi

    selinux_relabel

    cd $ROOT_MOUNT

    # busybox switch_root supports -c option
    exec switch_root -c /dev/console $ROOT_MOUNT $INIT ||
        fatal "Couldn't switch_root, dropping to shell"
}

fatal() {
    echo $1 >$CONSOLE
    echo >$CONSOLE
    exec sh
}

early_setup

[ -z "$CONSOLE" ] && CONSOLE="/dev/console"

read_args

#Waiting for device to become ready

wait_for_device () {
    i=1
    while [ "$i" -le 30 ]
    do
        if [ -b "${ROOT_DEVICE}" ]; then
            echo "${ROOT_DEVICE} is ready now."
            break
        fi
        echo "${ROOT_DEVICE} is not ready.  Waited for ${i} second"
        sleep 1
        i=$((i+1))
    done
}

format_and_install() {
    if [ -f "/${ROOT_MOUNT}/${FIRMWARE}" ] ; then
        echo "formating file system"
        export LD_LIBRARY_PATH=/usr/lib
        umount /dev/system
        mkfs.ext4 -F /dev/system
        mkdir -p system
    	if ! mount -o rw,noatime,nodiratime -t ext4 /dev/system /system ; then
		fatal "Could not mount system device"
    	fi
        echo "extracting file system ..."
        gunzip -c /${ROOT_MOUNT}/${FIRMWARE} | tar -xf - -C /system
        if [ $? -ne 0 ]; then
            echo "Error: untar failed."
        else
            echo "Done"
        fi
        device=/dev/boot
        if [ -f "/${ROOT_MOUNT}/boot.img" ]; then
            echo "Writing boot.img into boot partition(${device})..."
            dd if=/${ROOT_MOUNT}/boot.img of=${device}
            echo "Done"
        fi
        sync
        echo "copying existing modules to rootfs"
        cp -rf /lib/modules /system/lib/
        cp -rf /lib/firmware /system/lib/
        cp -rf /etc/modprobe.d /system/etc/
        cp -rf /etc/modules-load.d /system/etc/
        cp -rf /etc/modules /system/etc/
        echo "update complete"
        umount $ROOT_MOUNT
        ROOT_DEVICE=/dev/system
        ROOT_MOUNT=/system
    else
        echo "cannot locate ${FIRMWARE}"
        echo "boot normally..."
    fi
}

data_ext4_handle() {
	echo -e "Partition formater on $ROOT_RWDEVICE"
	FsType=$(blkid $ROOT_RWDEVICE | sed -n 's/.*TYPE=\"\([^\"]*\)\".*/\1/p')
	if [ "${FsType}" != "ext4" ]; then
		echo -e "Formating $ROOT_RWDEVICE to ext4 ..."
		yes 2>/dev/null | mkfs.ext4 -q -m 0 $ROOT_RWDEVICE
		sync
		FsType=$(blkid $ROOT_RWDEVICE | sed -n 's/.*TYPE=\"\([^\"]*\)\".*/\1/p')
		echo -e "After formating FSTYPE of $ROOT_RWDEVICE = ${FsType} ..."
	else
		echo -e "FSTYPE of $ROOT_RWDEVICE is already ext4 ..."
		FactoryReset=$(uenv get factory-reset | grep value | cut -d '[' -f2|cut -d ']' -f1)
		if [ ${FactoryReset} == 1 ]; then
		   echo -e "factory reset, Formating $ROOT_RWDEVICE to ext4 ..."
		   yes 2>/dev/null | mkfs.ext4 -q -m 0 $ROOT_RWDEVICE
		   sync
		   uenv set factory-reset 0
		fi
	fi

	[ ! -d $1 ]&&mkdir -p $1
	if ! mount -t ext4 -o rw,noatime,nodiratime $ROOT_RWDEVICE $1 ; then
		fatal "Could not mount $ROOT_RWDEVICE"
	fi
}

dm_verity_setup() {
    echo "setup dm-verity for ${1} partition(${2}) mount to ${3}"
    if [ -f /usr/share/${1}-dm-verity.env ]; then
        . /usr/share/${1}-dm-verity.env
        veritysetup --data-block-size=${DATA_BLOCK_SIZE} --hash-offset=${DATA_SIZE} \
            create ${1} ${2} ${2} ${ROOT_HASH}
        if [ $? = 0 ]; then
            if [ "${3}" != "none" ]; then
                #mount -o ro /dev/mapper/${1} ${3}
                mount -o ro /dev/dm-${DM_DEV_COUNT} ${3}
            else
                echo "skip mounting ${2}"
            fi
            DM_VERITY_STATUS="enabled"
            DM_DEV_COUNT=$((DM_DEV_COUNT+1))
        else
            echo "dm-verity fails with return code $?"
            DM_VERITY_STATUS="disabled"
        fi
    else
        echo "Cannot find root hash in initramfs"
        DM_VERITY_STATUS="disabled"
    fi
}

# Try to mount the root image read-write and then boot it up.
# This function distinguishes between a read-only image and a read-write image.
# In the former case (typically an iso), it tries to make a union mount if possible.
# In the latter case, the root image could be mounted and then directly booted up.
mount_and_boot() {
    mkdir $ROOT_MOUNT
    mknod /dev/loop0 b 7 0 2>/dev/null

if [ "${root_fstype}" = "ubifs" ]; then
    ubi_rootfs_mount
    ubi_vendor_attach
else
    if [ "${FIRMWARE}" != "" ];
    then
        ROOT_DEVICE="/dev/mmcblk1p1"
    fi

    wait_for_device

	if [ "$ROOT_DEVICE" != "" ];
	then
		dm_verity_setup system ${ROOT_DEVICE} ${ROOT_MOUNT}
		dm_verity_setup vendor ${VENDOR_DEVICE} none
		echo "dm-verity is $DM_VERITY_STATUS"
		if [ "$DM_VERITY_STATUS" = "disabled" ]; then
			if ! mount -o ro,noatime,nodiratime $ROOT_DEVICE $ROOT_MOUNT ; then
				fatal "Could not mount rootfs device"
			fi
		fi
	fi

	if [ "${FIRMWARE}" != "" ]; then
		format_and_install
	fi
fi

    if touch $ROOT_MOUNT/bin 2>/dev/null || [ -e $ROOT_MOUNT/read-only ]; then
		# The root image is read-write, directly boot it up.
        echo "read-only, skip overlay"
		data_ext4_handle $ROOT_MOUNT/data
		boot_root
    fi

    # determine which unification filesystem to use
    union_fs_type=""
    if grep -q -w "overlay" /proc/filesystems; then
	union_fs_type="overlay"
    elif grep -q -w "aufs" /proc/filesystems; then
	union_fs_type="aufs"
    else
	union_fs_type=""
    fi

    # make a union mount if possible
    case $union_fs_type in
	"overlay")
	    mkdir -p $ROOT_ROMOUNT
	    if ! mount -n --move $ROOT_MOUNT $ROOT_ROMOUNT; then
		rm -rf $ROOT_ROMOUNT
		fatal "Could not move rootfs mount point"
	    else
        echo "mount overlay"
        if [ "${root_fstype}" = "ubifs" ]; then
            data_ubi_handle
        else
            data_ext4_handle /data
        fi
		mkdir -p $ROOT_RWMOUNT/upperdir $ROOT_RWMOUNT/work
		mount -t overlay overlay -o "lowerdir=$ROOT_ROMOUNT,upperdir=$ROOT_RWMOUNT/upperdir,workdir=$ROOT_RWMOUNT/work" $ROOT_MOUNT
		mkdir -p ${ROOT_MOUNT}/$ROOT_ROMOUNT $ROOT_MOUNT/data
		mount --move $ROOT_ROMOUNT ${ROOT_MOUNT}/$ROOT_ROMOUNT
		mount --move /data $ROOT_MOUNT/data
	    fi
	    ;;
	"aufs")
	    mkdir -p /rootfs.ro /rootfs.rw
	    if ! mount -n --move $ROOT_MOUNT /rootfs.ro; then
		rm -rf /rootfs.ro /rootfs.rw
		fatal "Could not move rootfs mount point"
	    else
		mount -t tmpfs -o rw,noatime,mode=755 tmpfs /rootfs.rw
		mount -t aufs -o "dirs=/rootfs.rw=rw:/rootfs.ro=ro" aufs $ROOT_MOUNT
		mkdir -p $ROOT_MOUNT/rootfs.ro $ROOT_MOUNT/rootfs.rw
		mount --move /rootfs.ro $ROOT_MOUNT/rootfs.ro
		mount --move /rootfs.rw $ROOT_MOUNT/rootfs.rw
	    fi
	    ;;
	"")
	    mount -t tmpfs -o rw,noatime,mode=755 tmpfs $ROOT_MOUNT/media
	    ;;
    esac

    # boot the image
    boot_root
}

ubi_rootfs_mount()
{
    system_mtd_number=$(cat /proc/mtd | grep  -E "system" | awk -F : '{print $1}' | grep -o '[0-9]\+')
    ubiattach /dev/ubi_ctrl -m ${system_mtd_number}

    if ! mount -t ubifs -o ro $ROOT_DEVICE $ROOT_MOUNT ; then
        fatal "Could not mount $ROOT_DEVICE"
    fi
}

ubi_vendor_attach()
{
    vendor_mtd_number=$(cat /proc/mtd | grep  -E "vendor" | awk -F : '{print $1}' | grep -o '[0-9]\+')
    ubiattach /dev/ubi_ctrl -m ${vendor_mtd_number}
}

data_ubi_handle()
{
  data_mtd_number=$(cat /proc/mtd | grep  -E "data" | awk -F : '{print $1}' | grep -o '[0-9]\+')

  if ! uenv get factory-reset | grep -q 'value:\[1\]'; then
    mount | grep 'data' && echo "Already mounted" && return 0
    #mount dir create
    if [ ! -d "/data" ]
    then
      mkdir /data
    fi

    # sure ubi vol exist or not
    ubiattach /dev/ubi_ctrl -m ${data_mtd_number}
    if [ -c "/dev/ubi2_0" ]
    then
      data_vol_name=`cat /sys/class/ubi/ubi2_0/name`
      if [ "${data_vol_name}" = "data" ]
      then
        mount -t ubifs /dev/ubi2_0 /data
        return 0
      fi
    fi
  else
    uenv set factory-reset 0
  fi

  #mount data
  ubidetach -p /dev/mtd${data_mtd_number}
  ubiformat -y /dev/mtd${data_mtd_number}
  ubiattach /dev/ubi_ctrl -m ${data_mtd_number}
  ubimkvol /dev/ubi2 -m -N data
  mount -t ubifs /dev/ubi2_0 /data
}

mount_and_boot
