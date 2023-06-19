inherit kernel
require linux-meson.inc

LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

#We still need patch even in external src mode
SRCTREECOVEREDTASKS:remove = "do_patch"
FILESEXTRAPATHS:prepend := "${THISDIR}/5.15:"

# aq2432 zapper needs its own defconfig
FILESEXTRAPATHS:prepend:aq2432 := "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', '${THISDIR}/5.15/aq2432_zapper:', '${THISDIR}/5.15/aq2432:', d)}"
FILESEXTRAPATHS:prepend:bf201 := "${THISDIR}/5.15/bf201:"

KBRANCH = "bringup/amlogic-5.15/s4da1_2_2_20220407"
#SRC_URI = "git://${AML_GIT_ROOT}/kernel/common.git;protocol=${AML_GIT_PROTOCOL};branch=${KBRANCH};"
SRC_URI:append = " file://modules_install.sh"
SRC_URI:append = " file://extra_modules_install.sh"
SRC_URI:append:sc2 = " file://sc2.cfg"
SRC_URI:append:s4 = " file://s4.cfg"
SRC_URI:append:aq2432 = " file://defconfig"
SRC_URI:append:bf201 = " file://defconfig"

SRC_URI += "file://common.cfg"

# add support nand
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'file://nand.cfg', '', d)}"

# Enable selinux support in the kernel if the feature is enabled
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'file://selinux.cfg', '', d)}"

#For common patches
KDIR = "aml-5.15"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/kernel/${KDIR}')}"

LINUX_VERSION ?= "5.15.78"
LINUX_VERSION_EXTENSION ?= "-amlogic"

PR = "r2"
SRCREV ?="${AUTOREV}"
PV = "${LINUX_VERSION}+git${SRCPV}"

KERNEL_IMAGETYPE = "Image"
KCONFIG_MODE = "alldefconfig"

S = "${WORKDIR}/git"
KBUILD_DEFCONFIG = "final_defconfig"
DEPENDS += "elfutils-native"

GKI_DEFCONFIG = "gki_defconfig"
GKI_DEFCONFIG_kernel32 = "a32_base_defconfig"
GKI_AMLOGIC_DEFCONFIG = "amlogic_gki.fragment"
GKI_AMLOGIC_DEFCONFIG_kernel32 = "amlogic_a32.fragment"
GKI10_DEFCONFIG = "amlogic_gki.10"
GKIDEBUG_DEFCONFIG = "amlogic_gki.debug"
GCC_DEFCONFIG = "amlogic_gcc64_deconfig"
GCC_DEFCONFIG_kernel32 = "amlogic_gcc32_defconfig"

FINAL_DEFCONFIG_PATH = "${S}/arch/${ARCH}/configs"
GKI_DEFCONFIG_PATH = "${S}/arch/arm64/configs"
GKI_DEFCONFIG_PATH_kernel32 = "${S}/common_drivers/arch/arm/configs"
GKI_AML_CONFIG_PATH = "${S}/common_drivers/arch/${ARCH}/configs"

SOC = ""
SOC:sc2 = "sc2"
SOC:s4 = "s4"
SOC:t3 = "t3"
SOC:t7 = "t7"


do_install:append () {
    oe_runmake -C ${STAGING_KERNEL_DIR}/${1} CC="${KERNEL_CC}" LD="${KERNEL_LD}" O=${B} M=${1} KERNEL_SRC=${S} INSTALL_MOD_PATH=${D} INSTALL_MOD_STRIP=1 modules_install
    NAND_FLAG="${@bb.utils.contains('DISTRO_FEATURES', 'nand', "true", "false", d)}";
    bash ${WORKDIR}/modules_install.sh ${D} ${STAGING_KERNEL_DIR}/common_drivers/ ${KERNEL_VERSION} ${NAND_FLAG} ${SOC}
}

do_compile:prepend () {
    export MODULES_STAGING_DIR=${D}
    export COMMON_DRIVERS_DIR=./common_drivers
    echo "arch: ${ARCH}"
    if [ "${ARCH}" = "arm" ]; then
	echo "export LOADERADDR"
    	export LOADADDR=0x208000
    fi
}

do_configure:prepend () {
    export COMMON_DRIVERS_DIR=./common_drivers
}

do_kernel_configme:prepend () {
    export COMMON_DRIVERS_DIR=./common_drivers
}

