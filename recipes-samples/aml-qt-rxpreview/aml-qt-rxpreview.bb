SUMMARY = "amlogic qt rxpreview demo"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS += "qtbase qtmultimedia"
DEPENDS += "aml-tvserver "
LDFLAGS += " -ltvclient"

SRC_URI += "file://aml-qt-rxpreview.service \
            file://aml-qt-rxpreview.sh "

SYSTEMD_AUTO_ENABLE = "enable"

inherit systemd

SRCREV ?="${AUTOREV}"
PV = "${@bb.parse.vars_from_file(d.getVar('FILE'),d)[1] or '1.0'}"
PN = "${@bb.parse.vars_from_file(d.getVar('FILE'),d)[0] or 'aml-qt-rxpreview'}"

inherit qmake5

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${systemd_unitdir}/system/
    install -m 0755 -D ${WORKDIR}/${PN}-${PV}/aml-qt-rxpreview ${D}${bindir}
    install -m 0755 -D ${WORKDIR}/aml-qt-rxpreview.service ${D}${systemd_unitdir}/system/
    install -m 0755 -D ${WORKDIR}/aml-qt-rxpreview.sh ${D}${bindir}
}
SYSTEMD_SERVICE_${PN} = "aml-qt-rxpreview.service"
FILES_${PN} = " /usr/bin/*"
FILES_${PN} += " /lib/systemd/system/*"

RDEPENDS_${PN} = "qtmultimedia aml-tvserver "

INSANE_SKIP_${PN} = "ldflags dev-so dev-elf"
INSANE_SKIP_${PN}-dev = "ldflags dev-so dev-elf"

