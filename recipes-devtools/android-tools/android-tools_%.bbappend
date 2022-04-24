FILESEXTRAPATHS_prepend := "${THISDIR}/files:${THISDIR}/android-tools-adbd:"

SRC_URI += "file://cutils.mk;subdir=${BPN}"

SRC_URI += "file://adbd.service"
SRC_URI += "file://adbd_post.sh"
SRC_URI += "file://adbd_prepare.sh"
SRC_URI += "file://adb_udc_file"
#SRC_URI += "file://0001-adbd-enable-tcpip.patch;patchdir=system/core"

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE_${PN} = "adbd.service"

TOOLS = " adbd"

do_install_append() {

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

        echo "MACHINE_ARCH is ${MACHINE_ARCH}"
        case ${MACHINE_ARCH} in
        mesona213y*)
            sed 's@ff400000.dwc2_a@c9040000.dwc2_a@' -i ${D}/etc/adb_udc_file
        ;;
        mesonc1*)
            sed 's@ff400000.dwc2_a@ff500000.dwc2_a@' -i ${D}/etc/adb_udc_file
        ;;
        mesonsc2* | mesons4*)
            sed 's@ff400000.dwc2_a@fdd00000.dwc2_a@' -i ${D}/etc/adb_udc_file
        ;;
        mesont7*)
            sed 's@ff400000.dwc2_a@fdd00000.crgudc2@' -i ${D}/etc/adb_udc_file
        ;;
        mesont3*)
            sed 's@ff400000.dwc2_a@fdf00000.dwc2_a@' -i ${D}/etc/adb_udc_file
        ;;
        mesont5d* | mesont5w*)
            echo "Using default"
        ;;
        esac
    fi
}
INSANE_SKIP_${PN}-dev += "dev-elf ldflags"
FILES_${PN}-dev += "${includedir}/cutils"
