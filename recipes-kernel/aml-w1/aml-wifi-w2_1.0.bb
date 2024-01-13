inherit module

SUMMARY = "amlogic aml_wifi driver"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/wifi/amlogic/drivers/w2.git;protocol=${AML_GIT_PROTOCOL};branch=r-amlogic"
SRC_URI:append = " file://w2_bt_fw_uart.bin"

SRCREV ?= "${AUTOREV}"
PV = "git${SRCPV}"

do_configure[noexec] = "1"

do_install() {
    WIFIDIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/amlogic/wifi
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    mkdir -p ${WIFIDIR}
    install -m 0666 ${S}/aml_drv/fullmac/w2.ko ${WIFIDIR}
    install -m 0666 ${S}/aml_drv/fullmac/w2_comm.ko ${WIFIDIR}

    mkdir -p ${D}/lib/firmware/
    install -m 0644 ${S}/common/wifi_w2_fw_sdio.bin ${D}/lib/firmware/
    mkdir -p ${D}/etc/bluetooth/aml
	install -m 0644 ${WORKDIR}/w2_bt_fw_uart.bin ${D}/etc/bluetooth/aml/
}

FILES:${PN} = " /lib/firmware/* /etc/bluetooth/aml/*"
# Header file provided by a separate package
DEPENDS += ""

S = "${WORKDIR}/git"

EXTRA_OEMAKE='-C ${S}/aml_drv M=${S}/aml_drv CONFIG_BUILDROOT=y CONFIG_ANDROID_GKI=y KERNELDIR=${STAGING_KERNEL_DIR} ARCH=${ARCH} CROSS_COMPILE=${CROSS_COMPILE}'
