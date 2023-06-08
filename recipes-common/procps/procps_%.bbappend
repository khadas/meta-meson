FILESEXTRAPATHS:prepend := "${THISDIR}/files/:"
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-reference', 'file://sysctl.conf', '', d)}"

