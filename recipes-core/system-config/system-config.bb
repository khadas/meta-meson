inherit systemd

SUMMARY = "system config"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

SRC_URI += "file://system-config.service"
SRC_URI += "file://system-config.sh"


do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/system-config.service ${D}/${systemd_unitdir}/system

    mkdir -p ${D}${bindir}
    install -m 0755 ${WORKDIR}/system-config.sh ${D}/${bindir}
}

do_install_append() {

}

FILES_${PN} += "${bindir}/*"
FILES_${PN} += "${systemd_unitdir}/system/*"

SYSTEMD_SERVICE_${PN} += "system-config.service"
