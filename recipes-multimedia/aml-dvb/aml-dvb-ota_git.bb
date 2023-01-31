SUMMARY = "aml dvb ota samples"
LICENSE = "LGPL-2.0+"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/dvb_ota')}"
DEPENDS = " aml-dvb"
RDEPENDS_${PN} += "aml-dvb"

do_configure[noexec] = "1"
inherit autotools pkgconfig
S="${WORKDIR}/git"

EXTRA_OEMAKE="TARGET_DIR=${S} STAGING_DIR=${STAGING_DIR_TARGET}/usr INSTALL_DIR=${D}${bindir}"

do_compile() {
    cd ${S}
    oe_runmake all
}

do_install() {
    cd ${S}
    oe_runmake install
}

FILES_${PN} = "${bindir}/*"
FILES_${PN}-dev = "${includedir}/* "
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP_${PN}-dev = "dev-so ldflags dev-elf"
