SUMMARY = "aml swupdate ui"
LICENSE = "CLOSED"
SRC_URI += "file://recovery.bmp \
            file://ota_directfbrc \
            file://recovery.jpg \
"

#SYSTEMD_AUTO_ENABLE = "enable"
#inherit systemd pkgconfig
inherit pkgconfig

DEPENDS += "swupdate"
#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"
S = "${WORKDIR}/git"

# configurate lvgl app needs below two conditions
EXTRA_OEMAKE += "${@bb.utils.contains("SWUPDATE_UI_LIB", "lvgl", "CONFIG_LVGL_APP=y", "" ,d)} OUT_DIR=${B} "
PACKAGECONFIG:append = "${@bb.utils.contains("SWUPDATE_UI_LIB", "lvgl", " lvgl", " directfb" ,d)}"
PACKAGECONFIG[lvgl] = "-lvgl,-no-lvgl,lvgl lv-drivers"
PACKAGECONFIG[directfb] = "-directfb,-no-directfb,directfb"

do_compile(){
    oe_runmake -C ${S} PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config" all
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/swupdateui ${D}${bindir}

    install -d ${D}${sysconfdir}
    case ${PACKAGECONFIG} in
    *lvgl*)
        install -m 0644 ${WORKDIR}/recovery.jpg ${D}/etc
    ;;
    *)
        install -m 0644 ${WORKDIR}/recovery.bmp ${D}/etc
        install -d ${D}${sysconfdir}
        install -m 0644 ${WORKDIR}/ota_directfbrc ${D}/etc
    ;;
    esac

}

do_makeclean() {
    oe_runmake -C ${S} clean
}

addtask do_makeclean before do_clean
FILES:${PN} = " /usr/bin/* /etc/*"
