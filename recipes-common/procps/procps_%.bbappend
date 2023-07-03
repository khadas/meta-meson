FILESEXTRAPATHS:prepend := "${THISDIR}/files/:"
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-2k', 'file://sysctl.conf', '', d)}"

