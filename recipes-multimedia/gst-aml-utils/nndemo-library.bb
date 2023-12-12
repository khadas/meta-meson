DESCRIPTION = "Amlogic face detect with NPU"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += " npu-common zlib aml-log"
RDEPENDS:${PN} += " npu-common zlib aml-log"

inherit autotools pkgconfig


SRCREV ?="${AUTOREV}"

do_configure[noexec] = "1"

EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D} PKG_CONFIG=${STAGING_BINDIR_NATIVE}/pkg-config"

do_compile(){
    oe_runmake -C ${S} ${EXTRA_OEMAKE} all
}

do_install() {
    install -d -m 0755 ${D}${includedir}
    install -d -m 0755 ${D}${libdir}

    #install -D -m 0644 ${S}/include/common/*.h ${D}${includedir}/
    install -D -m 0644 ${S}/include/common/sdk_log.h  ${D}${includedir}/
    #install -D -m 0644 ${S}/include/demo/*.h ${D}${includedir}/
    install -D -m 0644 ${S}/include/demo/cv_postprocess.h ${D}${includedir}/
    install -D -m 0644 ${S}/include/demo/imagelabel.h ${D}${includedir}/
    install -D -m 0644 ${B}/*.so ${D}${libdir}/
}

FILES:${PN} = " ${libdir}/* /etc/* "
FILES:${PN}-dev = " ${includedir}/* \
                    /usr/lib/pkgconfig/* \
    "
INSANE_SKIP:${PN} += "file-rdeps"