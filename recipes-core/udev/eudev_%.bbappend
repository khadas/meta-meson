FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'zapper-reference', \
    'file://0001-custom-hwdb-module-for-zapper.patch', '', d)} \
"