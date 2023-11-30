#!/bin/sh

PATH=/sbin:/bin:/usr/sbin:/usr/bin

ROOT_MOUNT="/rootfs"
ROOT_ROMOUNT="/rom"
#ROOT_RWMOUNT="/data/overlay"
INIT="/sbin/init"
ROOT_DEVICE="/dev/system"
ROOT_RWDEVICE="/dev/data"
MOUNT="/bin/mount"
UMOUNT="/bin/umount"
FIRMWARE=""
VENDOR_DEVICE="/dev/vendor"
DM_VERITY_STATUS_SYSTEM="disabled"
DM_VERITY_STATUS_VENDOR="disabled"
DM_DEV_COUNT=0
ACTIVE_SLOT=""
root_fstype="ext4"
OVERLAY_DIR="/data/overlay"
UNENCRYPTED_DIR="/data/unencrypted"
SWUPDATE_DIR="/data/swupdate"
VBMETA_DEVICE=""
OverlayFS="enabled"

data_fbe_keyblob="fbe_keyblob"
data_fbe_root_dir=""
keyring_key_id=""
DATA_FBE_STATUS="disabled"

load_ramdisk_ko() {
    if [ -f /modules/ramdisk/ramdisk_install.sh ]; then
        cd /modules/ramdisk
        /modules/ramdisk/ramdisk_install.sh
        cd -
    fi
}

early_setup() {
    mkdir -p /proc
    mkdir -p /sys
    mount -t proc proc /proc
    mount -t sysfs sysfs /sys
    mount -t devtmpfs none /dev

    mkdir -p /run
    mkdir -p /var/run

    # load ramidsk ko for kernel 5.15
    #load_ramdisk_ko

    # wait gpt partition ready and map to /dev/name
    #upstream_emmc_mount
}

wait_for_emmc_partition () {
    i=1
    while [ "$i" -le 30 ]
    do
        if [  -d /sys/block/mmcblk0/mmcblk0p1/ ]; then
            echo "mmcblk0p1 ready"
            break
        fi

        echo "mmcblk0p1 is not ready.  Waited for 50ms"
        sleep 0.05
        i=$((i+1))
    done
}

upstream_emmc_mount() {
  echo " do upstream emmc mount"
  if [ ! -f /dev/system ];then
    echo "/dev/system not exist!"
    wait_for_emmc_partition
    if [  -d /sys/block/mmcblk0/mmcblk0p1/ ]; then
      cd /sys/block/mmcblk0/
      for part in `ls | grep mmcblk0p`
      do
        cd $part
#        major=`cat uevent | grep MAJOR= | sed 's/.*=//'`
#        minor=`cat uevent | grep MINOR= | sed 's/.*=//'`
        partname=`cat uevent | grep PARTNAME= | sed 's/.*=//'`
#        echo "part: $part, partname $partname, major $major, minor: $minor"
#        mknod /dev/$partname b $major $minor
        ln -s /dev/$part  /dev/$partname
        cd ..
      done
    fi
    cd ~

  else
    echo "/dev/system exist!"
  fi
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
                ACTIVE_SLOT=$optarg
                ROOT_DEVICE=${ROOT_DEVICE}${ACTIVE_SLOT};;
            androidboot.vbmeta.device=*)
                VBMETA_DEVICE=$optarg ;;
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
    if [ "$OverlayFS" = "disabled" ]; then
        if [ ! -e $ROOT_MOUNT/data/.autorelabel ] && [ -f /sbin/setfiles ]; then
            echo "selinux relabel"
            touch $ROOT_MOUNT/data/.autorelabel
            chroot ${ROOT_MOUNT} /sbin/setfiles -F /etc/selinux/standard/contexts/files/file_contexts /data
        fi
    else
        if [ -f ${ROOT_MOUNT}/.autorelabel ]; then
            echo "selinux relabel"
            relabel_list="/data /etc"
            if [ -d /$ROOT_MOUNT/lost+found ]; then
                relabel_list="${relabel_list} /lost+found"
            fi
            chroot ${ROOT_MOUNT} /sbin/setfiles -F /etc/selinux/standard/contexts/files/file_contexts ${relabel_list}
            rm ${ROOT_MOUNT}/.autorelabel
        fi
    fi
}

