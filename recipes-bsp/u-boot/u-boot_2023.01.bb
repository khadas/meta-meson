require u-boot-meson.inc
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2023:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2023/bl33/v2023:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2023/bl2/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2023/bl30/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2023/bl30/src_ao:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2023/bl31/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2023/bl31_1.3/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2023/bl31_2.7/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2023/bl32_3.8/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2023/bl32_3.18/bin:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files_2023/fip:"

LICENSE = "GPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

EXTRA_OEMAKE = ''
PACKAGE_ARCH = "${MACHINE_ARCH}"

PATCHTOOL="git"

#For common patch
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/bl33/v2023', 'bl33/v2023')}"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/uboot/fip', 'fip')}"
#can not patch bl binaries due to permission issue bl binary repos

do_configure[noexec] = "1"

SRCREV_bl2 ?="${AUTOREV}"
SRCREV_bl30 ?="${AUTOREV}"
SRCREV_src_ao ?="${AUTOREV}"
SRCREV_bl31 ?="${AUTOREV}"
SRCREV_bl31-1.3 ?="${AUTOREV}"
SRCREV_bl31-2.7 ?="${AUTOREV}"
SRCREV_bl32-3.8 ?="${AUTOREV}"
SRCREV_bl32-3.18 ?="${AUTOREV}"
SRCREV_bl33 ?="${AUTOREV}"
SRCREV_fip ?="${AUTOREV}"

S = "${WORKDIR}/uboot-repo"
SRCREV_FORMAT = "bl2_bl31-2.7_bl32-3.18_bl33_fip"
PR = "r1"
PV = "v2023.01+git${SRCPV}"

PATH:append = ":${STAGING_DIR_NATIVE}/gcc-linaro-aarch64-elf/bin"
PATH:append = ":${STAGING_DIR_NATIVE}/riscv-none-gcc/bin"
#DEPENDS:append = "optee-userspace-securebl32"
DEPENDS:append = "gcc-linaro-aarch64-elf-native "
DEPENDS:append = "vim-native zip-native cmake-native"
DEPENDS:append = " riscv-none-gcc-native "

DEPENDS:append = " coreutils-native python-native python-pycrypto-native ninja-native "
DEPENDS:append = "${@bb.utils.contains_any('DISTRO_FEATURES', 'partition-enc partition-enc-local', ' partition-keys ', '', d)}"

#override this in customer layer bbappend for customer specific bootloader binaries
export BL30_ARG = ""
export BL2_ARG = ""

BL31_SOC_FAMILY = "TBD"
BL31_SOC_FAMILY:s1a = "s1a"
BL31_ARG = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', '--bl31 bl31/bl31_2.7/bin/${BL31_SOC_FAMILY}/bl31.bin', '', d)}"

BL32_SOC_FAMILY = "TBD"
BL32_SOC_FAMILY:s1a = "s1a"
BL32_ARG = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', '--bl32 bl32/bl32_3.18/bin/${BL32_SOC_FAMILY}/blob-bl32.nand.bin.signed', '', d)}"
BL32_ARG:s1a = ""

BL33_ARG = "${@bb.utils.contains('DISTRO_FEATURES','AVB','--avb2','',d)}"

#VMX UBOOT PATH depends on SoC
VMX_UBOOT_PATH = "TBD"
VMX_UBOOT_PATH:s4 = "s905y4"
VMX_UBOOT_PATH:aq2432 = "s805c3"
VMX_UBOOT_NAND_OPTION = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', '.nand', '', d)}"
VMX_UBOOT_ARG = " ${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', '--bl32 vmx-sdk/bootloader/${VMX_UBOOT_PATH}/bl32/blob-bl32${VMX_UBOOT_NAND_OPTION}.bin.signed', '', d)}"

NAGRA_UBOOT_ARG = ""

