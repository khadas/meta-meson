inherit kernel
require linux-meson.inc
FILESEXTRAPATHS_prepend := "${THISDIR}/4.9:"

#For common patches
LINUX_VERSION ?= "4.9.269"
LINUX_VERSION_EXTENSION ?= "-amlogic"

KBUILD_DEFCONFIG_a213y = "meson32_defconfig"
KERNEL_IMAGETYPE = "Image"
KCONFIG_MODE = "alldefconfig"

PR = "r2"
SRCREV ?="${AUTOREV}"
PV = "${LINUX_VERSION}+git${SRCPV}"
S = "${WORKDIR}/git"
