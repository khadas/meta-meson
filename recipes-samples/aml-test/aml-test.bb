SUMMARY  = "scripts for nn test"
DESCRIPTION = "Some scripts and file for nn test."
LICENSE  = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

S = "${WORKDIR}/git"
adla_driver_present = "${@bb.utils.contains('MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS', 'adla-driver', 'yes', 'no', d)}"
mipi_cam_sup = "${@bb.utils.contains('DISTRO_FEATURES', 'arm-isp', 'yes', 'no', d)}"
hdmi_rx_sup = "${@bb.utils.contains('DISTRO_FEATURES', 'mediactrlsrc-hdmi', 'yes', 'no', d)}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    mkdir -p ${D}/home/test

    #for adla platform
    if [ "${adla_driver_present}" = "yes" ]; then
       cp -rf ${S}/special-test/adla-nn-test ${D}/home/test
    fi

    #for mipi cam platform
    if [ "${mipi_cam_sup}" = "yes" ]; then
       cp -rf ${S}/special-test/mipi-camera-test ${D}/home/test
    fi

    #for hdmirx platform
    if [ "${hdmi_rx_sup}" = "yes" ]; then
       cp -rf ${S}/special-test/hdmirx-test ${D}/home/test
    fi

    #for all SOC supported test case
       cp -rf ${S}/common-test/* ${D}/home/test

    chmod -R 755 ${D}/home/test
}

FILES:${PN} = " /home/* "
INSANE_SKIP:${PN} += "file-rdeps"