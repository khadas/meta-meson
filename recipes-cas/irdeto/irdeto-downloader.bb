SUMMARY="Irdeto Module updater for Yocto projects"
DESCRIPTION = "irdeto-downloader"
SECTION = "irdeto-downloader"
LICENSE = "CLOSE"
PV = "git${SRCPV}"
PR = "r0"

PN = 'irdeto-downloader'
#SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "directfb liblog"
RDEPENDS:${PN} = "liblog"

inherit cml1 systemd update-rc.d
INITSCRIPT_NAME = "irdeto-downloader"
INITSCRIPT_PARAMS = "start 80 2 3 4 5 . stop 80 0 6 1 ."

DEPENDS += "${@bb.utils.contains("DISTRO_FEATURES", "irdeto-downloader", " meson-display aml-ubootenv", " " ,d)}"


EXTRA_OEMAKE = "STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D} SYSROOT_DIR=${PKG_CONFIG_SYSROOT_DIR}"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

# HDMI Plug-in/Plug-out Control Header
INCLUDE_DIRS = " \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm_meson/ \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm/ \
    -I${STAGING_DIR_TARGET}${includedir}/display_settings/ \
    "
TARGET_CFLAGS += "${@bb.utils.contains("DISTRO_FEATURES", "irdeto-downloader", " ${INCLUDE_DIRS}", " " ,d)}"
EXTRA_OEMAKE += "${@bb.utils.contains("DISTRO_FEATURES", "irdeto-downloader", " ENABLE_HDMI_CTRL=y", " " ,d)}"


do_configure () {
}

do_compile () {
    echo "************do_compile****************"
    cd ${S}
    oe_runmake -j1 ${EXTRA_OEMAKE} all
}

do_install() {
    echo "************do_install****************"
    install -d ${D}${bindir}
    install -m 0755 -D ${S}/irdeto-downloader ${D}/usr/bin/

    install -d ${D}/usr/share/fonts/
    install -D -m 0644 ${S}/fonts/loader_font.ttf  ${D}/usr/share/fonts/loader_font.ttf

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/files/irdeto-downloader.init ${D}${sysconfdir}/init.d/irdeto-downloader

    install -d ${D}/usr/share/misc
    install -D -m 0644 ${S}/files/tuning_code_data.json ${D}/usr/share/misc/tuning_code_data.json
}

SYSTEMD_SERVICE:${PN} = "irdeto-downloader.service"

FILES:${PN} += "${bindir} ${sysconfdir} /usr/share/fonts/ /usr/share/misc/"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
