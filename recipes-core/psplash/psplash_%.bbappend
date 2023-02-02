
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
FILESEXTRAPATHS:prepend := "${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', '${THISDIR}/files/tv:', '${THISDIR}/files/stb:', d)}"

SRC_URI:append = " file://0001-use-fb1-set-alpha-ff.patch"
