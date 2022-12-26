BBMASK += "meta-meson/recipes-graphics/waffle/waffle_1.5.2.bbappend"
BBMASK += "meta-meson/recipes-multimedia/gstreamer/gst-player_git.bbappend"
BBMASK += "meta-thunder/meta-rdk-amazon/recipes-extended/amazon-prime-src/amazon-prime-src_2.3.bb"
BBMASK += "meta-thunder/meta-rdk-bsp-amlogic/recipes-extended/amazon-prime-src/amazon-prime-src_2.3.bbappend"
# MASK out this for error "not a BitBake file"
BBMASK .= "|meta-security/recipes-core/busybox"
# Don't need kernel patch for dm-verity(enabled by default)
BBMASK .= "|meta-security/recipes-kernel/linux/"

BBMASK .= "|meta-selinux/recipes-extended/sed/sed_4.%.bbappend"
BBMASK .= "|meta-selinux/recipes-extended/findutils/findutils_%.bbappend"
BBMASK .= "|meta-selinux/recipes-core/coreutils/coreutils_%.bbappend"

#Common part
BBMASK += "meta-aml-netflix/recipes-graphics/rdkresidentapp/rdkresidentapp.bbappend"
BBMASK += "meta-aml-netflix-nrdp6/recipes-graphics/rdkresidentapp/rdkresidentapp.bbappend"
BBMASK += "meta-aml-netflix-nrdp6/recipes-netflix/recipes-graphics/rdkresidentapp/rdkresidentapp.bbappend"

BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'OverlayFS', '|meta-meson/recipes-core/volatile-binds/volatile-binds.bb', '', d)}"

BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'qt5', '', '|meta-meson/recipes-graphics/qt5/qtbase_%d.bbappend', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', '', '|meta-meson/recipes-security/refpolicy/refpolicy-%.bbappend', d)}"
