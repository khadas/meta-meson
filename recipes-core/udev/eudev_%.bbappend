FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

INITSCRIPT_PARAMS = " \
 ${@bb.utils.contains('DISTRO_FEATURES', 'use-mdev', \
    'disable', 'start 04 S .', d)}\
"

SRC_URI:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'zapper', \
    'file://0001-custom-hwdb-module-for-zapper.patch', '', d)} \
"
