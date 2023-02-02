FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'system-user', \
    'file://system.patch', '', d)} \
"
