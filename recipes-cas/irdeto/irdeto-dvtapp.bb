SUMMARY="Irdeto dvt app for Yocto projects"
LICENSE = "CLOSED"
#SRCREV = "${AUTOREV}"

PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake pkgconfig systemd update-rc.d

INITSCRIPT_NAME = "irdeto-dvtapp"
INITSCRIPT_PARAMS = "start 80 2 3 4 5 . stop 80 0 6 1 ."

SYSTEMD_AUTO_ENABLE:${PN} = "enable"

DEPENDS = "liblog libbinder directfb irdeto-sdk"
RDEPENDS:${PN} = "jsoncpp libbinder liblog libjpeg-turbo libpng zlib freetype libxml2 libcurl openssl "

OECMAKE_GENERATOR = "Unix Makefiles"
EXTRA_OEMAKE = "STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D} SYSROOT_DIR=${PKG_CONFIG_SYSROOT_DIR}"

do_configure () {
}

do_compile () {
    cd ${S}
    oe_runmake -j1 ${EXTRA_OEMAKE} all
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 -D ${S}/irdeto-dvtapp ${D}/usr/bin/

    install -d ${D}/usr/share/fonts/
    install -D -m 0644 ${S}/fonts/loader_font.ttf  ${D}/usr/share/fonts/loader_font.ttf

    install -d ${D}/${systemd_unitdir}/system
    install -D -m 0644 ${S}/files/irdeto-dvtapp.service ${D}${systemd_unitdir}/system/irdeto-dvtapp.service

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/files/irdeto-dvtapp.init ${D}${sysconfdir}/init.d/irdeto-dvtapp
}

SYSTEMD_SERVICE:${PN} = "irdeto-dvtapp.service"

FILES:${PN} += "${bindir} ${sysconfdir} /usr/share/fonts/ ${systemd_unitdir}/system/"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
