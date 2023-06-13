FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# If define no-gpu feature, remove egl related config and patch
PACKAGECONFIG:append:class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'no-gpu', '', ' egl glesv2', d)}"
SRC_URI:append = " file://0008-add-noaa-compositor.patch \
                   file://cairo_scaled_font_destroy_Assertion.patch"
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'no-gpu', '', ' file://cairo-egl-device-create-for-egl-surface.patch', d)}"
