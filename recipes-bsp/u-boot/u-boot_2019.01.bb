require u-boot-meson.inc
FILESEXTRAPATHS_prepend := "${THISDIR}/files_2019:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files_2019/bl33/v2019:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files_2019/bl2/bin:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files_2019/bl30/bin:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files_2019/bl30/src_ao:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files_2019/bl31/bin:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files_2019/bl31_1.3/bin:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files_2019/bl32_3.8/bin:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files_2019/fip:"

LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

EXTRA_OEMAKE = ''
PACKAGE_ARCH = "${MACHINE_ARCH}"

#SRC_URI = "git://${AML_GIT_ROOT}/firmware/bin/bl2.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/bl2/bin;name=bl2"
#SRC_URI_append = " git://${AML_GIT_ROOT}/firmware/bin/bl30.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/bl30/bin;name=bl30"
#SRC_URI_append = " git://${AML_GIT_ROOT}/firmware/aocpu.git;protocol=${AML_GIT_PROTOCOL};branch=projects/amlogic-dev;destsuffix=uboot-repo/bl30/src_ao;name=src_ao"
#SRC_URI_append = " git://${AML_GIT_ROOT}/firmware/bin/bl31.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/bl31/bin;name=bl31"
#SRC_URI_append = " git://${AML_GIT_ROOT}/firmware/bin/bl31.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev-1.3;destsuffix=uboot-repo/bl31_1.3/bin;name=bl31-1.3"
#SRC_URI_append = " git://${AML_GIT_ROOT}/firmware/bin/bl32.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev-3.8.0;destsuffix=uboot-repo/bl32_3.8/bin;name=bl32-3.8"
#SRC_URI_append = " git://${AML_GIT_ROOT}/uboot.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev-2019;destsuffix=uboot-repo/bl33/v2019;name=bl33"
#SRC_URI_append = " git://${AML_GIT_ROOT}/amlogic/tools/fip.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/fip;name=fip"

PATCHTOOL="git"

#For common patch
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/bl33/v2019', 'bl33/v2019')}"
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/fip', 'fip')}"
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

PATH_append = ":${STAGING_DIR_NATIVE}/gcc-linaro-aarch64-elf/bin"
PATH_append = ":${STAGING_DIR_NATIVE}/riscv-none-gcc/bin"
#DEPENDS_append = "optee-scripts-native optee-userspace-securebl32"
DEPENDS_append = "gcc-linaro-aarch64-elf-native "
DEPENDS_append = "vim-native zip-native"
DEPENDS_append = " riscv-none-gcc-native "

DEPENDS_append = " coreutils-native python-native python-pycrypto-native "
#override this in customer layer bbappend for customer specific bootloader binaries
export BL30_ARG = ""
export BL2_ARG = ""

BL32_SOC_FAMILY = "TBD"
BL32_SOC_FAMILY_s4 = "s4/s905y4"
BL32_ARG = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', '--bl32 bl32_3.8/bin/${BL32_SOC_FAMILY}/blob-bl32.nand.bin.signed', '', d)}"

#VMX UBOOT PATH depends on SoC
VMX_UBOOT_PATH = "TBD"
VMX_UBOOT_PATH_s4 = "s905y4"
VMX_UBOOT_ARG = " ${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', '--bl32 vmx-sdk/bootloader/${VMX_UBOOT_PATH}/bl32/blob-bl32.bin.signed', '', d)}"
BL33_ARG = "${@bb.utils.contains('DISTRO_FEATURES','AVB','--avb2','',d)}"

#NAGRA UBOOT PATH depends on SoC
NAGRA_UBOOT_PATH = "TBD"
NAGRA_UBOOT_PATH_ah232 = "sc2/s905c2"
NAGRA_UBOOT_PATH_ah221 = "sc2/s905c2l"
NAGRA_UBOOT_ARG = " ${@bb.utils.contains('DISTRO_FEATURES', 'nagra', '--chip-varient nocs-jts-ap --bl32 nagra-sdk/bootloader/${NAGRA_UBOOT_PATH}/bl32/blob-bl32.bin.signed --bl31 nagra-sdk/bootloader/${NAGRA_UBOOT_PATH}/bl31/blob-bl31.bin.signed', '', d)}"

CFLAGS +=" -DCONFIG_YOCTO "
KCFLAGS +=" -DCONFIG_YOCTO "
do_compile () {
    cd ${S}
    cp -f fip/mk .
    export BUILD_FOLDER=${S}/build/
    export PYTHONPATH="${STAGING_DIR_NATIVE}/usr/lib/python2.7/site-packages/"
    export CROSS_COMPILE=aarch64-elf-
    export KCFLAGS="${KCFLAGS}"
    unset SOURCE_DATE_EPOCH
    UBOOT_TYPE="${UBOOT_MACHINE}"
    LDFLAGS= ./mk ${UBOOT_TYPE%_config} ${BL30_ARG} ${BL2_ARG} ${BL32_ARG} ${BL33_ARG} ${VMX_UBOOT_ARG} ${NAGRA_UBOOT_ARG}
    cp -rf build/* fip/
}

