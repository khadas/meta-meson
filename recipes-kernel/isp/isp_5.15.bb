inherit module systemd

SUMMARY = "amlogic arm-isp/aml-isp driver"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS += "linux-meson"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRCREV ?= "${AUTOREV}"
PV = "5.15"
S = "${WORKDIR}/git"

do_configure[noexec] = "1"

EXTRA_OEMAKE = "KERNEL_SRC=${STAGING_KERNEL_DIR} ARCH=${ARCH} CROSS_COMPILE=${CROSS_COMPILE}"

do_compile() {
    oe_runmake -C ${S}/app/v4l2_test_arm M=${S}/app/v4l2_test_arm ${EXTRA_OEMAKE}
}

do_compile:append() {
}

do_compile:append:g12b() {
    oe_runmake -C ${S}/linux_515_g12b/v4l2_dev M=${S}/linux_515_g12b/v4l2_dev ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_515_g12b/subdev/iq M=${S}/linux_515_g12b/subdev/iq ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_515_g12b/subdev/lens M=${S}/linux_515_g12b/subdev/lens ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_515_g12b/subdev/sensor M=${S}/linux_515_g12b/subdev/sensor ${EXTRA_OEMAKE}
    #oe_runmake -C ${S}/linux_515_g12b/firmware M=${S}/linux_515_g12b/firmware ${EXTRA_OEMAKE}
}

do_compile:append:t7() {
    oe_runmake -C ${S}/linux_515_t7/kernel/subdev/sensor M=${S}/linux_515_t7/kernel/subdev/sensor ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_515_t7/kernel/subdev/iq M=${S}/linux_515_t7/kernel/subdev/iq ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_515_t7/kernel/subdev/lens M=${S}/linux_515_t7/kernel/subdev/lens ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_515_t7/kernel/v4l2_dev M=${S}/linux_515_t7/kernel/v4l2_dev ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_54_t7c/driver/amlsens M=${S}/linux_54_t7c/driver/amlsens ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_54_t7c/driver/amllens M=${S}/linux_54_t7c/driver/amllens ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_54_t7c/driver/amlcam M=${S}/linux_54_t7c/driver/amlcam ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_54_t7c/ispHal/media-v4l2 M=${S}/linux_54_t7c/ispHal/media-v4l2 ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_54_t7c/ispHal/sensor_calibration M=${S}/linux_54_t7c/ispHal/sensor_calibration ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_54_t7c/ispHal/lens M=${S}/linux_54_t7c/ispHal/lens ${EXTRA_OEMAKE}
    #oe_runmake -C ${S}/linux_54_t7c/ispHal/firmware M=${S}/linux_54_t7c/ispHal/firmware ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_54_t7c/ispHal/v4l2_test_media M=${S}/linux_54_t7c/ispHal/v4l2_test_media ${EXTRA_OEMAKE}
    oe_runmake -C ${S}/linux_54_t7c/ispHal/media2videoService M=${S}/linux_54_t7c/ispHal/media2videoService ${EXTRA_OEMAKE}
}

do_install() {
    #can`t put into ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/amlogic/isp,Otherwise,v009_isp.ko will be installed firstly,system will panic
    #ISPDIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/amlogic/isp
    ISPDIR=${D}/lib/modules/isp
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    install -d ${ISPDIR}
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -d ${D}/etc
    install -m 0755 ${S}/app/v4l2_test_arm/v4l2_test ${D}${bindir}/v4l2_test
    oe_runmake -C ${S}/app/v4l2_test_arm/ clean
    if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
        install -D -m 0644 ${THISDIR}/files/isp.service ${D}${systemd_unitdir}/system/isp.service
    fi
}

do_install:append() {
}

do_install:append:g12b() {
    install -m 0666 ${S}/linux_515_g12b/v4l2_dev/iv009_isp.ko ${ISPDIR}
    install -m 0666 ${S}/linux_515_g12b/subdev/iq/iv009_isp_iq.ko ${ISPDIR}
    install -m 0666 ${S}/linux_515_g12b/subdev/lens/iv009_isp_lens.ko ${ISPDIR}
    install -m 0666 ${S}/linux_515_g12b/subdev/sensor/iv009_isp_sensor.ko ${ISPDIR}
    install -m 0755 -D ${S}/linux_515_g12b/firmware/iv009_isp.elf ${D}/usr/bin/iv009_isp
    install -m 0755 -D ${THISDIR}/files/g12b/isp_prepare.sh ${D}/etc/isp_prepare.sh
    oe_runmake -C ${S}/linux_515_g12b/v4l2_dev M=${S}/linux_515_g12b/v4l2_dev ${EXTRA_OEMAKE} clean
    oe_runmake -C ${S}/linux_515_g12b/subdev/iq M=${S}/linux_515_g12b/subdev/iq ${EXTRA_OEMAKE} clean
    oe_runmake -C ${S}/linux_515_g12b/subdev/lens M=${S}/linux_515_g12b/subdev/lens ${EXTRA_OEMAKE} clean
    oe_runmake -C ${S}/linux_515_g12b/subdev/sensor M=${S}/linux_515_g12b/subdev/sensor ${EXTRA_OEMAKE} clean
}

