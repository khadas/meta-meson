SUMMARY = "Arka zapper application"
LICENSE = "CLOSED"

SRC_URI = "git://${AML_GIT_ROOT}${AML_GIT_ROOT_YOCTO_SUFFIX}/zapper/arka;protocol=${AML_GIT_PROTOCOL};branch=master;nobranch=1"
SRC_URI_append = " file://arka.service "
SRCREV = "${AUTOREV}"

PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake pkgconfig systemd

SYSTEMD_AUTO_ENABLE_${PN} = "enable"
DEPENDS = "directfb dtvkit-release-prebuilt jsoncpp libbinder"
OECMAKE_GENERATOR = "Unix Makefiles"
EXTRA_OEMAKE="STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D}  -D_STBLABS_SAT_SCAN_"

INCLUDE_DIRS = " \
    -I${STAGING_DIR_TARGET}${includedir}/directfb/ \
    -I${STAGING_DIR_TARGET}${libdir}/include/ \
    "
TARGET_CFLAGS += "-fPIC -D_REENTRANT ${INCLUDE_DIRS}"

do_install_append() {
	install -D -m 0644 ${WORKDIR}/arka.service ${D}${systemd_unitdir}/system/arka.service
}
SYSTEMD_SERVICE_${PN} = "arka.service"

FILES_${PN} += "${bindir} /usr/share/fonts/ /usr/share/Arka/png ${systemd_unitdir}/system/"
