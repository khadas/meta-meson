SUMMARY = "aml libamvenc library (encoder hal) for Amlogic"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "libion"

SRCREV ?="${AUTOREV}"

do_configure[noexec] = "1"

EXTRA_OEMAKE += "LIBAMVENC_STAGING_DIR=${D} CROSS=${TARGET_PREFIX} TARGET_DIR=${D} STAGING_DIR=${D} DESTDIR=${D} INSTALL_DIR=${D}/usr/lib"

do_compile () {
    cd ${S}
    oe_runmake ${EXTRA_OEMAKE} -C ${S}/amvenc_lib
    oe_runmake ${EXTRA_OEMAKE} -C ${S}/amvenc_test
}

do_install () {
    install -d ${D}${includedir}
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -m 0644 -D ${S}/amvenc_lib/include/amvenc.h ${D}${includedir}
    install -m 0644 -D ${S}/amvenc_lib/libamvenc.so ${D}${libdir}
    install -m 0644 -D ${S}/amvenc_test/amvenc_test ${D}${bindir}
}

FILES:${PN} = " ${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/*"
INSANE_SKIP:${PN} = "dev-so"
INSANE_SKIP:${PN}-dev = "dev-elf dev-so"