SUMMARY = "aml libion library"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRCREV ?="${AUTOREV}"

PACKAGES = "${PN} FILES-${PN}-lib ${PN}-dev ${PN}-dbg ${PN}-staticdev"

do_configure[noexec] = "1"

EXTRA_OEMAKE = "LIBION_STAGING_DIR=${D} CROSS=${TARGET_PREFIX} TARGET_DIR=${D} STAGING_DIR=${D} DESTDIR=${D} INSTALL_DIR=${D}/usr/lib"

do_compile () {
    cd ${S}
    oe_runmake ${EXTRA_OEMAKE}
}

do_install () {
    install -d ${D}${includedir}
    install -d ${D}${libdir}
    install -m 0644 -D ${S}/include/ion/ion.h ${D}${includedir}
    install -m 0644 -D ${S}/include/ion/IONmem.h ${D}${includedir}
    install -m 0644 -D ${S}/libion.so ${D}${libdir}
}

FILES_${PN} = " ${libdir}/*"
FILES_${PN}-dev = "${includedir}/*"