do_install:append:t7() {
    install -m 0666 ${S}/linux_515_t7/kernel/subdev/sensor/iv009_isp_sensor.ko ${ISPDIR}
    install -m 0666 ${S}/linux_515_t7/kernel/subdev/iq/iv009_isp_iq.ko ${ISPDIR}
    install -m 0666 ${S}/linux_515_t7/kernel/subdev/lens/iv009_isp_lens.ko ${ISPDIR}
    install -m 0666 ${S}/linux_515_t7/kernel/v4l2_dev/iv009_isp.ko ${ISPDIR}
    install -m 0755 -D ${S}/linux_515_t7/isp_lib/arm64/iv009_isp.elf ${D}/usr/bin/iv009_isp
    install -m 0666 ${S}/linux_54_t7c/driver/amlsens/amlsens.ko ${ISPDIR}
    install -m 0666 ${S}/linux_54_t7c/driver/amlcam/amlcam.ko ${ISPDIR}
    #install -m 0666 ${S}/linux_54_t7c/driver/sensor/dw9714/dw9714.ko ${ISPDIR}
    install -m 0755 ${S}/linux_54_t7c/ispHal/v4l2_test_media/v4l2_test_raw ${D}${bindir}/v4l2_test_raw
    install -m 0755 ${S}/linux_54_t7c/ispHal/media2videoService/media2videoService ${D}${bindir}/media2videoService
    install -m 0666 ${S}/linux_54_t7c/ispHal/firmware/libispaml.so ${D}${libdir}
    install -m 0666 ${S}/linux_54_t7c/ispHal/media-v4l2/libmediaAPI.so ${D}${libdir}
    install -m 0666 ${S}/linux_54_t7c/ispHal/sensor_calibration/libtuning.so ${D}${libdir}
    install -m 0666 ${S}/linux_54_t7c/ispHal/lens/liblens.so ${D}${libdir}
    install -m 0666 ${S}/linux_54_t7c/ispHal/include/*.h ${D}${includedir}
    install -m 0666 ${S}/linux_54_t7c/ispHal/sensor_calibration/imx290/*.h ${D}${includedir}
    install -m 0666 ${S}/linux_54_t7c/ispHal/sensor_calibration/imx415/*.h ${D}${includedir}
    install -m 0755 -D ${THISDIR}/files/t7/isp_prepare.sh ${D}/etc/isp_prepare.sh
    oe_runmake -C ${S}/linux_515_t7/kernel/subdev/sensor M=${S}/linux_515_t7/kernel/subdev/sensor ${EXTRA_OEMAKE} clean
    oe_runmake -C ${S}/linux_515_t7/kernel/subdev/iq M=${S}/linux_515_t7/kernel/subdev/iq ${EXTRA_OEMAKE} clean
    oe_runmake -C ${S}/linux_515_t7/kernel/subdev/lens M=${S}/linux_515_t7/kernel/subdev/lens ${EXTRA_OEMAKE} clean
    oe_runmake -C ${S}/linux_515_t7/kernel/v4l2_dev M=${S}/linux_515_t7/kernel/v4l2_dev ${EXTRA_OEMAKE} clean
    oe_runmake -C ${S}/linux_54_t7c/driver/amlsens M=${S}/linux_54_t7c/driver/amlsens ${EXTRA_OEMAKE} clean
    oe_runmake -C ${S}/linux_54_t7c/driver/amlcam M=${S}/linux_54_t7c/driver/amlcam ${EXTRA_OEMAKE} clean
    oe_runmake -C ${S}/linux_54_t7c/driver/amllens M=${S}/linux_54_t7c/driver/amllens ${EXTRA_OEMAKE} clean
    oe_runmake -C ${S}/linux_54_t7c/ispHal/ clean
}

SYSTEMD_SERVICE:${PN} = "isp.service "

FILES:${PN} = " ${libdir}/* ${bindir}/* /etc/*"

INSANE_SKIP:${PN} = "dev-so"
INSANE_SKIP:${PN}-dev = "dev-elf dev-so"

