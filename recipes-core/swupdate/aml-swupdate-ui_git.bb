SUMMARY = "aml swupdate ui"
LICENSE = "CLOSED"

#SYSTEMD_AUTO_ENABLE = "enable"
#inherit systemd pkgconfig
inherit pkgconfig

SRC_URI += "file://recovery.bmp \
            file://ota_directfbrc \
"

DEPENDS += "directfb swupdate"
SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"
S = "${WORKDIR}/git"

do_compile(){
    oe_runmake -C ${S} PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config" all
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/swupdateui ${D}${bindir}

    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/recovery.bmp ${D}/etc

    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/ota_directfbrc ${D}/etc
}

FILES:${PN} = " /usr/bin/* /etc/*"
