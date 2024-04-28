SUMMARY = "aml avsync library"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

include aml-multimedia.inc

do_configure[noexec] = "1"
inherit autotools pkgconfig

S="${WORKDIR}/git/avsync-lib"

OUT_DIR="${B}/src"

EXTRA_OEMAKE=" OUT_DIR=${OUT_DIR} STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D}"

do_compile() {
    cd ${S}
    bash version_config.sh ${OUT_DIR}
    cd ${S}/src
    oe_runmake all
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -m 0644 ${S}/src/aml_avsync_log.h ${D}${includedir}
    install -m 0644 ${S}/src/aml_avsync.h ${D}${includedir}
    install -m 0644 ${OUT_DIR}/libamlavsync.so ${D}${libdir}
}

do_clean() {
    cd ${S}/src
    oe_runmake clean
}


FILES:${PN} = "${bindir}/* ${libdir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "ldflags installed-vs-shipped"
