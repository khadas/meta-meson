inherit kernel
require linux-meson.inc

LIC_FILES_CHKSUM = "file://${THISDIR}/../../license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

#We still need patch even in external src mode
SRCTREECOVEREDTASKS_remove = "do_patch"
#FILESEXTRAPATHS_prepend := "${THISDIR}/5.4:"
FILESEXTRAPATHS_prepend_t7 := "${THISDIR}/5.4_t7:"
FILESEXTRAPATHS_prepend_sc2 := "${THISDIR}/5.4_sc2:"

KBRANCH = "amlogic-5.4-dev"
#SRC_URI = "git://${AML_GIT_ROOT}/kernel/common.git;protocol=${AML_GIT_PROTOCOL};branch=${KBRANCH};"
SRC_URI_append = " file://defconfig"

#SRC_URI_append = " file://meson.scc \
#            file://meson.cfg \
#            file://meson-user-config.cfg \
#            file://systemd.cfg \
#            file://logcat.cfg \
#            file://meson-user-patches.scc "
#SRC_URI_append = " ${@get_patch_list('${THISDIR}/armv7a')}"

#For common patches
KDIR = "aml-5.4"
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/kernel/${KDIR}')}"

LINUX_VERSION ?= "5.4.61"
LINUX_VERSION_EXTENSION ?= "-amlogic"

PR = "r2"

SRCREV ?="${AUTOREV}"

PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE = "(mesontm2_*|mesonsc2_*|mesont7_*)"

KERNEL_IMAGETYPE = "Image"
KCONFIG_MODE = "alldefconfig"

S = "${WORKDIR}/git"
