DESCRIPTION = "Amlogic face detect with NPU"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += " npu-common detect-library"
RDEPENDS:${PN} += " npu-common detect-library"

inherit autotools pkgconfig


SRCREV ?="${AUTOREV}"

do_configure[noexec] = "1"

EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D} PKG_CONFIG=${STAGING_BINDIR_NATIVE}/pkg-config"

do_compile(){
    oe_runmake -C ${S} ${EXTRA_OEMAKE} all
}

do_install() {
    install -d -m 0755 ${D}${bindir}
    install -D -m 0777 ${B}/nnsample ${D}${bindir}/
}

FILES:${PN} = "${bindir}/*"
INSANE_SKIP:${PN} += "file-rdeps"
