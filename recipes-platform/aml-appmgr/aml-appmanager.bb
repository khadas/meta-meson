SUMMARY = "application manager"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

inherit systemd

SRC_URI = "file://appmgr.service"
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'weston', 'file://appmgr-weston.env', '', d)}"
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'westeros', 'file://appmgr-westeros.env', '', d)}"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

#SRCREV ?= "${AUTOREV}"

S = "${WORKDIR}/git"
do_configure[noexec] = "1"
BUILDDIR = "${WORKDIR}/build"

EXTRA_OEMAKE="BUILDDIR=${BUILDDIR} STAGING_DIR_TARGET=${STAGING_DIR_TARGET}"


DEPENDS += "wayland wayland-protocols aml-dbus readline virtual/egl aml-platformserver"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'westeros', 'westeros-simpleshell westeros', '', d)}"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'weston', 'weston', '', d)}"

RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'westeros', 'westeros-simpleshell westeros', '', d)}"

LDFLAGS += " \
           ${@bb.utils.contains('DISTRO_FEATURES', 'weston', '-lweston-desktop-10', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'weston', '-L${STAGING_LIBDIR}/weston/', '', d)} \
           -L${WORKDIR}/build/ \
           "

CFLAGS += " \
           ${@bb.utils.contains('DISTRO_FEATURES', 'weston', '-DHAVE_WESTON', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'westeros', '-DHAVE_WESTEROS', '', d)} \
           "

do_compile(){
    cd ${S}
    oe_runmake all
}

do_install() {
    install -d ${D}${includedir}
    cp -af ${S}/essos/essos-app.h ${D}${includedir}
    cp -af ${S}/essos/essos.h ${D}${includedir}
    cp -af ${S}/essos/essos-system.h ${D}${includedir}
    cp -af ${S}/essos/essos-game.h ${D}${includedir}
    cp -af ${S}/appmgr_dbus.h ${D}${includedir}
    cp -af ${S}/essos/peer_dbus.h ${D}${includedir}
    cp -af ${S}/essos/essos-resmgr.h ${D}${includedir}
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/appmgr.service ${D}${systemd_unitdir}/system/
    install -d ${D}${sysconfdir}
    if ${@bb.utils.contains('DISTRO_FEATURES', 'westeros', 'true', 'false', d)};then
      sed -i '/compositor.service/ s/compositor/westeros/g' ${D}${systemd_unitdir}/system/appmgr.service
      install -m 0644 ${WORKDIR}/appmgr-westeros.env ${D}${sysconfdir}/appmgr.env
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'weston', 'true', 'false', d)};then
      sed -i '/compositor.service/ s/compositor/weston/g' ${D}${systemd_unitdir}/system/appmgr.service
      install -m 0644 ${WORKDIR}/appmgr-weston.env ${D}${sysconfdir}/appmgr.env
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'UI_720P', 'true', 'false', d)};then
      sed -i '/WESTEROS_GL_GRAPHICS_MAX_SIZE/d' ${D}${sysconfdir}/appmgr.env
      sed -i '$aWESTEROS_GL_GRAPHICS_MAX_SIZE=1280x720' ${D}${sysconfdir}/appmgr.env
    fi
    cp -af ${WORKDIR}/build/libessos.so ${D}${libdir}
    cp -af ${WORKDIR}/build/libessosrmgr.so ${D}${libdir}
    cp -af ${WORKDIR}/build/appmgr ${D}${bindir}
    cp -af ${WORKDIR}/build/sample ${D}${bindir}
    install -d ${D}${sysconfdir}/app_list
    cp -af ${S}/app_list.txt ${D}/${sysconfdir}/app_list/
    install -d ${D}/etc/dbus-1/system.d
    install -m 0644 ${S}/amlogic.yocto.appmgr.conf ${D}/etc/dbus-1/system.d/
}

SYSTEMD_SERVICE:${PN} = "appmgr.service"

FILES:${PN} += "${libdir}/* ${bindir}/* ${includedir}/* ${sysconfdir}*"
#FILES:${PN}-dev += "${includedir}/* ${libdir}/pkgconfig/* ${libdir}/*"

FILES_SOLIBSDEV = ""

INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"

