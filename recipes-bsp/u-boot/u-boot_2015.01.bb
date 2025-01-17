require u-boot-meson.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/bl33/v2015:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/bl2/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/bl30/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/bl31/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/bl31_1.3/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/fip:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files/:"

LICENSE = "GPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

EXTRA_OEMAKE = ''
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI:append = " file://merge_config.sh"
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'absystem', ' file://absystem.cfg', '', d)}"

#SRC_URI = "git://${AML_GIT_ROOT}/firmware/bin/bl2.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/bl2/bin;name=bl2"
#SRC_URI:append = " git://${AML_GIT_ROOT}/firmware/bin/bl30.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/bl30/bin;name=bl30"
#SRC_URI:append = " git://${AML_GIT_ROOT}/firmware/bin/bl31.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/bl31/bin;name=bl31"
#SRC_URI:append = " git://${AML_GIT_ROOT}/firmware/bin/bl31.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev-1.3;destsuffix=uboot-repo/bl31_1.3/bin;name=bl31-1.3"
#SRC_URI:append = " git://${AML_GIT_ROOT}/uboot.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/bl33/v2015;name=bl33"
#SRC_URI:append = " git://${AML_GIT_ROOT}/amlogic/tools/fip.git;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev;destsuffix=uboot-repo/fip;name=fip"

PATCHTOOL="git"
#patches
#SRC_URI:append = " ${@get_patch_list('${THISDIR}/files_2015/bl33/v2015', 'bl33/v2015')}"
#SRC_URI:append = " ${@get_patch_list('${THISDIR}/files_2015/fip', 'fip')}"
#SRC_URI:append = " ${@get_patch_list('${THISDIR}/files_2015/bl31/bin', 'bl31/bin')}"
#SRC_URI:append = " ${@get_patch_list('${THISDIR}/files_2015/bl2/bin', 'bl2/bin')}"
#SRC_URI:append = " ${@get_patch_list('${THISDIR}/files_2015/bl30/bin', 'bl30/bin')}"
#SRC_URI:append = " ${@get_patch_list('${THISDIR}/files_2015/bl31_1.3', 'bl31_1.3/bin')}"

#For common patch
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/bl33/v2015', 'bl33/v2015')}"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/fip', 'fip')}"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/bl31/bin', 'bl31/bin')}"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/bl2/bin', 'bl2/bin')}"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/bl30/bin', 'bl30/bin')}"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/bl31_1.3', 'bl31_1.3/bin')}"

do_configure[noexec] = "1"

#SRCREV_bl2 ?="${AUTOREV}"
#SRCREV_bl30 ?="${AUTOREV}"
#SRCREV_bl31 ?="${AUTOREV}"
#SRCREV_bl31-1.3 ?="${AUTOREV}"
#SRCREV_bl33 ?="${AUTOREV}"
#SRCREV_fip ?="${AUTOREV}"

S = "${WORKDIR}/uboot-repo"
SRCREV_FORMAT = "bl2_bl30_bl31_bl31-1.3_bl33_fip"
PR = "r1"
PV = "v2015.01+git${SRCPV}"

BL32_SOC_FAMILY = "gx"
BL32_SOC_FAMILY:t5d = "t5d"
BL32_SOC_FAMILY:t5w = "t5w"
BL32_SOC_FAMILY:t3 = "t3"
BL32_SOC_FAMILY:g12b = "g12a"
BL32_SOC_FAMILY:sm1 = "g12a"

PATH:append = ":${STAGING_DIR_NATIVE}/gcc-linaro-aarch64-elf/bin"
PATH:append = ":${STAGING_DIR_NATIVE}/riscv-none-gcc/bin"
PATH:append = ":${STAGING_DIR_NATIVE}/gcc-arm-none-elf/bin"
DEPENDS:append = "gcc-linaro-aarch64-elf-native "
DEPENDS:append = "optee-userspace-securebl32"
DEPENDS:append = " riscv-none-gcc-native "
DEPENDS:append = " gcc-arm-none-eabi-native "

DEPENDS:append = " coreutils-native python3-native python3-pycryptodomex-native zip-native xxd-native zip-native "
DEPENDS:t5w:append = " ninja-native cmake-native "


inherit python3native
export BL30_ARG = ""
export BL2_ARG = ""

BL33_ARG = "${@bb.utils.contains('DISTRO_FEATURES','AVB','--avb2','',d)}"

