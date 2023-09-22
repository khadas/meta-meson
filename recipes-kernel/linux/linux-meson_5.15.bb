inherit kernel
require linux-meson.inc

LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

#We still need patch even in external src mode
SRCTREECOVEREDTASKS:remove = "do_patch"
FILESEXTRAPATHS:prepend := "${THISDIR}/5.15:"
FILESEXTRAPATHS:prepend := "${THISDIR}/aml_dtoverlay:"

FILESEXTRAPATHS:prepend:bf201 := "${THISDIR}/5.15/bf201:"
FILESEXTRAPATHS:prepend:bg201 := "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-128m', '${THISDIR}/5.15/bg201_128m:',  '${THISDIR}/5.15/bg201:', d)}"
#FILESEXTRAPATHS:prepend:ap222-zapper := "${THISDIR}/5.15/bg201:"

KBRANCH = "amlogic-5.15-dev"
#SRC_URI = "git://${AML_GIT_ROOT}/kernel/common.git;protocol=${AML_GIT_PROTOCOL};branch=${KBRANCH};"
SRC_URI:append = " file://modules_install.sh"
SRC_URI:append = " file://extra_modules_install.sh"
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', '',  ' file://common.cfg', d)}"
SRC_URI:append:sc2 = " file://sc2.cfg"
SRC_URI:append:s5 = " file://sc2.cfg"
SRC_URI:append:s4 = " file://s4.cfg"
SRC_URI:append:s7 = " file://s7.cfg"
SRC_URI:append:s7d = " file://s7d.cfg"
SRC_URI:append:t5d = " file://t5d.cfg"
SRC_URI:append:t5w = " file://t5w.cfg"
SRC_URI:append:t3 = " file://sc2.cfg"
SRC_URI:append:t3x = " file://sc2.cfg"
SRC_URI:append:t5m = " file://sc2.cfg"
SRC_URI:append:aq2432 = " file://defconfig"
SRC_URI:append:bf201 = " file://defconfig"
SRC_URI:append:bg201 = " file://defconfig"
#SRC_URI:append:ap222-zapper = " file://defconfig"
SRC_URI:append:t7 = " file://t7.cfg"
SRC_URI:append:g12b = " file://g12b.cfg"
SRC_URI:append:sm1 = " file://sm1.cfg"

#For common patches
KDIR = "aml-5.15"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/kernel/${KDIR}')}"

# add support partition encryption
SRC_URI:append = "${@bb.utils.contains_any('DISTRO_FEATURES', 'partition-enc partition-enc-local', ' file://partition-enc.cfg', '', d)}"

# add support nand
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', ' file://nand.cfg', '', d)}"

# Enable selinux support in the kernel if the feature is enabled
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', ' file://selinux.cfg', '', d)}"

# add support dm-verity
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', ' file://dm-verity.cfg', '', d)}"

# Enable debian support in the kernel if the feature is enabled
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'amlbian', ' file://amlbian.cfg', '', d)}"

# support booting from nfs if the feature is enabled
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'nfs-boot', ' file://nfs-boot.cfg', '', d)}"

# add support utf8
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'utf8', ' file://utf8.cfg', '', d)}"

# Irdeto IMW
SRC_URI:append:s1a = "${@bb.utils.contains('DISTRO_FEATURES', 'irdeto-imw', ' file://s1a_irdeto_imw_overlay.dtsi', '', d)}"

# sun direct
SRC_URI:append:s1a = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-128m-sei', ' file://s1a_128m_sei_overlay.dtsi', '', d)}"

# Enable Network
SRC_URI:append:s1a = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-network', ' file://zapper-network.cfg file://s1a_enable_ethernet_overlay.dtsi', '', d)}"

# Enable NFS
SRC_URI:append:s1a = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-nfs', ' file://zapper-nfs.cfg', '', d)}"

#For amlbian dtsoverlay
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'amlbian', ' file://partition_debian.dtsi', '', d)}"
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'hdmionly', ' file://hdmionly_overlay.dtsi', '', d)}"
SRC_URI:append:t7 = "${@bb.utils.contains('DISTRO_FEATURES', 'amlbian', ' file://t7_debian_overlay.dtsi', '', d)}"
SRC_URI:append:g12b = "${@bb.utils.contains('DISTRO_FEATURES', 'amlbian', ' file://g12b_debian_overlay.dtsi', '', d)}"
SRC_URI:append:s4 = "${@bb.utils.contains('DISTRO_FEATURES', 'amlbian', ' file://s4_debian_overlay.dtsi', '', d)}"
SRC_URI:append:sc2 = "${@bb.utils.contains('DISTRO_FEATURES', 'amlbian', ' file://sc2_debian_overlay.dtsi', '', d)}"
SRC_URI:append:sm1 = "${@bb.utils.contains('DISTRO_FEATURES', 'amlbian', ' file://sm1_debian_overlay.dtsi', '', d)}"

# add usbci
SRC_URI:append:s1a = "${@bb.utils.contains('DISTRO_FEATURES', 'usbci', ' file://s1a_usbci.cfg', '', d)}"

