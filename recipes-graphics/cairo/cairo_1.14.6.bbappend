FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
PACKAGECONFIG_append_class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', '', ' egl glesv2', d)}"
PACKAGECONFIG_remove = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', '', ' directfb', d)}"
SRC_URI_append = " file://0008-add-noaa-compositor.patch \
                   file://cairo_scaled_font_destroy_Assertion.patch"
SRC_URI_append = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', '', ' file://cairo-egl-device-create-for-egl-surface.patch', d)}"

