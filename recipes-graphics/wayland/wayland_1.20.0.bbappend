FILESEXTRAPATHS:prepend := "${THISDIR}/wayland-1.20:"

SRC_URI += " \
    file://0003-10.0-add-video-plane-id-request.patch \
    file://0004-10.0.0-add-pts-setting-request.patch \
    file://0005-10.0.0-improve-priority-of-main-thread.patch \
    "

do_install:append() {
    rm -f ${D}${libdir}/libwayland-egl*
    rm -f ${D}${libdir}/pkgconfig/wayland-egl.pc
}
RDEPENDS:${PN}-ptest:remove += " binutils"
