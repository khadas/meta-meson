SUMMARY = "Amlogic Yocto Image"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

inherit core-image

IMAGE_FSTYPES = "ext4"

require ${OEROOT}/meta-meson/recipes-core/images/aml-package.inc

LICENSE = "MIT"

#IMAGE_FEATURES += "splash "

IMAGE_INSTALL += " \
    packagegroup-amlogic-baserootfs \
    "

MACHINE_IMAGE_NAME ?= "${PN}"
IMAGE_FEATURES_remove = " read-only-rootfs"

#ROOTFS max in KB
# 4096K overhead
#IMAGE_ROOTFS_SIZE_u212 = "256000"

VIRTUAL-RUNTIME_dev_manager = "busybox"

DEPENDS_${PN} = " android-tools-native"

PACKAGE_INSTALL += "base-files netbase ${VIRTUAL-RUNTIME_base-utils} ${VIRTUAL-RUNTIME_dev_manager} base-passwd ${ROOTFS_BOOTSTRAP_INSTALL} initramfs-meson-boot e2fsprogs "

IMAGE_LINGUAS = ""

IMAGE_ROOTFS_SIZE = "8192"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
KERNEL_BOOTARGS = "root=/dev/system rootfstype=ext4"

do_rootfs[depends] += "android-tools-native:do_populate_sysroot"
#PREFERRED_VERSION_ell = "0.22"

#ROOTFS_POSTPROCESS_COMMAND += "disable_systemd_services; "

##disable_systemd_services() {
#        if [ -d ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/ ]; then
#                rm -f ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/appmanager.service;
#
#        fi
#
#        # Remove files added by openembedded-core/meta/recipes-connectivity/wpa-supplicant/wpa-supplicant_2.7.bb:
#        rm -f ${IMAGE_ROOTFS}${systemd_unitdir}/system/wpa_supplicant.service
#        rm -f ${IMAGE_ROOTFS}${systemd_unitdir}/system/wpa_supplicant-nl80211@.service
#        rm -f ${IMAGE_ROOTFS}${systemd_unitdir}/system/wpa_supplicant-wired@.service
#        rm -f ${IMAGE_ROOTFS}${systemd_unitdir}/system/wpa_supplicant@.service
#        rm -f ${IMAGE_ROOTFS}${sysconfdir}/network/if-pre-up.d/wpa-supplicant
#        rm -f ${IMAGE_ROOTFS}${sysconfdir}/network/if-post-down.d/wpa_supplicant
#        #rm -f ${IMAGE_ROOTFS}${sysconfdir}/dbus-1/system.d/dbus-wpa_supplicant.conf
#        #rm -f ${IMAGE_ROOTFS}${sysconfdir}/etc/default/volatiles/99_wpa_supplicant
#}
