# extra configuration udev rules
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'system-user', \
    'file://10-video.rules', '', d)} \
"

do_install_append () {
    sed -i 's/MOUNT="$MOUNT -o umask=007/MOUNT="$MOUNT -o iocharset=utf8,umask=007/g' ${D}${sysconfdir}/udev/scripts/mount.sh
    sed -i 's/vfat|fat/vfat|fat|ntfs|exfat/g' ${D}${sysconfdir}/udev/scripts/mount.sh

    if [ -e "${WORKDIR}/10-video.rules" ]; then
        install -d ${D}${sysconfdir}/udev/rules.d
        install -m 0644 ${WORKDIR}/10-video.rules ${D}${sysconfdir}/udev/rules.d
    fi
}
