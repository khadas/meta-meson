FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
westeros-soc = "westeros-soc-amlogic"
PACKAGECONFIG = "incapp inctest increndergl incsbprotocol incldbprotocol xdgv5 incplayer inclexpsyncprotocol"
inherit systemd
RDEPENDS:${PN} += "libwayland-egl.so "
SRC_URI:append = " file://essrmgr.conf "
SRC_URI:append = " file://westeros-init "
SRC_URI:append = " file://westeros.service "
PROVIDES += " essrmgr.conf"
RPROVIDES:${PN} += " essrmgr.conf"
# EXTERNALSRC_pn-westeros=""
include westeros_rev.inc
PARALLEL_MAKE = ""

LIC_FILES_CHKSUM = "file://${LICENSE_LOCATION};md5=8fb65319802b0c15fc9e0835350ffa02"
PACKAGECONFIG[incplayer] = "--enable-player=yes"
PACKAGECONFIG[incldbprotocol] = "--enable-ldbprotocol=yes"
PACKAGECONFIG[inclexpsyncprotocol] = "--enable-lexpsyncprotocol=yes"
PACKAGECONFIG[essos] = "--enable-essos=yes,--enable-essos=no"

do_compile:prepend() {
    export SCANNER_TOOL=${STAGING_BINDIR_NATIVE}/wayland-scanner
    oe_runmake -C ${S}/protocol
    oe_runmake -C ${S}/linux-dmabuf/protocol
    oe_runmake -C ${S}/linux-expsync/protocol
}


do_install:append() {
   install -D -m 0644 ${S}/systemd/westeros-env ${D}${sysconfdir}/default/westeros-env
   echo "WESTEROS_SINK_USE_ESSRMGR=1" >> ${D}${sysconfdir}/default/westeros-env
   if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "no" ]; then
       install -D -m 0755 ${S}/systemd/westeros.sysvinit ${D}${sysconfdir}/init.d/westeros
   fi

   install -D -m 0644 ${WORKDIR}/essrmgr.conf ${D}${sysconfdir}/default/essrmgr.conf
   if ${@bb.utils.contains("DISTRO_FEATURES", "appmanager", "true", "false", d)}; then
     install -D -m 0755 ${WORKDIR}/westeros-init ${D}${bindir}/westeros-init
     install -d ${D}${systemd_unitdir}/system
     install -m 0644 ${WORKDIR}/westeros.service ${D}${systemd_unitdir}/system/
   fi
}

do_install:append:aq2432() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'westeros', 'true', 'false', d)};then
      sed -i 's/1920x1080/1280x720/g' ${D}${bindir}/westeros-init
    fi
}

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'appmanager', 'westeros.service', '', d)}"
