# extra configuration udev rules
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'system-user', \
    'file://10-video.rules', '', d)} \
"
SRC_URI:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'nand', '', \
    bb.utils.contains('DISTRO_FEATURES', 'kernel_515', 'file://block.rules', '', d),  d)} \
"

SRC_URI:append = " \
    ${@bb.utils.contains_any('DISTRO_FEATURES', ['bluetooth', 'wifi'], \
    'file://99-rfkill.rules file://bt-rfkill.sh file://wlan-rfkill.sh', '', d)} \
"

do_install:append () {
    sed -i 's/vfat|fat/vfat|fat|ntfs|exfat/g' ${D}${sysconfdir}/udev/scripts/mount.sh
    sed -i 's/$UMOUNT $mnt/$UMOUNT $mnt/g' ${D}${sysconfdir}/udev/scripts/mount.sh

    if [ -e "${WORKDIR}/10-video.rules" ]; then
        install -d ${D}${sysconfdir}/udev/rules.d
        install -m 0644 ${WORKDIR}/10-video.rules ${D}${sysconfdir}/udev/rules.d
    fi
    if [ -e "${WORKDIR}/block.rules" ]; then
      echo "/dev/mmcblk0*" >> ${D}${sysconfdir}/udev/mount.ignorelist
      install -m 0644 ${WORKDIR}/block.rules ${D}${sysconfdir}/udev/rules.d
    fi
    if ${@bb.utils.contains_any('DISTRO_FEATURES', ['bluetooth', 'wifi'], 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/udev/rules.d
        install -m 0644 ${WORKDIR}/99-rfkill.rules ${D}${sysconfdir}/udev/rules.d
        install -d ${D}${sysconfdir}/udev/scripts
        install -m 0755 ${WORKDIR}/bt-rfkill.sh ${D}${sysconfdir}/udev/scripts
        install -m 0755 ${WORKDIR}/wlan-rfkill.sh ${D}${sysconfdir}/udev/scripts
    fi
}
