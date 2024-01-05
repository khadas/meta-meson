SUMMARY = "aml libge2d library"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "libion"

SRCREV ?="${AUTOREV}"

do_configure[noexec] = "1"

EXTRA_OEMAKE = "LIBGE2D_STAGING_DIR=${D} CROSS=${TARGET_PREFIX} TARGET_DIR=${D} STAGING_DIR=${D} DESTDIR=${D} INSTALL_DIR=${D}/usr/lib"

do_compile () {
    cd ${S}
    oe_runmake ${EXTRA_OEMAKE}
}

do_install () {
    install -d ${D}${includedir}
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -m 0644 -D ${S}/libge2d/include/aml_ge2d.h ${D}${includedir}
    install -m 0644 -D ${S}/libge2d/include/ge2d_port.h ${D}${includedir}
    install -m 0644 -D ${S}/libge2d/libge2d.so ${D}${libdir}
    install -m 0644 -D ${S}/ge2d_feature_test ${D}${bindir}
}

FILES:${PN} = " ${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/*"
INSANE_SKIP:${PN} = "dev-so ldflags"
