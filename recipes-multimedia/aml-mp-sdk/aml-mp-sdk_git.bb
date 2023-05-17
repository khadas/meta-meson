SUMMARY = "aml media hal"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#do_configure[noexec] = "1"
#inherit autotools pkgconfig
inherit cmake
DEPENDS += "aml-amaudioutils liblog aml-libdvr aml-mediahal-sdk aml-cas-hal aml-subtitleserver googletest"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/common/aml_mp_sdk;protocol=${AML_GIT_PROTOCOL};branch=master;"
#SRC_URI = "file://aml-comp/multimedia/aml_mp_sdk;protocol=file;branch=master;"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/aml_mp_sdk')}"
SRCREV = "${AUTOREV}"
#DEPENDS += "libbinder"
S="${WORKDIR}/git/"
RDEPENDS:${PN} += "aml-amaudioutils liblog aml-libdvr aml-mediahal-sdk aml-cas-hal aml-subtitleserver"
EXTRA_OEMAKE = "STAGING_DIR=${STAGING_DIR_TARGET} \
		  TARGET_DIR=${D} \
		"

#do_compile () {
#	cd ${S}
#	oe_runmake -j1 ${EXTRA_OEMAKE} all
#}
#do_install() {
#    install -d ${D}${bindir}
#    install -d ${D}${libdir}
#    install -d ${D}${includedir}
#    cp -rf ${S}/include/Aml_MP        ${D}/usr/include
#    install -m 0644  -D ${S}/libaml_mp_sdk.so ${D}/usr/lib/libaml_mp_sdk.so
#    install -m 0755  -D ${S}/amlMpPlayerDemo  ${D}/usr/bin/
#}


FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "ldflags"
INSANE_SKIP:${PN}-dev = "dev-elf dev-so"

