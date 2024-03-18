inherit module
SUMMARY = "afd drivers"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/DTVKit/AFD.git;protocol=${AML_GIT_PROTOCOL};branch=master"

#SRCREV ?= "${AUTOREV}"
PV = "git${SRCPV}"

S = "${WORKDIR}/git"

do_configure[noexec] = "1"

do_compile(){
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    export M=${S} KERNEL_SRC=${STAGING_KERNEL_DIR} CROSS_COMPILE=${CROSS_COMPILE}
    ${MAKE} -C ${S} modules
}

do_install() {
    KO_DIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/afd
    mkdir -p ${KO_DIR}

    find ${S}/ -name *.ko | xargs -i install -m 0666 {} ${KO_DIR}
}
