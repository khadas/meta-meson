# extra configuration udev rules
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'system-user', \
    'file://10-video.rules', '', d)} \
"

do_install_append () {
    sed -i 's/vfat|fat/vfat|fat|ntfs|exfat/g' ${D}${sysconfdir}/udev/scripts/mount.sh
    sed -i 's/$UMOUNT $mnt/$UMOUNT $mnt/g' ${D}${sysconfdir}/udev/scripts/mount.sh

    if [ -e "${WORKDIR}/10-video.rules" ]; then
        install -d ${D}${sysconfdir}/udev/rules.d
        install -m 0644 ${WORKDIR}/10-video.rules ${D}${sysconfdir}/udev/rules.d
    fi
}
