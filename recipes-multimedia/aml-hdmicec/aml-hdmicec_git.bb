SUMMARY = "Hdmi Control Service"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"
SRC_URI +="file://hdmicontrol.service"

DEPENDS = " libbinder liblog"
RDEPENDS_${PN} = " liblog libbinder"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
#inherit autotools pkgconfig systemd
S="${WORKDIR}/git"
ARM_TARGET = "32"
ARM_TARGET_aarch64 = "64"

EXTRA_OEMAKE="STAGING_DIR=${STAGING_DIR_TARGET} \
                TARGET_DIR=${D} \
             "

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -d ${D}${includedir}

    if [ -f "${S}/${ARM_TARGET}/hdmi-control-service" ]; then
        install -m 0755 ${S}/${ARM_TARGET}/hdmi-control-service ${D}${bindir}
        if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
            install -D -m 0644 ${WORKDIR}/hdmicontrol.service ${D}${systemd_unitdir}/system/hdmicontrol.service
        fi
    fi
    if [ -f "${S}/${ARM_TARGET}/libcec.so" ]; then
        install -m 0755 ${S}/${ARM_TARGET}/libcec.so ${D}${libdir}
    fi
    install -m 0644 ${S}/include/*.h ${D}${includedir}
}
#SYSTEMD_SERVICE_${PN} = "hdmicontrol.service "

FILES_${PN} = "${libdir}/* ${bindir}/*"
FILES_${PN}-dev = "${includedir}/* "
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP_${PN}-dev = "dev-so ldflags dev-elf"
