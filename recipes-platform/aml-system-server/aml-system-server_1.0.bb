SUMMARY = "amlogic system server"
LICENSE = "CLOSED"

inherit systemd pkgconfig

DEPENDS += " aml-platformserver aml-tvserver aml-pqserver curl "
DEPENDS += "aml-dbus"

SRC_URI += "file://system-server.service"
SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"
S = "${WORKDIR}/git"

EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', '', 'CONFIG_DISABLE_BLUETOOTH=y', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'CONFIG_AML_TV=y', 'CONFIG_AML_STB=y', d)}"

do_compile(){
    oe_runmake -C ${S} PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config" all
}

do_install(){
    install -d ${D}${bindir} ${D}${sysconfdir}/dbus-1/system.d/
    install -m 0755 ${S}/system-server ${D}${bindir}
    install -D -m 0644 ${WORKDIR}/system-server.service ${D}${systemd_unitdir}/system/system-server.service
    install -D -m 0644 ${S}/amlogic.yocto.systemserver.conf ${D}${sysconfdir}/dbus-1/system.d/
    if ${@bb.utils.contains("DISTRO_FEATURES", "system-user", "true", "false", d)}
    then
        sed -i '/^\[Service\]/ a User=system' ${D}${systemd_unitdir}/system/system-server.service
    fi
}

do_makeclean() {
    oe_runmake -C ${S} clean
}

addtask do_makeclean before do_clean
FILES:${PN} = " ${bindir}/* ${sysconfdir}/dbus-1/system.d/* "
SYSTEMD_SERVICE:${PN} = "system-server.service "
SYSTEMD_AUTO_ENABLE = "enable"
