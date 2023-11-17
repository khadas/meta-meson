do_install:append() {
    rm -f ${D}${libdir}/libwayland-egl*
    rm -f ${D}${libdir}/pkgconfig/wayland-egl.pc
}