#IRDETO UBOOT PATH depends on SoC
IRDETO_UBOOT_PATH = "TBD"
IRDETO_UBOOT_PATH:sc2 = "sc2"
IRDETO_UBOOT_PATH:ap232 = "s4d/s905c3"
IRDETO_UBOOT_PATH:aq2432 = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', 's4d/s805c3_nand', 's4d/s805c3_emmc', d)}"
IRDETO_UBOOT_PATH:bf201 = "s805c1a/Yocto"
IRDETO_UBOOT_PATH:bg201 = "s805c1/Yocto"
IRDETO_UBOOT_PATH:bg209 = "s805c1/Yocto"
IRDETO_BL2e_ARG="--bl2e irdeto-sdk/bootloader/${IRDETO_UBOOT_PATH}/bl2/blob-bl2e.sto.bin.signed"
IRDETO_BL32_ARG="--bl32 irdeto-sdk/bootloader/${IRDETO_UBOOT_PATH}/bl32/blob-bl32.bin.signed"
IRDETO_BL40_ARG="--bl40 irdeto-sdk/bootloader/${IRDETO_UBOOT_PATH}/bl40/blob-bl40.bin.signed"
IRDETO_UBOOT_ARG = " ${@bb.utils.contains('DISTRO_FEATURES', 'irdeto', '${IRDETO_BL2e_ARG} ${IRDETO_BL40_ARG}', '', d)}"
IRDETO_UBOOT_ARG:s1a += " ${@bb.utils.contains('DISTRO_FEATURES', 'irdeto-ree-only', '', '${IRDETO_BL32_ARG}', d)}"

CFLAGS +=" -DCONFIG_YOCTO "
KCFLAGS +=" -DCONFIG_YOCTO "

SOC = "TBD"
SOC_bg201 = "s805s1a"
do_compile () {
    cd ${S}
    cp -f fip/mk .
    export BUILD_FOLDER=${S}/build/
    export PYTHONPATH="${STAGING_DIR_NATIVE}/usr/lib/python2.7/site-packages/"
    export CROSS_COMPILE=aarch64-elf-
    export KCFLAGS="${KCFLAGS}"
    unset SOURCE_DATE_EPOCH
    UBOOT_TYPE="${UBOOT_MACHINE}"

    if [ ! -z "${ENCRYPTED_PARTITIONS}" ]; then
        bbnote "ENC_PART = ${ENCRYPTED_PARTITIONS}"
        if [ "${@bb.utils.contains('DISTRO_FEATURES', 'partition-enc', 'true', 'false', d)}" = "true" ]; then
            # Pre-encryption mode, partition key is wrapped by PWEK
            SUFFIX=".wrapped.bin"
        elif [ "${@bb.utils.contains('DISTRO_FEATURES', 'partition-enc-local', 'true', 'false', d)}" = "true" ]; then
            # Local encryption mode, partition key is used as seed
            SUFFIX=".bin"
        else
            bbfatal "It is likely wrong here!"
        fi

        TARGET_DIR="${DEPLOY_DIR_IMAGE}/partition_enc_data/"
        PAIR=""
        for part in ${ENCRYPTED_PARTITIONS}; do
            key=${TARGET_DIR}/${part}${SUFFIX}
            if [ -f ${key} ]; then
                if [ -z ${PAIR} ]; then
                    PAIR="${part}:`xxd -p ${key}`"
                else
                    PAIR="${PAIR};${part}:`xxd -p ${key}`"
                fi
            else
                bbfatal "${part} partition is encrypted, but it cannot find ${part}${SUFFIX} in ${TARGET_DIR}"
            fi
        done
        if [ ! -z ${PAIR} ]; then
            KCFLAGS_ADD="${KCFLAGS_ADD} -DPARTITION_ENC_ARGS='\"${PAIR}\"'"
        fi
        export KCFLAGS="${KCFLAGS} ${KCFLAGS_ADD}"
        bbnote "${KCFLAGS_ADD}"
    fi

    LDFLAGS= ./mk ${UBOOT_TYPE%_config} ${BL30_ARG} ${BL2_ARG} ${BL32_ARG} ${BL33_ARG} ${VMX_UBOOT_ARG} ${NAGRA_UBOOT_ARG} ${IRDETO_UBOOT_ARG}
    #LDFLAGS= ./mk ${UBOOT_TYPE%_config}

    cp -rf build/* fip/

    if [ "${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', 'true', 'false', d)}" = "true" ] &&\
           [ "${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', 'true', 'false', d)}" = "true" ] ; then
        mkdir -p ${DEPLOY_DIR_IMAGE}
        cp ${S}/bl33/v2023/board/amlogic/${UBOOT_TYPE%_config}/device-keys/fip/rsa/${SOC}/rootrsa-0/key/bl33-level-3-rsa-priv.pem ${DEPLOY_DIR_IMAGE}
    fi
}
