SUMMARY = "aml cas hal"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/cas/cas-hal.git;protocol=${AML_GIT_PROTOCOL};branch=master"
SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/cas-hal')}"
DEPENDS += "aml-audio-service aml-mediahal-sdk aml-libdvr liblog"

do_configure[noexec] = "1"
inherit autotools pkgconfig
S="${WORKDIR}/git"
RDEPENDS:${PN} += "aml-audio-service aml-mediahal-sdk aml-libdvr liblog"
EXTRA_OEMAKE="STAGING_DIR=${STAGING_DIR_TARGET}\
	      TARGET_DIR=${D} \
	      "
 
do_compile() {
    cd ${S}
    oe_runmake  all
}
do_install() {
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -m 0755 -d ${D}${includedir}/libamcas
    install -m 0755 ${S}/libamcas.a ${D}${libdir}
    install -m 0644 ${S}/libamcas/include/* ${D}${includedir}/libamcas/

    install -m 0755 -d ${D}${includedir}/liblinuxdvb_port
    install -m 0755 ${S}/liblinuxdvb_port.a ${D}${libdir}
    install -m 0644 ${S}/liblinuxdvb_port/include/* ${D}${includedir}/liblinuxdvb_port/

    install -m 0755 ${S}/cas_hal_test_bin ${D}${bindir}
}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
