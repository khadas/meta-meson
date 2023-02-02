inherit module

SUMMARY = "nagra amlsec and cert kernel driver"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM=""

#Only enable it in OpenLinux
#SRC_URI:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'nagra', 'git://${AML_GIT_ROOT_OP}/nagra-sdk-nocs.git;protocol=${AML_GIT_ROOT_PROTOCOL};branch=projects/openlinux/v3.6','', d)}"
SRC_URI:append = " ${@get_patch_list_with_path('${COREBASE}/aml-patches/vendor/nagra/nagra-sdk')}"

SRCREV ?= "${AUTOREV}"
PV = "git${SRCPV}"
S = "${WORKDIR}/git"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    MODULE_DIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/amlogic
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    mkdir -p ${MODULE_DIR}/sec_mkl
    mkdir -p ${MODULE_DIR}/ca_cert
    install -m 0666 ${S}/lib/ko/amlsec_mkl.ko ${MODULE_DIR}/sec_mkl
    install -m 0755 ${S}/lib/ko/amlsec-mkl-create-devnode.sh ${MODULE_DIR}/sec_mkl
    install -m 0666 ${S}/lib/ko/aml-ca-cert.ko ${MODULE_DIR}/ca_cert
    install -m 0755 ${S}/lib/ko/nocs-ca-cert-create-devnode.sh ${MODULE_DIR}/ca_cert
}

FILES:${PN} += "amlsec_mkl.ko aml-ca-cert.ko"
FILES:${PN} += "/lib/modules/${KERNEL_VERSION}/kernel/drivers/amlogic/ca_cert/nocs-ca-cert-create-devnode.sh"
FILES:${PN} += "/lib/modules/${KERNEL_VERSION}/kernel/drivers/amlogic/sec_mkl/amlsec-mkl-create-devnode.sh"
