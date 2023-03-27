SUMMARY = "Arka zapper application"
LICENSE = "CLOSED"

SRC_URI = "git://${AML_GIT_ROOT}${AML_GIT_ROOT_YOCTO_SUFFIX}/zapper/arka;protocol=${AML_GIT_PROTOCOL};branch=master;nobranch=1"
SRC_URI:append = " file://arka.service "
SRCREV = "${AUTOREV}"

PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake pkgconfig systemd

SYSTEMD_AUTO_ENABLE:${PN} = "enable"

DEPENDS = "directfb dtvkit-release-prebuilt jsoncpp libbinder aml-audio-service libdrm-meson libdrm udev"
RDEPENDS:${PN} = "dtvkit-release-prebuilt aml-audio-service"

OECMAKE_GENERATOR = "Unix Makefiles"
EXTRA_OEMAKE="STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D}  -D_STBLABS_SAT_SCAN_"

INCLUDE_DIRS = " \
    -I${STAGING_DIR_TARGET}${includedir}/directfb/ \
    -I${STAGING_DIR_TARGET}${libdir}/include/ \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm_meson \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm \
    "
TARGET_CFLAGS += "-fPIC -D_REENTRANT ${INCLUDE_DIRS}"

do_install:append() {
	install -D -m 0644 ${WORKDIR}/arka.service ${D}${systemd_unitdir}/system/arka.service
}
SYSTEMD_SERVICE:${PN} = "arka.service"

FILES:${PN} += "${bindir} ${sysconfdir} /usr/share/fonts/ /usr/share/Arka/png ${systemd_unitdir}/system/"
