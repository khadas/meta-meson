inherit image
SDKEXTCLASS ?= "${@['populate_sdk', 'populate_sdk_ext']['linux' in d.getVar("SDK_OS", True)]}"
inherit ${SDKEXTCLASS}

DEPENDS_append = " android-tools-native"
DEPENDS_append = "${@bb.utils.contains("DISTRO_FEATURES", "FIT", " u-boot-tools-native dtc-native", "" ,d)}"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += " file://boot-template.its"

#IMAGE_INSTALL = "udev busybox ${ROOTFS_PKGMANAGE_BOOTSTRAP}"
IMAGE_INSTALL = "udev busybox"
IMAGE_INSTALL_append = "\
                    initramfs-meson-boot \
                    e2fsprogs \
                    aml-ubootenv \
                    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'policycoreutils-setfiles', '', d)} \
                    ${@bb.utils.contains("DISTRO_FEATURES", "nand", "mtd-utils-ubifs", "", d)} \
                   "

#IMAGE_INSTALL_append_aarch64 = "\
#                    kernel-modules \
#                    gpu \
#                    kmod \
#                    ${MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS} \
#                    ${CORE_IMAGE_EXTRA_INSTALL} \
#                    "

IMAGE_INSTALL_append = "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', ' cryptsetup lvm2-udevrules ', '', d)}"

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

do_rootfs_append () {
    import shutil
    rootfsdir = d.getVar('IMAGE_ROOTFS', True) or ""
    bootdir = "%s/boot" % rootfsdir
    shutil.rmtree(bootdir, True)
}

KERNEL_BOOTARGS = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'root=/dev/ubi0_0 rootfstype=ubifs', 'root=/dev/system rootfstype=ext4', d)}"

do_bundle_initramfs_dtb() {
  #Handle FIT feature here
  if ${@bb.utils.contains('DISTRO_FEATURES','FIT','true','false',d)}; then
    cp -f ${WORKDIR}/boot-template.its ${DEPLOY_DIR_IMAGE}/boot.its
    #Replace Kernel image info
    sed -i 's@KERNEL_IMG_PATH@${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}@' ${DEPLOY_DIR_IMAGE}/boot.its
    sed -i 's@KERNEL_IMG_ARCH@arm@' ${DEPLOY_DIR_IMAGE}/boot.its
    sed -i 's@KERNEL_LOADADDR@0x1080000@' ${DEPLOY_DIR_IMAGE}/boot.its
    #sed -i 's@KERNEL_CMDLINE@${KERNEL_BOOTARGS}@' ${DEPLOY_DIR_IMAGE}/boot.its
    #Replace Ramdisk image info
    mkimage -A arm -T ramdisk -C gzip -d ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.cpio.gz  ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.cpio.gz.ramdisk
    sed -i 's@RAMDISK_IMG_PATH@${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.cpio.gz.ramdisk@' ${DEPLOY_DIR_IMAGE}/boot.its
    sed -i 's@RAMDISK_IMG_ARCH@arm@' ${DEPLOY_DIR_IMAGE}/boot.its
    #sed -i 's@RAMDISK_LOADADDR@0x1080000@' ${DEPLOY_DIR_IMAGE}/boot.its
    #Replace dtb image info
    sed -i 's@FDT_IMG_PATH@${DEPLOY_DIR_IMAGE}/dtb.img@' ${DEPLOY_DIR_IMAGE}/boot.its
    sed -i 's@FDT_IMG_ARCH@arm@' ${DEPLOY_DIR_IMAGE}/boot.its

    mkimage -f ${DEPLOY_DIR_IMAGE}/boot.its ${DEPLOY_DIR_IMAGE}/boot.img
  else
    mkbootimg --kernel ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} --base 0x0 --kernel_offset 0x1080000 --cmdline "${KERNEL_BOOTARGS}" --ramdisk ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.cpio.gz --second ${DEPLOY_DIR_IMAGE}/dtb.img --output ${DEPLOY_DIR_IMAGE}/boot.img
  fi
}

addtask bundle_initramfs_dtb before do_image_complete after do_image_cpio do_unpack
#always regenerate boot.img
do_bundle_initramfs_dtb[nostamp] = "1"

do_rootfs[depends] += "android-tools-native:do_populate_sysroot"
do_rootfs[depends] += "${@bb.utils.contains("DISTRO_FEATURES", "FIT", " u-boot-tools-native:do_populate_sysroot dtc-native:do_populate_sysroot", "" ,d)}"
IMAGE_ROOTFS_EXTRA_SPACE_append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "" ,d)}"

deploy_verity_hash() {
    if [ -f ${DEPLOY_DIR_IMAGE}/${DM_VERITY_IMAGE}.${DM_VERITY_IMAGE_TYPE}.verity.env ]; then
        bbnote "install -D -m 0644 ${DEPLOY_DIR_IMAGE}/${DM_VERITY_IMAGE}.${DM_VERITY_IMAGE_TYPE}.verity.env \
            ${IMAGE_ROOTFS}/${datadir}/system-dm-verity.env"
        install -D -m 0644 ${DEPLOY_DIR_IMAGE}/${DM_VERITY_IMAGE}.${DM_VERITY_IMAGE_TYPE}.verity.env \
            ${IMAGE_ROOTFS}/${datadir}/system-dm-verity.env
    else
        bberror "Cannot find ${DEPLOY_DIR_IMAGE}/${DM_VERITY_IMAGE}.${DM_VERITY_IMAGE_TYPE}.verity.env"
    fi

    if [ -f ${DEPLOY_DIR_IMAGE}/${VENDOR_DM_VERITY_IMAGE}.${DM_VERITY_IMAGE_TYPE}.verity.env ]; then
        bbnote " install -D -m 0644 ${DEPLOY_DIR_IMAGE}/${VENDOR_DM_VERITY_IMAGE}.${DM_VERITY_IMAGE_TYPE}.verity.env \
            ${IMAGE_ROOTFS}/${datadir}/vendor-dm-verity.env"
        install -D -m 0644 ${DEPLOY_DIR_IMAGE}/${VENDOR_DM_VERITY_IMAGE}.${DM_VERITY_IMAGE_TYPE}.verity.env \
            ${IMAGE_ROOTFS}/${datadir}/vendor-dm-verity.env
    else
        bberror "Cannot find ${DEPLOY_DIR_IMAGE}/${VENDOR_DM_VERITY_IMAGE}.${DM_VERITY_IMAGE_TYPE}.verity.env"
    fi
}
do_rootfs[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', '${DM_VERITY_IMAGE}:do_image_${DM_VERITY_IMAGE_TYPE}', '', d)}"
do_rootfs[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', '${VENDOR_DM_VERITY_IMAGE}:do_image_${DM_VERITY_IMAGE_TYPE}', '', d)}"
ROOTFS_POSTPROCESS_COMMAND += "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'deploy_verity_hash;', '', d)}"
