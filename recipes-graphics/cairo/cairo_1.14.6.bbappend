FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
PACKAGECONFIG:append:class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', '', ' egl glesv2', d)}"
PACKAGECONFIG:remove = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', '', ' directfb', d)}"
SRC_URI:append = " file://0008-add-noaa-compositor.patch \
                   file://cairo_scaled_font_destroy_Assertion.patch"
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', '', ' file://cairo-egl-device-create-for-egl-surface.patch', d)}"