CFLAGS +=" -DCONFIG_YOCTO "
KCFLAGS +=" -DCONFIG_YOCTO "
BUILD_GPT_FLAG=""
BUILD_GPT_FLAG:k5.15="--gpt"

FINAL_DEFCONFIG_PATH = "${S}/bl33/v2015/board/amlogic/defconfigs"
DEFCONFIG = "${UBOOT_TYPE%_config}_defconfig"

do_compile:prepend () {
    cfg_files=$(find ${WORKDIR} -maxdepth 1 -name "*.cfg")
    if [ -n "$cfg_files" ]; then
        UBOOT_TYPE="${UBOOT_MACHINE}"
        if [ ! -f ${FINAL_DEFCONFIG_PATH}/${DEFCONFIG}.temp ]; then
            mv ${FINAL_DEFCONFIG_PATH}/${DEFCONFIG} ${FINAL_DEFCONFIG_PATH}/${DEFCONFIG}.temp
        fi

        KCONFIG_CONFIG=${FINAL_DEFCONFIG_PATH}/${DEFCONFIG} ${WORKDIR}/merge_config.sh -m -r ${FINAL_DEFCONFIG_PATH}/${DEFCONFIG}.temp ${cfg_files}
    fi
}

do_compile () {
    cd ${S}
    cp -f fip/mk .
    export BUILD_FOLDER=${S}/build/
    export PYTHONPATH="${STAGING_DIR_NATIVE}/usr/lib/python3.8/site-packages/"
    export CROSS_COMPILE=aarch64-elf-
    export KCFLAGS="${KCFLAGS}"
    unset SOURCE_DATE_EPOCH
    UBOOT_TYPE="${UBOOT_MACHINE}"

    if ${@bb.utils.contains('DISTRO_FEATURES','secure-u-boot','true','false',d)}; then
        if [ "${BL32_SOC_FAMILY}" = "t5d" ];then
            mkdir -p ${S}/bl32/bl32_2.4/bin/${BL32_SOC_FAMILY}/
            cp ${STAGING_DIR_TARGET}/usr/share/tdk/secureos/${BL32_SOC_FAMILY}/bl32.img \
                ${S}/bl32/bl32_2.4/bin/${BL32_SOC_FAMILY}/bl32.img
            if ${@bb.utils.contains('DISTRO_FEATURES','uboot-abmode','true','false',d)}; then
                echo "process: mk ${UBOOT_TYPE%_config} --vab --ab-update ${BL30_ARG} ${BL2_ARG} ${BL33_ARG}"
                LDFLAGS= ./mk ${UBOOT_TYPE%_config} ${BUILD_GPT_FLAG} --vab --ab-update ${BL30_ARG} ${BL2_ARG} ${BL33_ARG}
            else
                echo "process: mk ${UBOOT_TYPE%_config} --bl32 bl32/bl32_2.4/bin/${BL32_SOC_FAMILY}/bl32.img ${BL30_ARG} ${BL2_ARG} ${BL33_ARG}"
                LDFLAGS= ./mk ${UBOOT_TYPE%_config} ${BUILD_GPT_FLAG} --bl32 bl32/bl32_2.4/bin/${BL32_SOC_FAMILY}/bl32.img ${BL30_ARG} ${BL2_ARG} ${BL33_ARG}
            fi
        elif [ "${BL32_SOC_FAMILY}" = "g12a" ];then
          LDFLAGS= ./mk ${UBOOT_TYPE%_config} --gpt ${BL30_ARG} ${BL2_ARG}
        else
            LDFLAGS= ./mk ${UBOOT_TYPE%_config} ${BUILD_GPT_FLAG} --bl32 bl32/bl32_3.8/bin/${BL32_SOC_FAMILY}/bl32.img ${BL30_ARG} ${BL2_ARG} ${BL33_ARG}
        fi
    else
        LDFLAGS= ./mk ${UBOOT_TYPE%_config} ${BUILD_GPT_FLAG} ${BL33_ARG}
    fi
    cp -rf build/* fip/

    if [ -f "${FINAL_DEFCONFIG_PATH}/${DEFCONFIG}.temp" ]; then
        mv -f ${FINAL_DEFCONFIG_PATH}/${DEFCONFIG}.temp ${FINAL_DEFCONFIG_PATH}/${DEFCONFIG}
    fi
}

