inherit module systemd

SUMMARY = "amlogic arm-isp/aml-isp driver"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS += "linux-meson"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

#SRCREV ?= "${AUTOREV}"
PV = "5.15"
S = "${WORKDIR}/git"

do_configure[noexec] = "1"

EXT_DRIVER_DIR = "common_drivers/drivers"
KOUTPUT_COMMON_DRV_DIR = "${KBUILD_OUTPUT}/${EXT_DRIVER_DIR}"

EXTRA_OEMAKE = "ARCH=${ARCH} CROSS_COMPILE=${CROSS_COMPILE}"

do_User_Build() {
    LIB_PATH=${1}

    # user lib/application build, set OUT_DIR to Makefile
    oe_runmake -C ${S}/${LIB_PATH} OUT_DIR=${B}/${LIB_PATH} ${EXTRA_OEMAKE}
}


do_Kmod_Build() {
    KMOD_PATH=${1}

    # if M use relative path, it can set the build output to KBUILD_OUTPUT folder
    oe_runmake -C ${S}/${KMOD_PATH} M=${EXT_DRIVER_DIR}/${KMOD_PATH} KERNEL_SRC=${STAGING_KERNEL_DIR} ${EXTRA_OEMAKE}
}


do_compile() {
    do_User_Build "app/v4l2_test_arm"
}

#Kmod_Build = "${@Kmod_Build(d)}"

do_compile:append:g12b() {
    ln -sf "${S}/linux_515_g12b" "${STAGING_KERNEL_DIR}/${EXT_DRIVER_DIR}/"

    do_Kmod_Build "linux_515_g12b/v4l2_dev"
    do_Kmod_Build "linux_515_g12b/subdev/iq"
    do_Kmod_Build "linux_515_g12b/subdev/lens"
    do_Kmod_Build "linux_515_g12b/subdev/sensor"
#    do_Kmod_Build "linux_515_g12b/firmware"

    rm "${STAGING_KERNEL_DIR}/${EXT_DRIVER_DIR}/linux_515_g12b"
}

do_compile:append:t7() {
    ###########################################################################
    # for T7B kernel module
    ###########################################################################
    ln -sf "${S}/linux_515_t7" "${STAGING_KERNEL_DIR}/${EXT_DRIVER_DIR}/"

    do_Kmod_Build "linux_515_t7/kernel/subdev/sensor"
    do_Kmod_Build "linux_515_t7/kernel/subdev/iq"
    do_Kmod_Build "linux_515_t7/kernel/subdev/lens"
    do_Kmod_Build "linux_515_t7/kernel/v4l2_dev"

    rm "${STAGING_KERNEL_DIR}/${EXT_DRIVER_DIR}/linux_515_t7"
    ###########################################################################
    # end
    ###########################################################################

    ###########################################################################
    # for T7C kernel module
    ###########################################################################
    ln -sf "${S}/linux_54_t7c" "${STAGING_KERNEL_DIR}/${EXT_DRIVER_DIR}/"
    
    do_Kmod_Build "linux_54_t7c/driver/amlsens"
    do_Kmod_Build "linux_54_t7c/driver/amllens"
    do_Kmod_Build "linux_54_t7c/driver/amlcam"

    rm "${STAGING_KERNEL_DIR}/${EXT_DRIVER_DIR}/linux_54_t7c"
    ###########################################################################
    # end
    ###########################################################################

    ###########################################################################
    # for T7C user library
    ###########################################################################
    do_User_Build "linux_54_t7c/ispHal/media-v4l2"
    do_User_Build "linux_54_t7c/ispHal/sensor_calibration"
    do_User_Build "linux_54_t7c/ispHal/lens"
#    do_User_Build "linux_54_t7c/ispHal/firmware"
    do_User_Build "linux_54_t7c/ispHal/v4l2_test_media"
    do_User_Build "linux_54_t7c/ispHal/media2videoService"
    ###########################################################################
    # end
    ###########################################################################
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
    install -m 0755 ${B}/app/v4l2_test_arm/v4l2_test ${D}${bindir}/v4l2_test
    if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
        install -D -m 0644 ${THISDIR}/files/isp.service ${D}${systemd_unitdir}/system/isp.service
    fi
}

