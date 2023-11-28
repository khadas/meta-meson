require u-boot-meson.inc
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2019:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2019/bl33/v2019:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2019/bl2/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2019/bl30/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2019/bl30/src_ao:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2019/bl31/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2019/bl31_1.3/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2019/bl32_3.8/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2019/fip:"

LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

EXTRA_OEMAKE = ''
PACKAGE_ARCH = "${MACHINE_ARCH}"

#SRC_URI = "git://${AML_GIT_ROOT}/firmware/bin/bl2.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/bl2/bin;name=bl2"
#SRC_URI:append = " git://${AML_GIT_ROOT}/firmware/bin/bl30.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/bl30/bin;name=bl30"
#SRC_URI:append = " git://${AML_GIT_ROOT}/firmware/aocpu.git;protocol=${AML_GIT_PROTOCOL};branch=projects/amlogic-dev;destsuffix=uboot-repo/bl30/src_ao;name=src_ao"
#SRC_URI:append = " git://${AML_GIT_ROOT}/firmware/bin/bl31.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/bl31/bin;name=bl31"
#SRC_URI:append = " git://${AML_GIT_ROOT}/firmware/bin/bl31.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev-1.3;destsuffix=uboot-repo/bl31_1.3/bin;name=bl31-1.3"
#SRC_URI:append = " git://${AML_GIT_ROOT}/firmware/bin/bl32.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev-3.8.0;destsuffix=uboot-repo/bl32_3.8/bin;name=bl32-3.8"
#SRC_URI:append = " git://${AML_GIT_ROOT}/uboot.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev-2019;destsuffix=uboot-repo/bl33/v2019;name=bl33"
#SRC_URI:append = " git://${AML_GIT_ROOT}/amlogic/tools/fip.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/fip;name=fip"

PATCHTOOL="git"

#For common patch
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/bl33/v2019', 'bl33/v2019')}"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/fip', 'fip')}"
#can not patch bl binaries due to permission issue bl binary repos

do_configure[noexec] = "1"

SRCREV_bl2 ?="${AUTOREV}"
SRCREV_bl30 ?="${AUTOREV}"
SRCREV_src_ao ?="${AUTOREV}"
SRCREV_bl31 ?="${AUTOREV}"
SRCREV_bl31-1.3 ?="${AUTOREV}"
SRCREV_bl32-3.8 ?="${AUTOREV}"
SRCREV_bl33 ?="${AUTOREV}"
SRCREV_fip ?="${AUTOREV}"

S = "${WORKDIR}/uboot-repo"
SRCREV_FORMAT = "bl2_bl30_src_ao_bl31_bl31-1.3_bl32-3.8_bl33_fip"
PR = "r1"
PV = "v2019.01+git${SRCPV}"

PATH:append = ":${STAGING_DIR_NATIVE}/gcc-linaro-aarch64-elf/bin"
PATH:append = ":${STAGING_DIR_NATIVE}/riscv-none-gcc/bin"
#DEPENDS:append = "optee-scripts-native optee-userspace-securebl32"
DEPENDS:append = "gcc-linaro-aarch64-elf-native "
DEPENDS:append = "vim-native zip-native"
DEPENDS:append = " riscv-none-gcc-native "

DEPENDS:append = " coreutils-native python-native python-pycrypto-native "
#override this in customer layer bbappend for customer specific bootloader binaries
export BL30_ARG = ""
export BL2_ARG = ""

BL32_SOC_FAMILY = "TBD"
BL32_SOC_FAMILY:ap222 = "s4/s905y4"
BL32_SOC_FAMILY:aq222 = "s4/s805x2"
BL32_ARG = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', '--bl32 bl32/bl32_3.8/bin/${BL32_SOC_FAMILY}/blob-bl32.nand.bin.signed', '', d)}"
BL32_ARG:aq2432 = ""

BL33_ARG = "${@bb.utils.contains('DISTRO_FEATURES','AVB','--avb2','',d)}"
BL33_ARG += " ${@bb.utils.contains('DISTRO_FEATURES', 'AVB_recovery_partition', '--avb2-recovery', '', d)}"

#VMX UBOOT PATH depends on SoC
VMX_UBOOT_PATH = "TBD"
VMX_UBOOT_PATH:s4 = "s905y4"
VMX_UBOOT_PATH:aq2432 = "s805c3"
VMX_UBOOT_PATH:sc2 = "sc2"

