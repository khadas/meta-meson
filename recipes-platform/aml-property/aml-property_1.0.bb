SUMMARY = "Amlogic Property Server"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRCREV ?= "${AUTOREV}"

S = "${WORKDIR}/git"
inherit pkgconfig systemd
do_configure[noexec] = "1"

TOOLCHAIN = "gcc"
BUILDDIR = "${WORKDIR}/build"
EXTRA_OEMAKE="BUILDDIR=${BUILDDIR} \
              DESTDIR=${D} \
             "

DEPENDS += "leveldb"
DEPENDS += "aml-dbus"

RDEPENDS:${PN} += "leveldb"
RDEPENDS:${PN} += "aml-dbus"

do_compile(){
    cd ${S}
    oe_runmake all
}

do_install() {
    cd ${S}
    oe_runmake install
}

SYSTEMD_SERVICE:${PN} = "propertyserver.service"

# ----------------------------------------------------------------------------

FILES:${PN} += "${libdir}/* ${bindir}/* /etc/dbus-1/system.d"
FILES:${PN}-dev += "${includedir}/* ${libdir}/pkgconfig/*"

PROVIDES = " libaml-property.so.0"
RPROVIDES:${PN} += " libaml-property.so.0()(64bit)"
