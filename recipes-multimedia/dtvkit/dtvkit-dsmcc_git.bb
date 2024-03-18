SUMMARY = "dtvkit dsmcc"

LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://${COREBASE}/../${AML_META_LAYER}/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"


#SRC_URI = "git://${AML_GIT_ROOT}/DTVKit/DSMCC;protocol=${AML_GIT_PROTOCOL};branch=p-amlogic"


#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

DEPENDS = "libbinder liblog libjpeg-turbo libpng zlib freetype sqlite3 libxml2 curl freetype openssl aml-mp-sdk"


do_configure[noexec] = "1"
inherit autotools pkgconfig
S="${WORKDIR}/git"


EXTRA_OEMAKE="_DTVKIT_ROOT=${S}\
        DTVKIT_CC=${TARGET_PREFIX}gcc \
        DTVKIT_AR=${TARGET_PREFIX}ar \
        DTVKIT_JPEG_INCLUDE_PATH=${STAGING_DIR_TARGET}/usr/include   \
        DTVKIT_PNG_INCLUDE_PATH=${STAGING_DIR_TARGET}/usr/include/   \
        DTVKIT_ZLIB_INCLUDE_PATH=${STAGING_DIR_TARGET}/usr/include   \
        DTVKIT_LIBXML2_INCLUDE_PATH=${STAGING_DIR_TARGET}/usr/include   \
        DTVKIT_CURL_INCLUDE_PATH=${STAGING_DIR_TARGET}/usr/include   \
        DTVKIT_OSSL_INCLUDE_PATH=${STAGING_DIR_TARGET}/usr/include/openssl   \
        DTVKIT_FREETYPE_INCLUDE_PATH=${STAGING_DIR_TARGET}/usr/include/freetype2 \
        DTVKIT_SDK_INCLUDE_PATH=${STAGING_DIR_TARGET}   \
        RDK_INCLUDES='${STAGING_DIR_TARGET}/usr/include ${STAGING_DIR_TARGET}/usr/include/libdvr  ${STAGING_DIR_TARGET}/usr/include/Aml_MP  ${STAGING_DIR_TARGET}/usr/include/libamcas '   \
        DTVKIT_ADDITIONAL_COMPILER_OPTIONS='-Wall -Wextra -march=armv7-a -mthumb -mfpu=neon -mfloat-abi=hard ${TOOLCHAIN_OPTIONS}' \
        DTVKIT_OUTPUT_DIR=${B} \
        DTVKIT_INSTALL_DIR=${D}/usr \
        "


do_compile() {
    cd ${B}
    oe_runmake -C ${S}
}

do_install() {
    cd ${B}
    oe_runmake -C ${S} install

}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
