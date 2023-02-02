SUMMARY = "aml avsync library"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

include aml-multimedia.inc

do_configure[noexec] = "1"
inherit autotools pkgconfig

S="${WORKDIR}/git/avsync-lib"

EXTRA_OEMAKE="STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D}"

do_compile() {
    cd ${S}
    bash version_config.sh
    cd ${S}/src
    oe_runmake  all
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    cd ${S}/src
    install -m 0644 aml_avsync_log.h ${D}${includedir}
    install -m 0644 aml_avsync.h ${D}${includedir}
    install -m 0644 libamlavsync.so ${D}${libdir}
}

FILES:${PN} = "${bindir}/* ${libdir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "ldflags"
