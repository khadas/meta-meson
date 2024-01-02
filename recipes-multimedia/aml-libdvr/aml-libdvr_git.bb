SUMMARY = "aml libdvr"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRC_URI:append = "${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libdvr')}"
DEPENDS += "aml-audio-service aml-mediahal-sdk liblog aml-dvb"
RDEPENDS:${PN} += "aml-audio-service aml-mediahal-sdk liblog aml-dvb"

#do_compile[noexec] = "1"

SRCREV ?= "${AUTOREV}"
PV = "git${SRCPV}"

ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET:aarch64 = "aarch64.lp64."
TA_TARGET="noarch"

EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET}"

do_compile() {
    oe_runmake -C ${S} all
}

do_install() {
    oe_runmake -C ${S} install
}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
