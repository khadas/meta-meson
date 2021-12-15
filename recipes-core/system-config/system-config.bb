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
    #init the unifykeys
    case ${MACHINE_ARCH} in
    mesont5w*)
        sed -i '$a\\necho 1 > /sys/class/unifykeys/attach' ${D}/${bindir}/system-config.sh
    ;;
    esac
}

FILES_${PN} += "${bindir}/*"
FILES_${PN} += "${systemd_unitdir}/system/*"

SYSTEMD_SERVICE_${PN} += "system-config.service"
