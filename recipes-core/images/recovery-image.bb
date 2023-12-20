SUMMARY = "create recovery image"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

inherit core-image
inherit image
SDKEXTCLASS ?= "${@['populate_sdk', 'populate_sdk_ext']['linux' in d.getVar("SDK_OS", True)]}"
inherit ${SDKEXTCLASS}

DEPENDS:append = " android-tools-native"

IMAGE_INSTALL = "udev busybox"
IMAGE_INSTALL:append = "\
                    initramfs-recovery \
                    fuse-exfat \
                    exfat-utils \
                    ntfs-3g \
                    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
                    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'base-files sysvinit initscripts modutils-initscripts util-linux-agetty', '', d)} \
                    kernel-modules \
                    system-config \
                    zram \
                    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-dtv', 'aml-dtvdemod', '', d)} \
                    ${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'mtd-utils-ubifs', 'e2fsprogs',d)} \
                   "

IMAGE_INSTALL:remove:k5.15 = "\
                    kernel-modules \
                    "

IMAGE_INSTALL:remove = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'zapper-2k', 'system-config', '', d)} \
"

IMAGE_INSTALL:append= "${@bb.utils.contains("DISTRO_FEATURES", "swupdate", \
            "aml-bootloader-message \
            libconfig \
            aml-swupdate-ui \
            swupdate", "", d)}"

IMAGE_INSTALL:append= "${@bb.utils.contains("DISTRO_FEATURES", "swupdate-download", \
            " wpa-supplicant wifi-amlogic ", "", d)}"

IMAGE_INSTALL:append= "${@bb.utils.contains("DISTRO_FEATURES", "swupdate-dvb-ota", \
            " tuner-prebuilt \
            modules-load \
            aucpu-fw \
            aml-dvb-ota", "", d)}"

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"

python __anonymous () {
    import re
    type = d.getVar('KERNEL_IMAGETYPE', True) or ""
    alttype = d.getVar('KERNEL_ALT_IMAGETYPE', True) or ""
    types = d.getVar('KERNEL_IMAGETYPES', True) or ""
    if type not in types.split():
        types = (type + ' ' + types).strip()
    if alttype not in types.split():
        types = (alttype + ' ' + types).strip()
    d.setVar('KERNEL_IMAGETYPES', types)

    typeformake = re.sub(r'\.gz', '', types)
    d.setVar('KERNEL_IMAGETYPE_FOR_MAKE', typeformake)

    d.delVarFlag('do_fetch', 'noexec')
    d.delVarFlag('do_unpack', 'noexec')
}

do_rootfs:append () {
    import shutil
    rootfsdir = d.getVar('IMAGE_ROOTFS', True) or ""
    bootdir = "%s/boot" % rootfsdir
    shutil.rmtree(bootdir, True)
}

KERNEL_BOOTARGS = ""

do_bundle_initramfs_dtb() {
    if ${@bb.utils.contains('MULTILIBS', 'multilib:lib32', 'true', 'false', d)}; then
        mkbootimg --kernel ${DEPLOY_DIR_IMAGE}/Image.gz --base 0x0 --kernel_offset 0x1080000 --cmdline "${KERNEL_BOOTARGS}" --ramdisk  ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.cpio.gz --second ${DEPLOY_DIR_IMAGE}/dtb.img --output ${DEPLOY_DIR_IMAGE}/recovery.img
    else
        mkbootimg --kernel ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} --base 0x0 --kernel_offset 0x1080000 --cmdline "${KERNEL_BOOTARGS}" --ramdisk  ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.cpio.gz --second ${DEPLOY_DIR_IMAGE}/dtb.img --output ${DEPLOY_DIR_IMAGE}/recovery.img
    fi
}

addtask bundle_initramfs_dtb before do_image_complete after do_image_cpio do_unpack
#always regenerate recovery.img
do_bundle_initramfs_dtb[nostamp] = "1"

do_rootfs[depends] += "android-tools-native:do_populate_sysroot"
IMAGE_ROOTFS_EXTRA_SPACE:append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "" ,d)}"

ROOTFS_POSTPROCESS_COMMAND += "remove_alternative_files; "
remove_alternative_files () {
    rm -rf ${IMAGE_ROOTFS}/usr/lib/opkg
}

ROOTFS_POSTPROCESS_COMMAND += "delete_unused_items_from_fstab; "

#if dm-verity is enabled, mount /vendor(/dev/dm-1) will be failed, and recovery can not boot up
delete_unused_items_from_fstab() {
    if [ -e ${IMAGE_ROOTFS}/etc/fstab ];then
        sed -i '/\/vendor/ d' ${IMAGE_ROOTFS}/etc/fstab
        sed -i '/\/tee/ d' ${IMAGE_ROOTFS}/etc/fstab
        sed -i '/\/factory/ d' ${IMAGE_ROOTFS}/etc/fstab
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-2k', 'remove_hwdb_for_zapper; ', '', d)}"
remove_hwdb_for_zapper() {
    if [ -e ${IMAGE_ROOTFS}/lib/udev/hwdb.bin ];then
        rm -rf ${IMAGE_ROOTFS}/lib/udev/hwdb.bin
    fi
    if [ -d ${IMAGE_ROOTFS}/lib/udev/hwdb.d ];then
        rm -rf ${IMAGE_ROOTFS}/lib/udev/hwdb.d
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "${@bb.utils.contains('DISTRO_FEATURES', 'kernel_515', 'install_kernel_modules; ', '', d)}"
install_kernel_modules() {
   if [ -f ${DEPLOY_DIR_IMAGE}/kernel-modules.tgz ]; then
     tar -zxvf ${DEPLOY_DIR_IMAGE}/kernel-modules.tgz -C ${IMAGE_ROOTFS}/
   fi
}

