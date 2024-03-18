DESCRIPTION = "irdeto-tests"
SECTION = "irdeto-tests"
LICENSE = "CLOSE"
PV = "git${SRCPV}"
PR = "r0"

SRC_URI:append = " ${@get_patch_list_with_path('${COREBASE}/aml-patches/vendor/irdeto/irdeto-tests')}"

PN = 'irdeto-tests'
#SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "liblog aml-libdvr aml-mediahal-sdk aml-cas-hal irdeto-sdk optee-userspace"
RDEPENDS:${PN} += "liblog aml-libdvr aml-mediahal-sdk aml-cas-hal irdeto-sdk optee-userspace"

EXTRA_OEMAKE = "STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D} SYSROOT_DIR=${PKG_CONFIG_SYSROOT_DIR}"

do_compile () {
    cd ${S}
    oe_runmake -j1 ${EXTRA_OEMAKE} all
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 -D ${S}/irdeto_msr_test ${D}/usr/bin/
}

FILES:${PN} = "${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
