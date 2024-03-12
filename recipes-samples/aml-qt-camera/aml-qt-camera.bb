SUMMARY = "amlogic qt camera demo"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS += "qtbase qtmultimedia"

SRCREV ?="${AUTOREV}"
PV = "${@bb.parse.vars_from_file(d.getVar('FILE'),d)[1] or '1.0'}"
PN = "${@bb.parse.vars_from_file(d.getVar('FILE'),d)[0] or 'aml-qt-camera'}"

inherit qmake5

do_install() {
    install -d ${D}${bindir}
    install -m 0755 -D ${WORKDIR}/aml-qt-camera-${PV}/qt-demo-camera ${D}${bindir}
}

RDEPENDS:${PN} += "qtmultimedia"
FILES:${PN} = " /usr/bin/*"