check_set_machine_id() {
    if [ "$OverlayFS" = "disabled" ]; then
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
    check_set_machine_id
    # The rootfs does not yet contain kernel modules.  Copy it!
    if [ ! -d ${ROOT_MOUNT}/lib/modules ]; then
        cp -rf /lib/modules ${ROOT_MOUNT}/lib/
        cp -rf /lib/firmware ${ROOT_MOUNT}/lib/
        cp -rf /etc/modprobe.d ${ROOT_MOUNT}/etc/
        cp -rf /etc/modules-load.d ${ROOT_MOUNT}/etc/
        cp -rf /etc/modules ${ROOT_MOUNT}/etc/
    fi

    mount -n --move /proc ${ROOT_MOUNT}/proc
    mount -n --move /sys ${ROOT_MOUNT}/sys
    mount -n --move /dev ${ROOT_MOUNT}/dev

    if [ "$DM_VERITY_STATUS_VENDOR" = "disabled" ] && [ "${ACTIVE_SLOT}" != "" ]; then
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

# fbe_check_modules
# Search for necessary external modules for FBE.
# In case of builtin modules, nothing should be done.
# Input: N/A
# Return: N/A
fbe_check_modules() {
    modules=""
    # If /dev/tee0 is up, it seems the optee.ko and optee_armtz.ko
    # are already builtin.
    if [[ ! -e /dev/tee0 ]]; then
        echo "FBE: /dev/tee0 is not up. Trying to insert optee.ko optee_armtz.ko."
        modules="optee.ko optee_armtz.ko"
    fi
    # If there is /proc/config.gz, it might be able to find some clues in it.
    if [[ -e /proc/config.gz ]]; then
        cat /proc/config.gz | gunzip | grep CONFIG_TRUSTED_KEYS=y > /dev/null 2>&1
        is_builtin1=$?
        cat /proc/config.gz | gunzip | grep CONFIG_TRUSTED_KEYS_TEE=y > /dev/null 2>&1
        is_builtin2=$?
        if [[ $is_builtin1 -ne 0 ]] || [[ $is_builtin2 -ne 0 ]]; then
            modules="$modules trusted.ko"
        fi
    else
        modules="$modules trusted.ko"
    fi
    if [[ -n "$modules" ]]; then
        echo "searching for $modules"
        for mod in $modules; do
            module_path=`find ${ROOT_ROMOUNT}/lib/modules/ -name $mod`
            if [ "$module_path" != "" ]; then
                insmod $module_path
                if [ $? -ne 0 ]; then
                    echo "FBE: Cannot insert $mod."
                fi
            else
                echo "FBE: Cannot find $mod."
            fi
        done
    fi
}

#fbe_kernel_keyring_setup
# Input: $1: mount point of /dev/data
#        $2: newly formatted or not
# Return: 0: success
#         1: failed
#         2: not newly formatted and no sealed key found
#            (FBE is not enabled for current system)
fbe_kernel_keyring_setup() {
    if [ $2 -ne 0 ]; then
        echo -n "FBE: Found newly formatted data partition. Creating Data FBE Key ... "
        keyring_key_id=`keyctl add trusted data_fbe_key "new 64" @u`
    else
        if [ -f $1/$data_fbe_keyblob ]; then
            echo -n "FBE: Sealed key found. Trying to add it to kernel keyring ..."
            sealed_key=`cat $1/$data_fbe_keyblob`
            keyring_key_id=`keyctl add trusted data_fbe_key "load $sealed_key" @u`
        else
            echo "FBE: No sealed key found!"
            return 2
        fi
    fi
    #echo "keyring_key_id = $keyring_key_id"
    if expr "$keyring_key_id" + 0  > /dev/null 2>&1; then
        echo "[ Success ]"
        return 0
    else
        echo "[ Failed ]"
        return 1
    fi
}

fbe_check_packages() {
    packages="keyctl fscryptctl"
    for p in $packages; do
        which $p 1> /dev/null
        if [ $? -ne 0 ]; then
            echo "FBE: Cannot find $p. It looks like DISTRO_FEATURE, FBE, is not enabled."
            echo "FBE: Skip setup."
            return 1
        fi
    done
    return 0
}

fbe_fscrypt_setup() {
    data_fbe_root_dir="fbe_root"

    [ ! -d $1/$data_fbe_root_dir ] && mkdir -p $1/$data_fbe_root_dir

    fscrypt_key_id=`echo $keyring_key_id | fscryptctl add_key_by_keyid $1/$data_fbe_root_dir`
    #echo "fscrypt_key_id= $fscrypt_key_id"

    echo -n "FBE: fscryptctl set_policy ... "
    fscryptctl set_policy $fscrypt_key_id $1/$data_fbe_root_dir
    if [ $? -ne 0 ]; then
        echo "[ Failed ]"
        return 1
    fi
    echo "[ Success ]"
    return 0
}

data_fbe_bind() {
    # param 1: data partition mount point
    DATA_MNT_LOC=$1
    if [ "$DATA_FBE_STATUS" = "setup" ]; then
        # When DATA_FBE_STATUS = setup, it means FBE has been setup on <data mount point>/fbe_root
        # We need to bind it to <root mount point>/data
        mkdir -p $ROOT_MOUNT/data
        mount --bind $DATA_MNT_LOC/$data_fbe_root_dir $ROOT_MOUNT/data
        if [ $? != 0 ]; then
            echo "FBE: Failed to bind $DATA_MNT_LOC/$data_fbe_root_dir to $ROOT_MOUNT/data"
            DATA_FBE_STATUS="disabled"
            return 1
        fi
        DATA_FBE_STATUS="bound"
        return 0
    else
        return 2
    fi
}

data_fbe_add_unencrypted_folders() {
    if [[ -z "$1" ]]; then
        echo "Invalid foler name provided"
        return
    fi
    # When DATA_FBE_STATUS == bound, it means FBE has been setup and bound to $ROOT_MOUNT/data
    if [[ "$DATA_FBE_STATUS" = "bound" ]]; then
        [[ ! -d $1 ]] && mkdir -p $1
        [[ ! -d $ROOT_MOUNT/$1 ]] && mkdir -p $ROOT_MOUNT/$1
        mount --bind $1 $ROOT_MOUNT/$1
        if [[ $? != 0 ]]; then
            echo "FBE: Failed to add $1 to $ROOT_MOUNT/$1"
            DATA_FBE_STATUS="Error add $1"
        fi
    fi
}

data_fbe_setup() {
    #echo "param: $1 $2"
    fbe_check_packages
    ret=$?
    if [ $ret -ne 0 ]; then
        return $ret
    fi

    fbe_kernel_keyring_setup $1 $2
    ret=$?
    if [ $ret -eq 1 ]; then
        fbe_check_modules
        fbe_kernel_keyring_setup $1 $2
    elif [[ $ret -eq 2 ]]; then
        return $ret
    fi

    if expr "$keyring_key_id" + 0  > /dev/null 2>&1; then
        fbe_fscrypt_setup $1
        ret=$?
        if [ $ret != 0 ]; then
            return $ret
        fi
        # Only store key blob in case of newly created key
        if [ $2 -ne 0 ]; then
            keyctl pipe $keyring_key_id > $1/$data_fbe_keyblob
            ret=$?
            sync
            if [ $ret -ne 0 ]; then
                echo "FBE: Store key blob failed($ret)"
                return $ret
            else
                echo "FBE: key blob saved as $1/$data_fbe_keyblob"
            fi
        fi
        DATA_FBE_STATUS="setup"
    else
        echo "FBE: Error: Invalid key id($keyring_key_id)"
    fi
}

data_ext4_handle() {
    echo -e "Partition formater on $ROOT_RWDEVICE"
    FsType=$(blkid $ROOT_RWDEVICE | sed -n 's/.*TYPE=\"\([^\"]*\)\".*/\1/p')
    newly_formatted=0
    if [ "${FsType}" != "ext4" ]; then
        echo -e "Formating $ROOT_RWDEVICE to ext4 ..."
        yes 2>/dev/null | mkfs.ext4 -q -m 0 -O encrypt $ROOT_RWDEVICE
        sync
        FsType=$(blkid $ROOT_RWDEVICE | sed -n 's/.*TYPE=\"\([^\"]*\)\".*/\1/p')
        echo -e "After formating FSTYPE of $ROOT_RWDEVICE = ${FsType} ..."
        newly_formatted=1
    else
        echo -e "FSTYPE of $ROOT_RWDEVICE is already ext4 ..."
        FactoryReset=$(uenv get factory-reset | grep value | cut -d '[' -f2|cut -d ']' -f1)
        if [ ${FactoryReset} == 1 ]; then
           echo -e "factory reset, Formating $ROOT_RWDEVICE to ext4 ..."
           yes 2>/dev/null | mkfs.ext4 -q -m 0 -O encrypt $ROOT_RWDEVICE
           sync
           uenv set factory-reset 0
        fi
    fi

    [ ! -d $1 ] && mkdir -p $1
    if ! mount -t ext4 -o rw,noatime,nodiratime $ROOT_RWDEVICE $1 ; then
        fatal "Could not mount $ROOT_RWDEVICE"
    fi

    data_fbe_setup $1 $newly_formatted
}

dm_verity_setup() {
    echo "setup dm-verity for ${1} partition(${2}) mount to ${3}"
    VERITY_ENV=/usr/share/${1}-dm-verity.env

    if [ "${root_fstype}" = "squashfs" ]; then
        vbmeta_mtd_number=$(cat /proc/mtd | grep -E "vbmeta" | awk -F : '{print $1}' | grep -o '[0-9]\+')
        VBMETA_DEVICE_REAL=/dev/mtdblock${vbmeta_mtd_number}
    else
        VBMETA_DEVICE_REAL=${VBMETA_DEVICE}${ACTIVE_SLOT}
        # Change /dev/block/ to /dev/
        if [ ! -b ${VBMETA_DEVICE_REAL} ]; then
            VBMETA_DEVICE_REAL=`echo ${VBMETA_DEVICE_REAL} | sed "s/\/block\//\//g"`
        fi
    fi

    echo "vbmeta device is $VBMETA_DEVICE_REAL"

    if [ -b "${VBMETA_DEVICE_REAL}" ]; then
        mkdir -p /tmp
        AVB_DM_TOOL_PY=/usr/bin/avbtool-dm-verity.py
        AVB_DM_TOOL_C=/usr/bin/aml-avb-dm-verity
        if [ -x "${AVB_DM_TOOL_PY}" ]; then
            AVB_DM_TOOL=${AVB_DM_TOOL_PY}
        fi
        if [ -x "${AVB_DM_TOOL_C}" ]; then
            AVB_DM_TOOL=${AVB_DM_TOOL_C}
        fi
        if [ -x "${AVB_DM_TOOL}" ]; then
            VERITY_ENV=/tmp/${1}-dm-verity.env
            ${AVB_DM_TOOL} print_partition_verity --image "${VBMETA_DEVICE_REAL}" --partition_name "${1}" --active_slot "${ACTIVE_SLOT}" --output "$VERITY_ENV"
            if [ "$?" != "0" ]; then
                echo "failed to read vbmeta device from ${VBMETA_DEVICE_REAL}"
            fi
        fi
    fi

    echo "verity env is $VERITY_ENV"

    if [ -f $VERITY_ENV ]; then
        . $VERITY_ENV
        veritysetup --data-block-size=${DATA_BLOCK_SIZE} --hash-offset=${DATA_SIZE} \
            create ${1} ${2} ${2} ${ROOT_HASH}
        if [ $? = 0 ]; then
            if [ "${3}" != "none" ]; then
                #mount -o ro /dev/mapper/${1} ${3}
                mount -o ro /dev/dm-${DM_DEV_COUNT} ${3}
            else
                echo "skip mounting ${2}"
            fi
            if [ "$1" = "system" ]; then
                DM_VERITY_STATUS_SYSTEM="enabled"
            elif [ "$1" = "vendor" ]; then
                DM_VERITY_STATUS_VENDOR="enabled"
            fi
            DM_DEV_COUNT=$((DM_DEV_COUNT+1))
        else
            echo "dm-verity fails with return code $?"
            if [ "$1" = "system" ]; then
                DM_VERITY_STATUS_SYSTEM="disabled"
            elif [ "$1" = "vendor" ]; then
                DM_VERITY_STATUS_VENDOR="disabled"
            fi
        fi
    else
        echo "Cannot find root hash in initramfs"
        if [ "$1" = "system" ]; then
            DM_VERITY_STATUS_SYSTEM="disabled"
        elif [ "$1" = "vendor" ]; then
            DM_VERITY_STATUS_VENDOR="disabled"
        fi
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
    elif [ "${root_fstype}" = "squashfs" ]; then
        squashfs_rootfs_mount
    else
        if [ "${FIRMWARE}" != "" ]; then
            ROOT_DEVICE="/dev/mmcblk1p1"
        fi

        wait_for_device

        if [ "$ROOT_DEVICE" != "" ]; then
            dm_verity_setup system ${ROOT_DEVICE} ${ROOT_MOUNT}
            dm_verity_setup vendor ${VENDOR_DEVICE}${ACTIVE_SLOT} none
            echo "dm-verity for system is $DM_VERITY_STATUS_SYSTEM"
            if [ "$DM_VERITY_STATUS_SYSTEM" = "disabled" ]; then
                if ! mount -o ro,noatime,nodiratime $ROOT_DEVICE $ROOT_MOUNT ; then
                    fatal "Could not mount rootfs device"
                fi
            fi
        fi

        if [ "${FIRMWARE}" != "" ]; then
            format_and_install
        fi
    fi

    if touch $ROOT_MOUNT/bin 2>/dev/null; then
        # The root image is read-write, directly boot it up.
        if [ "${root_fstype}" = "ext4" ]; then
            data_ext4_handle $ROOT_MOUNT/data
            data_fbe_bind $ROOT_MOUNT/data
            if [[ $? -eq 0 ]]; then
                if [[ -d $OVERLAY_DIR ]]; then
                    data_fbe_add_unencrypted_folders $OVERLAY_DIR
                fi
                data_fbe_add_unencrypted_folders $UNENCRYPTED_DIR
                data_fbe_add_unencrypted_folders $SWUPDATE_DIR
                [[ "$DATA_FBE_STATUS" = "bound" ]] && DATA_FBE_STATUS="enabled"
            fi
            echo "DATA FBE is: $DATA_FBE_STATUS!"
        else
            data_yaffs2_handle $ROOT_MOUNT/data
        fi
        boot_root
    fi

    # determine which unification filesystem to use
    union_fs_type=""
    if grep -q -w "overlay" /proc/filesystems; then
        union_fs_type="overlay"
    elif grep -q -w "aufs" /proc/filesystems; then
        union_fs_type="aufs"
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
                data_ubi_handle ubi2 /data
            elif [ "${root_fstype}" = "squashfs" ]; then
                data_ubi_handle ubi0 /data
            else
                data_ext4_handle /data
            fi
            mkdir -p $OVERLAY_DIR/upperdir $OVERLAY_DIR/work
            mount -t overlay overlay -o "lowerdir=$ROOT_ROMOUNT,upperdir=$OVERLAY_DIR/upperdir,workdir=$OVERLAY_DIR/work" $ROOT_MOUNT

            mkdir -p ${ROOT_MOUNT}/$ROOT_ROMOUNT
            mount --move $ROOT_ROMOUNT ${ROOT_MOUNT}/$ROOT_ROMOUNT
            data_fbe_bind /data
            if [[ $? -eq 0 ]]; then
                if [[ -d $OVERLAY_DIR ]]; then
                    data_fbe_add_unencrypted_folders $OVERLAY_DIR
                fi
                data_fbe_add_unencrypted_folders $UNENCRYPTED_DIR
                data_fbe_add_unencrypted_folders $SWUPDATE_DIR
                [[ "$DATA_FBE_STATUS" = "bound" ]] && DATA_FBE_STATUS="enabled"
            fi
            if [[ "$DATA_FBE_STATUS" = "disabled" ]]; then
                mkdir -p $ROOT_MOUNT/data
                mount --move /data $ROOT_MOUNT/data
            fi
            echo "DATA FBE is: $DATA_FBE_STATUS!"
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
        echo "OverlayFS is disabled"
        OverlayFS="disabled"
        if [ "${root_fstype}" = "ext4" ]; then
            data_ext4_handle $ROOT_MOUNT/data
        elif [ "${root_fstype}" = "ubifs" ]; then
            data_ubi_handle ubi2 $ROOT_MOUNT/data
        elif [ "${root_fstype}" = "squashfs" ]; then
            # data_yaffs2_handle $ROOT_MOUNT/data
            data_ubi_handle ubi0 $ROOT_MOUNT/data
        fi
        ;;
    esac

    # boot the image
    boot_root
}

squashfs_rootfs_mount()
{
    vendor_mtd_number=$(cat /proc/mtd | grep  -E "vendor" | awk -F : '{print $1}' | grep -o '[0-9]\+')
    VENDOR_DEVICE=/dev/mtdblock${vendor_mtd_number}

    system_mtd_number=$(cat /proc/mtd | grep  -E "system" | awk -F : '{print $1}' | grep -o '[0-9]\+')
    ROOT_DEVICE=/dev/mtdblock${system_mtd_number}

    if [ -n "$system_mtd_number" ]; then
        ln -sf "${ROOT_DEVICE}" "/dev/system"${ACTIVE_SLOT}
        dm_verity_setup system ${ROOT_DEVICE} ${ROOT_MOUNT}
    fi

    if [ -n "$vendor_mtd_number" ]; then
        ln -sf "${VENDOR_DEVICE}" "/dev/vendor"${ACTIVE_SLOT}
        dm_verity_setup vendor ${VENDOR_DEVICE} none
    fi

    echo "dm-verity for system is $DM_VERITY_STATUS_SYSTEM"
    if [ "$DM_VERITY_STATUS_SYSTEM" = "disabled" ]; then
        if ! mount -t squashfs -o ro,noatime,nodiratime $ROOT_DEVICE $ROOT_MOUNT ; then
            fatal "Could not mount $ROOT_DEVICE"
        fi
    fi
}

ubi_rootfs_mount()
{
    system_mtd_number=$(cat /proc/mtd | grep  -E "system" | awk -F : '{print $1}' | grep -o '[0-9]\+')
    ubiattach /dev/ubi_ctrl -m ${system_mtd_number}

    if [ -c "/dev/ubi0_0" ]; then
        if ! mount -t ubifs -o ro /dev/ubi0_0 $ROOT_MOUNT ; then
            fatal "Could not mount /dev/ubi0_0"
        fi
    fi
}

ubi_vendor_attach()
{
    vendor_mtd_number=$(cat /proc/mtd | grep  -E "vendor" | awk -F : '{print $1}' | grep -o '[0-9]\+')
    ubiattach /dev/ubi_ctrl -m ${vendor_mtd_number}
}

data_ubi_handle()
{
    data_mtd_number=$(cat /proc/mtd | grep -E -w "data" | awk -F : '{print $1}' | grep -o '[0-9]\+')

    mkdir -p $2
    if ! uenv get factory-reset | grep -q 'value:\[1\]'; then
        mount | grep 'data' && echo "Already mounted" && return 0

        # sure ubi vol exist or not
        ubiattach /dev/ubi_ctrl -m ${data_mtd_number}
        if [ -c "/dev/${1}_0" ]; then
            data_vol_name=`cat /sys/class/ubi/${1}_0/name`
            if [ "${data_vol_name}" = "data" ]; then
                mount -t ubifs /dev/${1}_0 $2
                return 0
            fi
        fi
        ubidetach -p /dev/mtd${data_mtd_number}
    else
        uenv set factory-reset 0
    fi

    #format data
    ubiformat -y /dev/mtd${data_mtd_number}
    ubiattach /dev/ubi_ctrl -m ${data_mtd_number}
    ubimkvol /dev/${1} -m -N data

    mount -t ubifs /dev/${1}_0 $2
}

data_yaffs2_handle()
{
    data_mtd_number=$(cat /proc/mtd | grep -E -w "data" | awk -F : '{print $1}' | grep -o '[0-9]\+')

    mkdir -p $1
    if ! uenv get factory-reset | grep -q 'value:\[1\]'; then
        mount | grep 'data' && echo "Already mounted" && return 0
    else
        flash_erase /dev/mtd${data_mtd_number} 0 0
        uenv set factory-reset 0
    fi

    #mount data
    mount -t yaffs2 /dev/mtdblock${data_mtd_number} $1
}

mount_and_boot
