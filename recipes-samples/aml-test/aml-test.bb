SUMMARY  = "scripts for nn test"
DESCRIPTION = "Some scripts and file for nn test."
LICENSE  = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

S = "${WORKDIR}"
adla_driver_present = "${@bb.utils.contains('MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS', 'adla-driver', 'yes', 'no', d)}"
mipi_cam_sup = "${@bb.utils.contains('DISTRO_FEATURES', 'arm-isp', 'yes', 'no', d)}"
hdmi_rx_sup = "${@bb.utils.contains('DISTRO_FEATURES', 'mediactrlsrc-hdmi', 'yes', 'no', d)}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    mkdir -p ${D}/home/test

    #for adla platform
    if [ "${adla_driver_present}" = "yes" ]; then
        cp -rf ${THISDIR}/adla-nn-test ${D}/home/test
    fi

    #for hdmirx platform
    if [ "${mipi_cam_sup}" = "yes" ]; then
        cp -rf ${THISDIR}/mipi-camera-test ${D}/home/test
    fi

    #for mipi cam platform
    if [ "${hdmi_rx_sup}" = "yes" ]; then
        cp -rf ${THISDIR}/hdmirx-test ${D}/home/test
    fi

    #for all amlbian and yocto
    cp -rf ${THISDIR}/common ${D}/home/test
    cp -rf ${THISDIR}/playback-test ${D}/home/test
    cp -rf ${THISDIR}/screencapture-test ${D}/home/test
    cp -rf ${THISDIR}/system ${D}/home/test
    cp -rf ${THISDIR}/usb-camera-test ${D}/home/test
    cp -rf ${THISDIR}/video-transcoding-test ${D}/home/test

    chmod -R 755 ${D}/home/test
}

FILES:${PN} = " /home/* "
INSANE_SKIP:${PN} += "file-rdeps"