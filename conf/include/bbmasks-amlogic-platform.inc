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
#BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'OverlayFS', '|meta-meson/recipes-core/volatile-binds/volatile-binds.bb', '', d)}"

BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'qt5', '', '|meta-meson/recipes-graphics/qt5/', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'qt5', '', '|aml-qt-rxpreview/aml-qt-rxpreview.bb', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'qt5', '', '|aml-qt-camera/aml-qt-camera.bb', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', '', '|meta-meson/recipes-security/refpolicy/refpolicy-%.bbappend', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'kernel_515', '', '|meta-meson/recipes-core/util-linux/util-linux_%.bbappend', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'kernel_515', '', '|meta-meson/recipes-kernel/make-mod-scripts/make-mod-scripts_1.0.bbappend', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'kernel_515', bb.utils.contains('DISTRO_FEATURES', 'selinux', '', '|meta-meson/recipes-security/audit/audit_%.bbappend', d), '|meta-meson/recipes-security/audit/audit_%.bbappend', d)}"
BBMASK .= "|meta-thunder/recipes-support/curl/curl_7.69%.bbappend"
BBMASK .= "|meta-thunder/recipes-devtools/breakpad/"
BBMASK .= "|poky/meta/recipes-graphics/wayland/weston-init.bb"
BBMASK .= "|meta-meson/recipes-graphics/wayland/weston_6.0.1.bb"
BBMASK .= "|meta-meson/recipes-graphics/wayland/weston_8.0.0.bb"
BBMASK .= "|poky/meta/recipes-connectivity/wpa-supplicant/wpa-supplicant_2.10.bb"
