inherit module
SUMMARY = "Tuner prebuilt drivers"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRCREV ?= "${AUTOREV}"
PV = "git${SRCPV}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
ARM_TARGET = "32"
ARM_TARGET:aarch64 = "64"

PREBUILT_TARGET = ""
PREBUILT_TARGET:aq2432 = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', 'aq2432_zapper', 'aq2432', d)}"
PREBUILT_TARGET:bf201 = "aq2432_zapper"

S = "${WORKDIR}/git"
do_install() {
    TUNER_KO_VERSION=$(echo ${KERNEL_VERSION} | cut -d'-' -f1 | cut -d'.' -f1,2)
    KO_DIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/tuner
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    mkdir -p ${KO_DIR}

    for item in ${KERNEL_MODULE_AUTOLOAD}
    do
      module=${S}/kernel-module/tuner/${TUNER_KO_VERSION}/${ARM_TARGET}/${PREBUILT_TARGET}/${item}.ko
      if [ -f ${module} ];then
        install -m 0644 ${module} ${KO_DIR}
      fi
    done
}

