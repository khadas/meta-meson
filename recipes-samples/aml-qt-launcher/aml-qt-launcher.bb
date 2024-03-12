SUMMARY = "amlogic qt launcher"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

SRCREV ?="${AUTOREV}"
PV = "${@bb.parse.vars_from_file(d.getVar('FILE'),d)[1] or '1.0'}"
PN = "${@bb.parse.vars_from_file(d.getVar('FILE'),d)[0] or 'aml-qt-launcher'}"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " file://aml-qt-launcher.service"

inherit systemd

do_install() {
    install -d ${D}${bindir}

    install -D -m 0644 ${THISDIR}/files/aml-qt-launcher.service ${D}${systemd_unitdir}/system/aml-qt-launcher.service
    install -D -m 0755 ${THISDIR}/files/qtlauncher.sh ${D}${bindir}
}

SYSTEMD_SERVICE:${PN} = "aml-qt-launcher.service"

FILES:{PN} = "/usr/bin "

