inherit kernel
require linux-meson.inc
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"


#We still need patch even in external src mode
SRCTREECOVEREDTASKS:remove = "do_patch"
KERNEL_FEATURES:remove = " ${@bb.utils.contains('MACHINE_FEATURES', 'vfat', 'cfg/fs/vfat.scc', '', d)}"
FILESEXTRAPATHS:prepend := "${THISDIR}/6.6:"

KBRANCH = "amlogic-6.6-dev"
#SRC_URI = "git://${AML_GIT_ROOT}/kernel/common.git;protocol=${AML_GIT_PROTOCOL};branch=${KBRANCH};destsuffix=git;name=k66"
#SRC_URI += "git://${AML_GIT_ROOT}/kernel/common_drivers.git;protocol=${AML_GIT_PROTOCOL};branch=${KBRANCH};destsuffix=git/common_drivers;name=k66-driver"
SRC_URI:append = " file://modules_install.sh"
SRC_URI:append = " file://extra_modules_install.sh"
SRC_URI:append:sc2 = " file://sc2.cfg"
SRC_URI:append:s5 = " file://sc2.cfg"
SRC_URI:append:s4 = " file://s4.cfg"
SRC_URI:append:s7 = " file://s7.cfg"
SRC_URI:append:s1a = " file://s1a.cfg"
SRC_URI:append:t5d = " file://t5d.cfg"
SRC_URI:append:t5w = " file://t5w.cfg"
SRC_URI:append:aq2432 = " file://defconfig"
SRC_URI:append:bf201 = " file://defconfig"
SRC_URI:append:bg201 = " file://defconfig"
SRC_URI:append:t7 = " file://t7.cfg"
SRC_URI:append:g12b = " file://g12b.cfg"
SRC_URI:append:sm1 = " file://sm1.cfg"
SRC_URI:append = " file://common.cfg"
#SRC_URI:append = " file://modules_sequence_list"

# add support nand
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'file://nand.cfg', '', d)}"
# Enable selinux support in the kernel if the feature is enabled
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'file://selinux.cfg', '', d)}"

#For common patches
#SRC_URI_append = " ${@get_patch_list_with_path('${COREBASE}/../aml-patches/kernel/aml-6.6')}"
LINUX_VERSION ?= "6.6.18"
LINUX_VERSION_EXTENSION ?= "-amlogic"
PR = "r0"

PV = "${LINUX_VERSION}+git${SRCPV}"

KERNEL_IMAGETYPE = "Image.gz"
KCONFIG_MODE = "alldefconfig"

S = "${WORKDIR}/git"
#KBUILD_DEFCONFIG ??= "meson64_a64_R_defconfig"
KBUILD_DEFCONFIG = "meson64_a64_smarthome_defconfig"
KBUILD_DEFCONFIG[p1] = "meson64_a64_P_defconfig"
KBUILD_DEFCONFIG[kernel32] = "meson64_a32_defconfig"
KBUILD_DEFCONFIG[smarthome_kernel64] = "meson64_a64_smarthome_defconfig"

#GKI_DEFCONFIG = "meson64_gki_module_config"
#p1 did not use GKI yet.
#GKI_DEFCONFIG_p1 = ""
#Force NO GKI for 32bit kernel
#GKI_DEFCONFIG_kernel32 = ""
#GKI_DEFCONFIG_smarthome_kernel64 = ""
GKI_DEFCONFIG = ""


do_install:append () {
    oe_runmake -C ${STAGING_KERNEL_DIR}/${1} CC="${KERNEL_CC}" LD="${KERNEL_LD}" O=${B} M=${1} KERNEL_SRC=${S} INSTALL_MOD_PATH=${D} INSTALL_MOD_STRIP=1 modules_install
    NAND_FLAG="${@bb.utils.contains('DISTRO_FEATURES', 'nand', "true", "false", d)}";
    bash ${WORKDIR}/modules_install.sh ${D} ${STAGING_KERNEL_DIR}/common_drivers/ ${KERNEL_VERSION} ${NAND_FLAG} ${SOC}
}

do_compile:prepend () {
    export MODULES_STAGING_DIR=${D}
    export LOADADDR=0x1008000
    export INSTALL_MOD_STRIP=1
}

do_configure:prepend () {
    export COMMON_DRIVERS_DIR=./common_drivers
    install -m 755 ${WORKDIR}/extra_modules_install.sh ${STAGING_KERNEL_DIR}/
}

do_kernel_configme:prepend () {
    export COMMON_DRIVERS_DIR=./common_drivers
}

do_kernel_metadata:prepend () {
    export COMMON_DRIVERS_DIR=./common_drivers
    cd ${S}
    KERNEL_DIR=${S} ./common_drivers/auto_patch/auto_patch.sh "common15-6.6"
    cd -

    cp -f ${S}/${COMMON_DRIVERS_DIR}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG} ${S}/arch/${ARCH}/configs/
}

do_compile_kernelmodules:prepend () {
    export MODULES_STAGING_DIR=${D}
}

do_compile_kernelmodules:append () {
    export MODULES_STAGING_DIR=${D}
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
KERNEL_MODULE_AUTOLOAD += "amlogic-mmc"
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
KERNEL_MODULE_AUTOLOAD += "v4l2-async"
KERNEL_MODULE_AUTOLOAD += "v4l2-fwnode"

FILES:${PN} += "modules/*"
INSANE_SKIP:${PN} = " installed-vs-shipped"
