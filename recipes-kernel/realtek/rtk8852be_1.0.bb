inherit module

SUMMARY = "Realtek 8852be driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/wifi/realtek/drivers/8852be.git;protocol=${AML_GIT_PROTOCOL};branch=p-amlogic"

#SRCREV ?= "${AUTOREV}"
PV = "git${SRCPV}"

do_configure[noexec] = "1"

do_install() {
    WIFIDIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/realtek/wifi
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    mkdir -p ${WIFIDIR}
    install -m 0666 ${S}/rtl8852BE/8852be.ko ${WIFIDIR}
}

FILES:${PN} = "8852be.ko"
# Header file provided by a separate package
DEPENDS += ""

S = "${WORKDIR}/git"

EXTRA_OEMAKE='CONFIG_RTW_AML_YOCTO_OS=y -C ${S}/rtl8852BE M=${S}/rtl8852BE KERNEL_SRC=${STAGING_KERNEL_DIR} KERNELPATH=${STAGING_KERNEL_DIR} ARCH=${ARCH} CROSS_COMPILE=${CROSS_COMPILE} EXTRA_CFLAG=${EXTRA_CFLAGS}'
