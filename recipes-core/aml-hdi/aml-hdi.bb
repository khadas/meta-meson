inherit cmake

DESCRIPTION = "aml_hdi"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

INSANE_SKIP_${PN} += " ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""
PR = "r0"

DEPENDS = "aml-mediahal-sdk libgpiod aml-libdvr aml-subtitleserver meson-display libdrm aml-cas-hal cjson"

INCLUDE_DIRS = " \
    -I${STAGING_DIR_TARGET}${libdir}/include/ \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm_meson \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm \
    -I${STAGING_DIR_TARGET}${includedir}/display_settings \
    "

TARGET_CFLAGS += "${INCLUDE_DIRS}"

SRC_URI = "file://${MESON_ROOT_PATH}/aml-comp/vendor/amlogic/aml_hdi"

S = "${WORKDIR}"
do_install() {
    rm -rf ${MESON_ROOT_PATH}/aml-comp/vendor/amlogic/aml_hdi/oe-*
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -m 0644 source/libaml_hdi.so ${D}${libdir}
    install -m 0755 sample/sample_* ${D}${bindir}
}

FILES_${PN} = "${libdir}/libaml_hdi.so"
FILES_${PN} += "${bindir}/sample_*"
INSANE_SKIP_${PN} = "dev-so"
