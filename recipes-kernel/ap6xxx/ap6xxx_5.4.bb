inherit module

SUMMARY = "Broadcom AP6xxx driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/wifi/broadcom/drivers/ap6xxx.git;protocol=${AML_GIT_PROTOCOL};nobranch=1"

#SRCREV = "e0599cf99ebac232e73d4c76aa7022f0db21921e"
DHD_MODULE_PATH="bcmdhd.100.10.315.x"
AP6XXX_KCONFIGS = "KCPPFLAGS='-DCONFIG_BCMDHD_FW_PATH=\"/etc/wifi/fw_bcmdhd.bin\" -DCONFIG_BCMDHD_NVRAM_PATH=\"/etc/wifi/nvram.txt\"'"
#AP6XXX_KCONFIGS = "KCPPFLAGS='-DCONFIG_BCMDHD_FW_PATH=\"/etc/wifi/fw_bcmdhd.bin\" -DCONFIG_BCMDHD_NVRAM_PATH=\"/etc/wifi/nvram.txt\" $(cat{STAGING_KERNEL_DIR}/gki_ext_module_predefine)'"

do_configure[noexec] = "1"

do_install() {
    WIFIDIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/broadcom/wifi
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    mkdir -p ${WIFIDIR}
    install -m 0666 ${S}/${DHD_MODULE_PATH}/dhd.ko ${WIFIDIR}
}

#do_compile:append() {
#    oe_runmake -C ${STAGING_KERNEL_DIR} M="${S}/${DHD_MODULE_PATH}" ${AP6XXX_KCONFIGS} modules
#}

FILES:${PN} = "dhd.ko"
# Header file provided by a separate package
DEPENDS += ""

S = "${WORKDIR}/git"

EXTRA_OEMAKE='-C ${STAGING_KERNEL_DIR} M="${S}/${DHD_MODULE_PATH}" ${AP6XXX_KCONFIGS} modules CONFIG_DHD_USE_STATIC_BUF=y CONFIG_BCMDHD_SDIO=y'
