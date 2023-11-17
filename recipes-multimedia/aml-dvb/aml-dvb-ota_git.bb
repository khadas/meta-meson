SUMMARY = "aml dvb ota samples"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/dvb_ota')}"
DEPENDS = " aml-dvb"
RDEPENDS:${PN} += "aml-dvb"

do_configure[noexec] = "1"
inherit autotools pkgconfig
S="${WORKDIR}/git"

CONFIG = "ap222.conf"
CONFIG_aq2432 = "aq2432.conf"
CONFIG_bf201 = "bf201.conf"

FLAGS = "-DBIG_BUFFER"
FLAGS_aq2432 = ""

EXTRA_OEMAKE="TARGET_DIR=${S} STAGING_DIR=${STAGING_DIR_TARGET}/usr INSTALL_DIR=${D}${bindir} FLAGS=${FLAGS}"

do_compile() {
    cd ${S}
    oe_runmake all
}

do_install() {
    cd ${S}
    oe_runmake install

    mkdir -p ${D}/etc
    install -D -m 0644 ${S}/config/${CONFIG} ${D}/etc/dvb_ota.conf
}

FILES:${PN} = "${bindir}/* ${sysconfdir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