do_kernel_metadata:prepend () {
    export COMMON_DRIVERS_DIR=./common_drivers

    install -m 755 ${WORKDIR}/extra_modules_install.sh ${STAGING_KERNEL_DIR}/
    rm -f ${FINAL_DEFCONFIG_PATH}/${KBUILD_DEFCONFIG}
    if [ "${ARCH}" = "arm" ]; then
        KCONFIG_CONFIG=${FINAL_DEFCONFIG_PATH}/${KBUILD_DEFCONFIG}  ${S}/scripts/kconfig/merge_config.sh -m -r ${GKI_DEFCONFIG_PATH}/${GKI_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GKI_AMLOGIC_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GCC_DEFCONFIG}
    else
        KCONFIG_CONFIG=${FINAL_DEFCONFIG_PATH}/${KBUILD_DEFCONFIG}  ${S}/scripts/kconfig/merge_config.sh -m -r ${GKI_DEFCONFIG_PATH}/${GKI_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GKI_AMLOGIC_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GKI10_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GKIDEBUG_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GCC_DEFCONFIG}
    fi
}

do_compile_kernelmodules:prepend () {
    export COMMON_DRIVERS_DIR=./common_drivers
    export MODULES_STAGING_DIR=${D}
}

do_compile_kernelmodules:append () {
    export COMMON_DRIVERS_DIR=./common_drivers
    export MODULES_STAGING_DIR=${D}
}

do_install:prepend () {
    export COMMON_DRIVERS_DIR=./common_drivers
}

do_deploy:append() {
         if [ ${MODULE_TARBALL_DEPLOY} = "1" ] && (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
                 mkdir -p ${D}${root_prefix}/modules
                 tar -cvzf $deployDir/kernel-modules-${MODULE_TARBALL_NAME}.tgz -C ${D}${root_prefix} modules
                 ln -sf kernel-modules-${MODULE_TARBALL_NAME}.tgz $deployDir/modules-${MODULE_TARBALL_LINK_NAME}.tgz
                 ln -sf kernel-modules-${MODULE_TARBALL_NAME}.tgz $deployDir/kernel-modules.tgz
         fi
}

KERNEL_MODULE_AUTOLOAD += "amlogic-crypto-dma"
KERNEL_MODULE_AUTOLOAD += "sha1-ce"
KERNEL_MODULE_AUTOLOAD += "zsmalloc"
KERNEL_MODULE_AUTOLOAD += "ntfs3"
KERNEL_MODULE_AUTOLOAD += "zram"
KERNEL_MODULE_AUTOLOAD += "system_heap"
KERNEL_MODULE_AUTOLOAD += "mdio-mux"
KERNEL_MODULE_AUTOLOAD += "pcs_xpcs"
KERNEL_MODULE_AUTOLOAD += "amlogic-phy-debug"
KERNEL_MODULE_AUTOLOAD += "stmmac"
KERNEL_MODULE_AUTOLOAD += "stmmac-platform"
KERNEL_MODULE_AUTOLOAD += "dwmac-meson"
KERNEL_MODULE_AUTOLOAD += "dwmac-meson8b"
KERNEL_MODULE_AUTOLOAD += "dwmac-dwc-qos-eth"
KERNEL_MODULE_AUTOLOAD += "i2c-dev"
KERNEL_MODULE_AUTOLOAD += "btqca"
KERNEL_MODULE_AUTOLOAD += "btbcm"
KERNEL_MODULE_AUTOLOAD += "hci_uart"
KERNEL_MODULE_AUTOLOAD += "amlogic-rtc"
KERNEL_MODULE_AUTOLOAD += "amlogic-audio-utils"
KERNEL_MODULE_AUTOLOAD += "amlogic-dvb-demux"
KERNEL_MODULE_AUTOLOAD += "amlogic_pcie_v2_host"
KERNEL_MODULE_AUTOLOAD += "amlogic-memory-debug"
KERNEL_MODULE_AUTOLOAD += "amlogic-jtag"
KERNEL_MODULE_AUTOLOAD += "amlogic-socinfo"
KERNEL_MODULE_AUTOLOAD += "amlogic-hifidsp"
KERNEL_MODULE_AUTOLOAD += "amlogic-wireless"
KERNEL_MODULE_AUTOLOAD += "amlogic-mdio-g12a"
KERNEL_MODULE_AUTOLOAD += "amlogic-led"
KERNEL_MODULE_AUTOLOAD += "amlogic-snd-codec-dummy"
KERNEL_MODULE_AUTOLOAD += "amlogic-snd-soc"
KERNEL_MODULE_AUTOLOAD += "amlogic-snd-codec-t9015"
KERNEL_MODULE_AUTOLOAD += "amlogic-snd-codec-tl1"
KERNEL_MODULE_AUTOLOAD += "amlogic-snd-codec-tas5707"
KERNEL_MODULE_AUTOLOAD += "cfg80211"
KERNEL_MODULE_AUTOLOAD += "mac80211"

FILES:${PN} += "modules/*"
