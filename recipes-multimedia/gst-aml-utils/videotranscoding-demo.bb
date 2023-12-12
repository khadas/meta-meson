DESCRIPTION = "Amlogic dma buffer allocator plugin"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "gstreamer1.0 gstreamer1.0-plugins-base"
DEPENDS += "gst-aml-videotranscoding"
RDEPENDS:${PN} += "gst-aml-videotranscoding"

inherit autotools pkgconfig

SRCREV ?="${AUTOREV}"

do_configure[noexec] = "1"

EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D} PKG_CONFIG=${STAGING_BINDIR_NATIVE}/pkg-config"

do_compile(){
    oe_runmake -C ${S} ${EXTRA_OEMAKE} all
}


do_install() {
    install -d -m 0755 ${D}${bindir}
    install -D -m 0777 ${B}/videotranscoding_demo ${D}${bindir}/
}

FILES:${PN} = "${bindir}/*"
FILES:${PN}-dev = "${includedir}/*"

INSANE_SKIP:${PN} += "file-rdeps"
