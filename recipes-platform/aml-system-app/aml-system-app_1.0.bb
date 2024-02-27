SUMMARY = "amlogic system app"
LICENSE = "CLOSED"

inherit systemd pkgconfig

DEPENDS += " lvglui virtual/egl aml-platformserver aml-tvserver rapidjson aml-appmanager aml-system-server"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"
S = "${WORKDIR}/git"

EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', '', 'CONFIG_DISABLE_BLUETOOTH=y', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'CONFIG_AML_TV=y', 'CONFIG_AML_STB=y', d)}"

do_compile(){
    oe_runmake -C ${S} PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config" all
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/system-app ${D}${bindir}
    install -d ${D}/usr/share/pixmaps/sysapp/
    install -d ${D}/usr/share/pixmaps/sysapp/bt-help-raw-gif
    install -d ${D}/usr/share/pixmaps/sysapp/network-help-raw-gif
    install -D -m 0644 ${S}/image/* ${D}/usr/share/pixmaps/sysapp/
    install -D -m 0644 ${S}/bt-help-raw-gif/* ${D}/usr/share/pixmaps/sysapp/bt-help-raw-gif/
    install -D -m 0644 ${S}/network-help-raw-gif/* ${D}/usr/share/pixmaps/sysapp/network-help-raw-gif/
}

do_makeclean() {
    oe_runmake -C ${S} clean
}

addtask do_makeclean before do_clean
FILES:${PN} = " /usr/bin/* "
FILES:${PN} += " /usr/share/pixmaps/sysapp/* "
FILES:${PN} += " /usr/share/pixmaps/sysapp/bt-help-raw-gif/* "
FILES:${PN} += " /usr/share/pixmaps/sysapp/network-help-raw-gif/* "
SYSTEMD_AUTO_ENABLE = "enable"
