DESCRIPTION = "Offline encryption tool for partition encryption"
HOMEPAGE = ""
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

inherit native

#SRCREV ?= "${AUTOREV}"
do_configure[noexec] = "1"
DEPENDS = "openssl-native"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/enc_part ${D}${bindir}
}

do_compile:prepend() {
    cd ${S}
}