# videocapture
SRC_URI:append:s1a = "${@bb.utils.contains('DISTRO_FEATURES', 'videocapture', ' file://s1a_videocapture.dtsi', '', d)}"
SRC_URI:append:s1a = "${@bb.utils.contains('DISTRO_FEATURES', 'videocapture', ' file://s1a_videocapture.cfg', '', d)}"

LINUX_VERSION ?= "5.15.137"
LINUX_VERSION:k5.15-u = "5.15.137"
LINUX_VERSION_EXTENSION ?= "-amlogic"
KERNEL_FEATURES:remove = "cfg/fs/vfat.scc"

PR = "r2"
#SRCREV ?="${AUTOREV}"
PV = "${LINUX_VERSION}+git${SRCPV}"

KERNEL_IMAGETYPE = "Image"
KCONFIG_MODE = "alldefconfig"

S = "${WORKDIR}/git"
KBUILD_DEFCONFIG = "${@bb.utils.contains('SRC_URI', 'file://defconfig', '', 'final_defconfig', d)}"
DEPENDS += "elfutils-native lzop-native "

GKI_DEFCONFIG = "gki_defconfig"
GKI_DEFCONFIG:kernel32 = "a32_base_defconfig"
GKI_AMLOGIC_DEFCONFIG = "amlogic_gki.fragment"
GKI_AMLOGIC_DEFCONFIG:kernel32 = "amlogic_a32.fragment"
GKI10_DEFCONFIG = "amlogic_gki.10"
GKIDEBUG_DEFCONFIG = "amlogic_gki.debug"
GCC_DEFCONFIG = "amlogic_gcc64_deconfig"
GCC_DEFCONFIG:kernel32 = "amlogic_gcc32_defconfig"

FINAL_DEFCONFIG_PATH = "${S}/arch/${ARCH}/configs"
GKI_DEFCONFIG_PATH = "${S}/arch/arm64/configs"
GKI_DEFCONFIG_PATH:kernel32 = "${S}/common_drivers/arch/arm/configs"
GKI_AML_CONFIG_PATH = "${S}/common_drivers/arch/${ARCH}/configs"
DTB_DIR = "${B}/common_drivers/arch/${ARCH}/boot/dts/amlogic"

SOC = ""
SOC:sc2 = "sc2"
SOC:s4 = "s4"
SOC:s5 = "s5"
SOC:s7 = "s7"
SOC:s7d = "s7d"
SOC:t3 = "t3"
SOC:t3x = "t3x"
SOC:t5m = "t5m"
SOC:t5w = "t5w"
SOC:t5d = "t5d"
SOC:t7 = "t7"
SOC:s1a = "s1a"


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

do_compile:append () {
    for f in $(ls ${WORKDIR}/*.dtsi 2> /dev/null); do
        output="${f%.dtsi}"
        ${B}/scripts/dtc/dtc -I dts -O dtb -o ${output}.dtbo ${f}
        for DTB in ${KERNEL_DEVICETREE}; do
            ${B}/scripts/dtc/fdtoverlay -i ${DTB_DIR}/${DTB} ${output}.dtbo -o ${DTB_DIR}/${DTB}
        done
    done
}

do_configure:prepend () {
    export COMMON_DRIVERS_DIR=./common_drivers
    install -m 755 ${WORKDIR}/extra_modules_install.sh ${STAGING_KERNEL_DIR}/
}

do_kernel_configme:prepend () {
    export COMMON_DRIVERS_DIR=./common_drivers
}

do_kernel_metadata:prepend:k5.15-u () {
    export COMMON_DRIVERS_DIR=./common_drivers
    cd ${S}
    KERNEL_DIR=${S} ./common_drivers/auto_patch/auto_patch.sh "common14-5.15"
    cd -
}

do_kernel_metadata:prepend () {
    export COMMON_DRIVERS_DIR=./common_drivers
    if [ ! -f ${WORKDIR}/defconfig ];then
        export KBUILD_DEFCONFIG="final_defconfig"
        rm -f ${FINAL_DEFCONFIG_PATH}/${KBUILD_DEFCONFIG}
        if [ "${ARCH}" = "arm" ]; then
            KCONFIG_CONFIG=${FINAL_DEFCONFIG_PATH}/${KBUILD_DEFCONFIG}  ${S}/scripts/kconfig/merge_config.sh -m -r ${GKI_DEFCONFIG_PATH}/${GKI_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GKI_AMLOGIC_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GCC_DEFCONFIG}
        else
            KCONFIG_CONFIG=${FINAL_DEFCONFIG_PATH}/${KBUILD_DEFCONFIG}  ${S}/scripts/kconfig/merge_config.sh -m -r ${GKI_DEFCONFIG_PATH}/${GKI_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GKI_AMLOGIC_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GKI10_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GKIDEBUG_DEFCONFIG} ${GKI_AML_CONFIG_PATH}/${GCC_DEFCONFIG}
        fi
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
KERNEL_MODULE_AUTOLOAD += "v4l2-async"
KERNEL_MODULE_AUTOLOAD += "v4l2-fwnode"

FILES:${PN} += "modules/*"
INSANE_SKIP:${PN} = " installed-vs-shipped"