# Define BL32 version. Default is v1. Adjust accordingly.
VMX_UBOOT_BL32_VER = "1"
VMX_UBOOT_BL32_VER_s4 = "1"
VMX_UBOOT_BL32_VER_sc2 = "2"
# Decide BL32 directory based on version
VMX_UBOOT_BL32_DIR = "${@'v2' if d.getVar('VMX_UBOOT_BL32_VER') == '2' else ''}"

VMX_UBOOT_NAND_OPTION = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', '.nand', '', d)}"
VMX_UBOOT_ARG = " ${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', '--bl32 vmx-sdk/bootloader/${VMX_UBOOT_PATH}/bl32/${VMX_UBOOT_BL32_DIR}/blob-bl32${VMX_UBOOT_NAND_OPTION}.bin.signed', '', d)}"

#NAGRA UBOOT PATH depends on SoC
NAGRA_UBOOT_PATH = "TBD"
NAGRA_UBOOT_PATH:ah232 = "sc2/s905c2"
NAGRA_UBOOT_PATH:ah221 = "sc2/s905c2l"
NAGRA_UBOOT_ARG = " ${@bb.utils.contains('DISTRO_FEATURES', 'nagra', '--chip-varient nocs-jts-ap --bl32 nagra-sdk/bootloader/${NAGRA_UBOOT_PATH}/bl32/blob-bl32.bin.signed --bl31 nagra-sdk/bootloader/${NAGRA_UBOOT_PATH}/bl31/blob-bl31.bin.signed', '', d)}"

#IRDETO UBOOT PATH depends on SoC
IRDETO_UBOOT_PATH = "TBD"
IRDETO_UBOOT_PATH:sc2 = "sc2"
IRDETO_UBOOT_PATH:ap232 = "s4d/s905c3"
IRDETO_UBOOT_PATH:aq2432 = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', 's4d/s805c3_nand', 's4d/s805c3_emmc', d)}"
IRDETO_UBOOT_PATH:bf201 = "s805c1a/Yocto"
IRDETO_BL2e_ARG="--bl2e irdeto-sdk/bootloader/${IRDETO_UBOOT_PATH}/bl2/blob-bl2e.sto.bin.signed"
IRDETO_BL32_ARG="--bl32 irdeto-sdk/bootloader/${IRDETO_UBOOT_PATH}/bl32/blob-bl32.bin.signed"
IRDETO_BL40_ARG="--bl40 irdeto-sdk/bootloader/${IRDETO_UBOOT_PATH}/bl40/blob-bl40.bin.signed"
IRDETO_UBOOT_ARG = " ${@bb.utils.contains('DISTRO_FEATURES', 'irdeto', '${IRDETO_BL2e_ARG} ${IRDETO_BL32_ARG} ${IRDETO_BL40_ARG}', '', d)}"

CFLAGS +=" -DCONFIG_YOCTO "
KCFLAGS +=" -DCONFIG_YOCTO "

SOC = "TBD"
SOC_aq2432 = "s805c3"
SOC_ap222 = "s905y4"
SOC_ah212 = "s905x4"
do_compile () {
    cd ${S}
    cp -f fip/mk .
    export BUILD_FOLDER=${S}/build/
    export PYTHONPATH="${STAGING_DIR_NATIVE}/usr/lib/python2.7/site-packages/"
    export CROSS_COMPILE=aarch64-elf-
    export KCFLAGS="${KCFLAGS}"
    unset SOURCE_DATE_EPOCH
    UBOOT_TYPE="${UBOOT_MACHINE}"
    LDFLAGS= ./mk ${UBOOT_TYPE%_config} ${BL30_ARG} ${BL2_ARG} ${BL32_ARG} ${BL33_ARG} ${VMX_UBOOT_ARG} ${NAGRA_UBOOT_ARG} ${IRDETO_UBOOT_ARG}
    cp -rf build/* fip/

    if [ "${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', 'true', 'false', d)}" = "true" ] &&\
           [ "${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', 'true', 'false', d)}" = "true" ] ; then
        mkdir -p ${DEPLOY_DIR_IMAGE}
        cp ${S}/bl33/v2019/board/amlogic/${UBOOT_TYPE%_config}/device-keys/fip/rsa/${SOC}/rootrsa-0/key/bl33-level-3-rsa-priv.pem ${DEPLOY_DIR_IMAGE}
    fi
}

