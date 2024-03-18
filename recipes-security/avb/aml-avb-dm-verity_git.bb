DESCRIPTION = "Android Verified Boot 2.0 with Linux DM-Verity support"
LICENSE = "APACHE"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/APACHE;md5=b8228f2369d92593f53f0a0685ebd3c0"

#SRCREV ?= "${AUTOREV}"
PV = "git${SRCPV}"
PR = "r0"

do_compile () {
    cd ${S}
    mkdir -p ${WORKDIR}/obj
    mkdir -p ${WORKDIR}/bin
    mkdir -p ${WORKDIR}/debug
    oe_runmake OBJ_PATH=${WORKDIR}/obj BIN_PATH=${WORKDIR}/bin  DBG_PATH=${WORKDIR}/debug
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/bin/aml-avb-dm-verity ${D}${bindir}
}

FILES:${PN} += "${bindir}/*"
