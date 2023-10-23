SUMMARY = "dtv test"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"
SRCREV ?= "${AUTOREV}"

inherit cmake systemd update-rc.d

INITSCRIPT_NAME = "dtvtest"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 6 1 ."

SYSTEMD_AUTO_ENABLE:${PN} = "enable"

##SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/aml_commonlib')}"
S = "${WORKDIR}/git/aml-dtv-test"

SRC_URI +="file://dtvtest.service"
SRC_URI +="file://dtvtest.init "

DEPENDS += "liblog libbinder sqlite3 aml-dvb aml-mediahal-sdk"
RDEPENDS_aml-dtv-test += "liblog libbinder sqlite3 aml-dvb aml-mediahal-sdk"

INCLUDE_DIRS = " \
    -I${STAGING_DIR_TARGET}${includedir}/am_adp/ \
    -I${STAGING_DIR_TARGET}${includedir}/am_mw \
    -I${STAGING_DIR_TARGET}${includedir}/amports \
    -I${STAGING_DIR_TARGET}${includedir}/ndk \
    "

TARGET_CFLAGS += "${INCLUDE_DIRS}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 aml_dtv_testserver ${D}${bindir}

	install -d ${D}/${systemd_unitdir}/system
	install -D -m 0644 ${WORKDIR}/dtvtest.service ${D}${systemd_unitdir}/system/dtvtest.service

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/dtvtest.init ${D}${sysconfdir}/init.d/dtvtest
}

SYSTEMD_SERVICE:${PN} = "dtvtest.service"
FILES_${PN} = "${libdir}/* ${bindir}/* ${sysconfdir}/* ${systemd_unitdir}/system/*"

