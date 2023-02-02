SUMMARY = "aml dvb ota for dtvkit"

LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://${COREBASE}/../${AML_META_LAYER}/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

DEPENDS = " aml-dvb android-rpcservice liblog jsoncpp"
RDEPENDS:${PN} = " aml-dvb android-rpcservice liblog jsoncpp"

do_configure[noexec] = "1"
inherit autotools pkgconfig
S="${WORKDIR}/git"


EXTRA_OEMAKE="DTVKIT_ROOT=${MESON_ROOT_PATH}/aml-comp/thirdparty\
        STAGING_DIR=${STAGING_DIR_TARGET}/usr \
        INSTALL_DIR=${D}${libdir} \
        TARGET_DIR=${S} \
        "

do_compile () {
    cd ${S}
    oe_runmake -C glue_dtvkit clean
    oe_runmake -C glue_dtvkit all
}

do_install() {
    cd ${S}
    oe_runmake -C glue_dtvkit install
}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "

