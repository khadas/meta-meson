FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
PACKAGECONFIG_append_class-target = " egl glesv2"
PACKAGECONFIG_remove = "directfb"
SRC_URI_append = " file://cairo-egl-device-create-for-egl-surface.patch \
                   file://0008-add-noaa-compositor.patch \ 
                   file://cairo_scaled_font_destroy_Assertion.patch"

