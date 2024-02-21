SUMMARY = "dtvkit android-rpcservice"

LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://${COREBASE}/../${AML_META_LAYER}/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

ARM_TARGET = "lib32"
ARM_TARGET:aarch64 = "lib64"
#SRC_URI = "git://${AML_GIT_ROOT}/DTVKit/android-rpcservice;protocol=${AML_GIT_PROTOCOL};branch=p-amlogic"
#dtvkit-ciplus dtvkit-dsmcc  dtvkit-mheg5

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

DEPENDS = "dtvkit-amlogic dtvkit-ciplus dtvkit-dsmcc  dtvkit-mheg5  dtvkit-dvbcore dtvkit-atv dtvkit-atsc  jsoncpp  libbinder liblog libjpeg-turbo libpng zlib freetype sqlite3 libxml2 curl freetype openssl aml-mp-sdk aml-subtitleserver optee-userspace"
RDEPENDS:${PN} = "aml-mediahal-sdk  aml-subtitleserver aml-libdvr  libbinder liblog "

do_configure[noexec] = "1"
inherit autotools pkgconfig
S="${WORKDIR}/git"


EXTRA_OEMAKE="_DTVKIT_ROOT=${S}\
        DTVKIT_CC=${TARGET_PREFIX}gcc \
        DTVKIT_CXX=${TARGET_PREFIX}g++ \
        DTVKIT_AR=${TARGET_PREFIX}ar \
        BUILD_TYPE=${ARM_TARGET}\
        RDK_INCLUDES='${STAGING_DIR_TARGET}/usr/include ${STAGING_DIR_TARGET}/usr/json/include   ${STAGING_DIR_TARGET}/usr/binder/include   ${STAGING_DIR_TARGET}/usr/include/libdvr '   \
        RDK_LDPATH='${STAGING_DIR_TARGET}/usr/lib/'   \
        DTVKIT_ADDITIONAL_COMPILER_OPTIONS='-Wall -Wextra -march=armv7-a -mthumb -mfpu=neon -mfloat-abi=hard ${TOOLCHAIN_OPTIONS} ' \
        DTVKIT_OUTPUT_DIR=${B} \
        DTVKIT_INSTALL_DIR=${D}/usr \
        DTVKIT_RUNENV=linux \
        TARGET_CFLAGS+='-fstack-protector-strong -pie -fPIE -Wformat -Wformat-security -Werror=format-security -D_FORTIFY_SOURCE=2' \
        TARGET_LDFLAGS+='-fstack-protector-strong -Wl,-z,relro,-z,now' \
        "


do_compile () {
    cd ${S}
    oe_runmake clean
    oe_runmake all
}

do_install() {
#    install -d ${D}${bindir}
#    install -d ${D}${libdir}
#    install -d ${D}${includedir}
    cd ${B}
    oe_runmake -C ${S} install
}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
