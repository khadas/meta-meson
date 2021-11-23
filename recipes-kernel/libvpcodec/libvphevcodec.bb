SUMMARY = "aml libvpcodec library H265 for C&M Wav420 IP"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "libge2d"

SRCREV ?="${AUTOREV}"

do_configure[noexec] = "1"

EXTRA_OEMAKE += "LIBVPHEVCODEC_STAGING_DIR=${D} CROSS=${TARGET_PREFIX} TARGET_DIR=${D} STAGING_DIR=${D} DESTDIR=${D} INSTALL_DIR=${D}/usr/lib"

do_compile () {
    cd ${S}
    oe_runmake ${EXTRA_OEMAKE} -C ${S}/EncoderAPI-HEVC/hevc_enc/
    oe_runmake ${EXTRA_OEMAKE} -C ${S}/EncoderAPI-HEVC/
}

do_install () {
    install -d ${D}${includedir}
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -m 0644 -D ${S}/EncoderAPI-HEVC/hevc_enc/vp_hevc_codec_1_0.h ${D}${includedir}
    install -m 0644 -D ${S}/EncoderAPI-HEVC/hevc_enc/libvphevcodec.so ${D}${libdir}
    install -m 0644 -D ${S}/EncoderAPI-HEVC/testHevcApi ${D}${bindir}
}

FILES_${PN} = " ${libdir}/* ${bindir}/*"
FILES_${PN}-dev = "${includedir}/*"
INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN}-dev = "dev-elf dev-so"
