inherit cmake

DESCRIPTION = "aml_hdi"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

INSANE_SKIP_${PN} += " ldflags"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""
PR = "r0"

DEPENDS = "aml-mediahal-sdk libgpiod aml-libdvr aml-subtitleserver meson-display libdrm aml-cas-hal cjson"
RDEPENDS:${PN} = "aml-mediahal-sdk aml-libdvr"

INCLUDE_DIRS = " \
    -I${STAGING_DIR_TARGET}${libdir}/include/ \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm_meson \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm \
    -I${STAGING_DIR_TARGET}${includedir}/display_settings \
    "

TARGET_CFLAGS += "${INCLUDE_DIRS}"

#SRC_URI = "file://${MESON_ROOT_PATH}/aml-comp/vendor/amlogic/aml_hdi"

EXTRA_OECMAKE = ""

S = "${WORKDIR}"
do_install() {
    mkdir -p ${D}${includedir}
    install -D -m 0644 ${S}/include/*.h  ${D}${includedir}/
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -m 0644 source/libaml_hdi.so ${D}${libdir}
    install -m 0755 sample/sample_* ${D}${bindir}
}

FILES_${PN} = "${libdir}/libaml_hdi.so"
FILES_${PN} += "${bindir}/sample_*"
INSANE_SKIP:${PN} = "dev-so already-stripped"
