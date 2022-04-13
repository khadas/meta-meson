
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
FILESEXTRAPATHS_prepend := "${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', '${THISDIR}/files/tv:', '${THISDIR}/files/stb:', d)}"

SRC_URI_append = " file://0001-use-fb1-set-alpha-ff.patch"
