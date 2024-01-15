USE_VT = "${@bb.utils.contains('DISTRO_FEATURES', 'disable-vt', '0', '1', d)}"
