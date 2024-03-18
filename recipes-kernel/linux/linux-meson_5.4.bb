inherit kernel
require linux-meson.inc

LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

#We still need patch even in external src mode
SRCTREECOVEREDTASKS:remove = "do_patch"
FILESEXTRAPATHS:prepend := "${THISDIR}/5.4:"
FILESEXTRAPATHS:prepend:aq2432 := "${THISDIR}/5.4/aq2432:"
KERNEL_FEATURES:remove = "cfg/fs/vfat.scc"

# t5d lowmem
FILESEXTRAPATHS:prepend:t5d := "${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', '${THISDIR}/5.4/t5d-lowmem:', '', d)}"
FILESEXTRAPATHS:prepend:aq222 := "${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', '${THISDIR}/5.4/aq222-lowmem:', '', d)}"

KBRANCH = "amlogic-5.4-dev"
#SRC_URI = "git://${AML_GIT_ROOT}/kernel/common.git;protocol=${AML_GIT_PROTOCOL};branch=${KBRANCH};"
#SRC_URI:append = " file://defconfig"
SRC_URI:append = " file://gki-read_ext_module_config.sh"
SRC_URI:append = " file://gki-read_ext_module_predefine.sh"
SRC_URI:append:s4 = " file://s4.cfg"
SRC_URI:append:t7 = " file://t7.cfg"
SRC_URI:append:t5d = " file://t5d.cfg"
SRC_URI:append:t5w = " file://t5w.cfg"
SRC_URI:append:t3 = " file://t3.cfg"
SRC_URI:append:sc2 = " file://sc2.cfg"
SRC_URI:append:aq2432 = " file://defconfig"

# t5d lowmem
SRC_URI:append:t5d = "${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', ' file://defconfig', '', d)}"
SRC_URI:append:aq222 = "${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', ' file://defconfig', '', d)}"

SRC_URI += "file://common.cfg"

# add support nand
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'file://nand.cfg', '', d)}"

# Enable selinux support in the kernel if the feature is enabled
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'file://selinux.cfg', '', d)}"

# add config for system-user
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'system-user', 'file://system-user.cfg', '', d)}"
# Enable trusted-key(TEE backend)in the kernel if FBE is enabled
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'FBE', 'file://trusted-key-tee.cfg', '', d)}"

# add config for OverlayFS
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'OverlayFS', '', 'file://disable_OverlayFS.cfg', d)}"

#SRC_URI:append = " file://meson.scc \
#            file://meson.cfg \
#            file://meson-user-config.cfg \
#            file://systemd.cfg \
#            file://logcat.cfg \
#            file://meson-user-patches.scc "
#SRC_URI:append = " ${@get_patch_list('${THISDIR}/armv7a')}"

#For common patches
KDIR = "aml-5.4"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/kernel/${KDIR}')}"

LINUX_VERSION ?= "5.4.259"
LINUX_VERSION_EXTENSION ?= "-amlogic"

PR = "r2"
#SRCREV ?="${AUTOREV}"
PV = "${LINUX_VERSION}+git${SRCPV}"

KERNEL_IMAGETYPE = "Image"
KCONFIG_MODE = "alldefconfig"

S = "${WORKDIR}/git"
KBUILD_DEFCONFIG = "meson64_a64_R_defconfig"
KBUILD_DEFCONFIG:kernel32 = "meson64_a32_defconfig"

GKI_DEFCONFIG = "meson64_gki_module_config"
#p1 did not use GKI yet.
#Force NO GKI for 32bit kernel
GKI_DEFCONFIG:kernel32 = ""

DEPENDS += " lzop-native "

python () {
    d.setVar("KERNEL_DANGLING_FEATURES_WARN_ONLY","1")
}



gki_module_compile () {
  oe_runmake -C ${STAGING_KERNEL_DIR}/${1} CC="${KERNEL_CC}" LD="${KERNEL_LD}" O=${B} M=${1} KERNEL_SRC=${S}
}

gki_module_install () {
  cd ${B}; rsync -R $(find ${1} -name *.ko | xargs) ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/; cd -
}

do_compile:append () {
  if [ -n "${GKI_DEFCONFIG}" ]; then
    rm -f ${STAGING_KERNEL_DIR}/gki_ext_module_config
    rm -f ${STAGING_KERNEL_DIR}/gki_ext_module_predefine

    #Note, gki_ext_module_config/gki_ext_module_predefine will be used by all kernel module build
    if [ -f ${S}/arch/arm64/configs/${GKI_DEFCONFIG} ]; then
      ${WORKDIR}/gki-read_ext_module_config.sh ${S}/arch/arm64/configs/${GKI_DEFCONFIG} >> ${STAGING_KERNEL_DIR}/gki_ext_module_config
      ${WORKDIR}/gki-read_ext_module_predefine.sh ${S}/arch/arm64/configs/${GKI_DEFCONFIG} >> ${STAGING_KERNEL_DIR}/gki_ext_module_predefine
    fi

    if [ -f ${WORKDIR}/${GKI_DEFCONFIG_ADDON} ]; then
      echo -n " " >> ${STAGING_KERNEL_DIR}/gki_ext_module_config
      echo -n " " >> ${STAGING_KERNEL_DIR}/gki_ext_module_predefine
      ${WORKDIR}/gki-read_ext_module_config.sh ${WORKDIR}/${GKI_DEFCONFIG_ADDON}  >> ${STAGING_KERNEL_DIR}/gki_ext_module_config
      ${WORKDIR}/gki-read_ext_module_predefine.sh ${WORKDIR}/${GKI_DEFCONFIG_ADDON} >> ${STAGING_KERNEL_DIR}/gki_ext_module_predefine
    fi

    export GKI_EXT_MODULE_CONFIG="$(cat ${STAGING_KERNEL_DIR}/gki_ext_module_config)"
    export GKI_EXT_MODULE_PREDEFINE="$(cat ${STAGING_KERNEL_DIR}/gki_ext_module_predefine)"
    gki_module_compile drivers/amlogic
    gki_module_compile sound/soc/amlogic
    gki_module_compile sound/soc/codecs/amlogic
  else
    rm -fr ${STAGING_KERNEL_DIR}/gki_ext_module_config ${STAGING_KERNEL_DIR}/gki_ext_module_predefine
  fi
}

do_install:append () {
  if [ -n "${GKI_DEFCONFIG}" ]; then
    gki_module_install drivers/amlogic
    gki_module_install sound/soc/amlogic
    gki_module_install sound/soc/codecs/amlogic
  fi
}
INSANE_SKIP:${PN} = "installed-vs-shipped"
