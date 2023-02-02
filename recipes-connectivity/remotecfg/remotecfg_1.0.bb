SUMMARY = "Amlogic IR remote configuration setup tool"
DESCRIPTION = "Provides a handy way to setup remote controller key code for Amlogic Soc."
SECTION = "base"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRC_URI += "file://remotecfg.service"

#S = "${WORKDIR}/git"
PR = "r1"

inherit systemd

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${sysconfdir}/remote
    install -d ${D}${systemd_unitdir}/system/
    cd ${S}
    oe_runmake
    install remotecfg ${D}${bindir}
    install remote.tab ${D}${sysconfdir}/remote/
    install remote.cfg ${D}${sysconfdir}/remote/
    install -D -m 0644 ${WORKDIR}/remotecfg.service ${D}${systemd_unitdir}/system/
}

SYSTEMD_AUTO_ENABLE = "disable"
SYSTEMD_SERVICE:${PN} = "remotecfg.service "
FILES:${PN} += "${sysconfdir}/* ${bindir}/*"

