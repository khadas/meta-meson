inherit systemd

SUMMARY = "sandbox setup"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

SRC_URI += "file://sandbox-setup.service"
SRC_URI += "file://sandbox-setup"


do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/sandbox-setup.service ${D}/${systemd_unitdir}/system

    mkdir -p ${D}${bindir}
    install -m 0755 ${WORKDIR}/sandbox-setup ${D}/${bindir}
}

FILES:${PN} += "${bindir}/*"
FILES:${PN} += "${systemd_unitdir}/system/*"

SYSTEMD_SERVICE:${PN} += "sandbox-setup.service"