do_install:append:g12b() {
    install -m 0666 ${KOUTPUT_COMMON_DRV_DIR}/linux_515_g12b/v4l2_dev/iv009_isp.ko ${ISPDIR}
    install -m 0666 ${KOUTPUT_COMMON_DRV_DIR}/linux_515_g12b/subdev/iq/iv009_isp_iq.ko ${ISPDIR}
    install -m 0666 ${KOUTPUT_COMMON_DRV_DIR}/linux_515_g12b/subdev/lens/iv009_isp_lens.ko ${ISPDIR}
    install -m 0666 ${KOUTPUT_COMMON_DRV_DIR}/linux_515_g12b/subdev/sensor/iv009_isp_sensor.ko ${ISPDIR}
    install -m 0755 -D ${S}/linux_515_g12b/firmware/iv009_isp.elf ${D}/usr/bin/iv009_isp
    install -m 0755 -D ${THISDIR}/files/g12b/isp_prepare.sh ${D}/etc/isp_prepare.sh
}

do_install:append:t7() {
    install -m 0666 ${KOUTPUT_COMMON_DRV_DIR}/linux_515_t7/kernel/subdev/sensor/iv009_isp_sensor.ko ${ISPDIR}
    install -m 0666 ${KOUTPUT_COMMON_DRV_DIR}/linux_515_t7/kernel/subdev/iq/iv009_isp_iq.ko ${ISPDIR}
    install -m 0666 ${KOUTPUT_COMMON_DRV_DIR}/linux_515_t7/kernel/subdev/lens/iv009_isp_lens.ko ${ISPDIR}
    install -m 0666 ${KOUTPUT_COMMON_DRV_DIR}/linux_515_t7/kernel/v4l2_dev/iv009_isp.ko ${ISPDIR}
    install -m 0755 -D ${S}/linux_515_t7/isp_lib/arm64/iv009_isp.elf ${D}/usr/bin/iv009_isp
    install -m 0666 ${KOUTPUT_COMMON_DRV_DIR}/linux_54_t7c/driver/amlsens/amlsens.ko ${ISPDIR}
    install -m 0666 ${KOUTPUT_COMMON_DRV_DIR}/linux_54_t7c/driver/amlcam/amlcam.ko ${ISPDIR}
    install -m 0666 ${KOUTPUT_COMMON_DRV_DIR}/linux_54_t7c/driver/amllens/dw9714/dw9714.ko ${ISPDIR}
    install -m 0755 ${B}/linux_54_t7c/ispHal/v4l2_test_media/v4l2_test_raw ${D}${bindir}/v4l2_test_raw
    install -m 0755 ${B}/linux_54_t7c/ispHal/media2videoService/media2videoService ${D}${bindir}/media2videoService
    install -m 0666 ${S}/linux_54_t7c/ispHal/firmware/libispaml.so ${D}${libdir}
    install -m 0666 ${B}/linux_54_t7c/ispHal/media-v4l2/libmediaAPI.so ${D}${libdir}
    install -m 0666 ${B}/linux_54_t7c/ispHal/sensor_calibration/libtuning.so ${D}${libdir}
    install -m 0666 ${B}/linux_54_t7c/ispHal/lens/liblens.so ${D}${libdir}
    install -m 0666 ${S}/linux_54_t7c/ispHal/include/*.h ${D}${includedir}
    install -m 0666 ${S}/linux_54_t7c/ispHal/sensor_calibration/imx290/*.h ${D}${includedir}
    install -m 0666 ${S}/linux_54_t7c/ispHal/sensor_calibration/imx415/*.h ${D}${includedir}
    install -m 0755 -D ${THISDIR}/files/t7/isp_prepare.sh ${D}/etc/isp_prepare.sh
}

SYSTEMD_SERVICE:${PN} = "isp.service "

FILES:${PN} = " ${libdir}/* ${bindir}/* /etc/*"

INSANE_SKIP:${PN} = "dev-so"
INSANE_SKIP:${PN}-dev = "dev-elf dev-so"

