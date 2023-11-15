SUMMARY = "aml libdvr"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"


#SRC_URI = "git://${AML_GIT_ROOT}/libdvr.git;protocol=${AML_GIT_PROTOCOL};branch=r-tv-dev"

#For common patches

SRC_URI:append = "${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libdvr')}"
DEPENDS += "aml-audio-service aml-mediahal-sdk liblog aml-dvb"
RDEPENDS:${PN} += "aml-audio-service aml-mediahal-sdk liblog aml-dvb"

#do_compile[noexec] = "1"

SRCREV ?= "${AUTOREV}"

#PV = "git999"

#S = "${WORKDIR}/git"


PV = "git${SRCPV}"

S = "${WORKDIR}/git"

ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET:aarch64 = "aarch64.lp64."
TA_TARGET="noarch"


do_compile() {
    cd ${S}
    oe_runmake all TARGET_DIR=TBD
}
do_install() {
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -m 0755 -d ${D}${includedir}/libdvr
    install -m 0755 ${S}/libamdvr.so ${D}${libdir}/
    install -m 0755 ${S}/am_fend_test ${D}${bindir}/
    install -m 0755 ${S}/am_dmx_test ${D}${bindir}/
    install -m 0755 ${S}/dvr_wrapper_test ${D}${bindir}/
    install -m 0644 ${S}/include/* ${D}${includedir}/libdvr/
    install -m 0644 ${S}/include/dvb_*.h ${D}${includedir}/
    install -m 0644 ${S}/include/dvr_*.h ${D}${includedir}/
    install -m 0644 ${S}/include/segment*.h ${D}${includedir}/
    install -m 0644 ${S}/include/list.h ${D}${includedir}/
}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
