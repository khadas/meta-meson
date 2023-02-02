require u-boot-meson.inc
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/bl33/v2015:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/bl2/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/bl30/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/bl31/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/bl31_1.3/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2015/fip:"

LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

EXTRA_OEMAKE = ''
PACKAGE_ARCH = "${MACHINE_ARCH}"

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

SRCREV_bl2 ?="${AUTOREV}"
SRCREV_bl30 ?="${AUTOREV}"
SRCREV_bl31 ?="${AUTOREV}"
SRCREV_bl31-1.3 ?="${AUTOREV}"
SRCREV_bl33 ?="${AUTOREV}"
SRCREV_fip ?="${AUTOREV}"

S = "${WORKDIR}/uboot-repo"
SRCREV_FORMAT = "bl2_bl30_bl31_bl31-1.3_bl33_fip"
PR = "r1"
PV = "v2015.01+git${SRCPV}"

BL32_SOC_FAMILY = "gx"
BL32_SOC_FAMILY:t5d = "t5d"
BL32_SOC_FAMILY:t5w = "t5w"
BL32_SOC_FAMILY:t3 = "t3"

PATH:append = ":${STAGING_DIR_NATIVE}/gcc-linaro-aarch64-elf/bin"
PATH:append = ":${STAGING_DIR_NATIVE}/riscv-none-gcc/bin"
DEPENDS:append = "gcc-linaro-aarch64-elf-native "
DEPENDS:append = "optee-scripts-native optee-userspace-securebl32"
DEPENDS:append = " riscv-none-gcc-native "

DEPENDS:append = " coreutils-native python3-native python3-pycryptodomex-native "
DEPENDS:t5w:append = " ninja-native cmake-native "

inherit python3native
export BL30_ARG = ""
export BL2_ARG = ""

CFLAGS +=" -DCONFIG_YOCTO "
KCFLAGS +=" -DCONFIG_YOCTO "
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
            mkdir -p ${S}/bl32/bin/${BL32_SOC_FAMILY}/
            ${STAGING_DIR_NATIVE}/tdk/scripts/pack_kpub.py \
                --rsk=${STAGING_DIR_NATIVE}/tdk/keys/root_rsa_pub_key.pem \
                --rek=${STAGING_DIR_NATIVE}/tdk/keys/root_aes_key.bin \
                --in=${STAGING_DIR_TARGET}/usr/share/tdk/secureos/${BL32_SOC_FAMILY}/bl32.img \
                --out=${S}/bl32/bin/${BL32_SOC_FAMILY}/bl32.img

            LDFLAGS= ./mk ${UBOOT_TYPE%_config} --bl32 bl32/bin/${BL32_SOC_FAMILY}/bl32.img ${BL30_ARG} ${BL2_ARG}
        else
            LDFLAGS= ./mk ${UBOOT_TYPE%_config} --bl32 bl32_3.8/bin/${BL32_SOC_FAMILY}/bl32.img ${BL30_ARG} ${BL2_ARG}
        fi
    else
        LDFLAGS= ./mk ${UBOOT_TYPE%_config}
    fi
    cp -rf build/* fip/
}

