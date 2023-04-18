inherit update-rc.d systemd

FILESEXTRAPATHS:prepend := "${THISDIR}/files:${THISDIR}/android-tools-adbd:"

SRC_URI += "file://cutils.mk;subdir=${BPN}"

SRC_URI += "file://adbd.service"
SRC_URI += "file://adbd.init"
SRC_URI += "file://adbd_post.sh"
SRC_URI += "file://adbd_prepare.sh"
SRC_URI += "file://adb_udc_file"
#SRC_URI += "file://0001-adbd-enable-tcpip.patch;patchdir=system/core"

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_AUTO_ENABLE:aq2432 = "${@bb.utils.contains('RELEASE_MODE', 'PROD', 'disable', 'enable', d)}"
SYSTEMD_AUTO_ENABLE:bf201 = "${@bb.utils.contains('RELEASE_MODE', 'PROD', 'disable', 'enable', d)}"
SYSTEMD_SERVICE:${PN} = "adbd.service"

INITSCRIPT_NAME = "adbd"
INITSCRIPT_PARAMS = "start 80 2 3 4 5 . stop 80 0 6 1 ."

TOOLS = " adbd"
ADB_UDC = "ff400000.dwc2_a"
ADB_UDC:s4 = "fdd00000.dwc2_a"
ADB_UDC:sc2 = "fdd00000.dwc2_a"
ADB_UDC:t7 = "fdd00000.crgudc2"
ADB_UDC:t3 = "fdf00000.dwc2_a"

do_install:append() {

    if echo ${TOOLS} | grep -q "cutils" ; then
        install -d ${D}${libdir}
        install -d ${D}${includedir}/cutils
        install -m0644 ${B}/cutils/libcutils.so ${D}${libdir}
        install -D ${S}/system/core/include/cutils/*.h ${D}${includedir}/cutils
    fi

    if echo ${TOOLS} | grep -q "adbd" ; then
        install -d ${D}/etc
        install -d ${D}/${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/adb_udc_file ${D}/etc
        install -m 0755 ${WORKDIR}/adbd_prepare.sh ${D}${bindir}
        install -m 0755 ${WORKDIR}/adbd_post.sh ${D}${bindir}
        install -m 0644 ${WORKDIR}/adbd.service ${D}/${systemd_unitdir}/system
        rm ${D}/${systemd_unitdir}/system/android-tools-adbd.service
        sed -i '/usb_monitor/s/^/#&/g' ${D}${bindir}/adbd_post.sh

        sed "s@ff400000.dwc2_a@${ADB_UDC}@" -i ${D}/etc/adb_udc_file

        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/adbd.init ${D}${sysconfdir}/init.d/adbd
    fi
}
INSANE_SKIP:${PN}-dev += "dev-elf ldflags"
FILES:${PN}-dev += "${includedir}/cutils"
