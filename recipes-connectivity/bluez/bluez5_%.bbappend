inherit systemd
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-bluez5_utils-add-qca9377-bt-support-1-3.patch"
SRC_URI += "file://0001-BT-add-qca6174-bt-support-2-3.patch"
SRC_URI += "file://main.conf"
SRC_URI += "file://bluez.service"
SRC_URI += "file://bluez_tool.sh"

do_install_append(){
    install -d ${D}${bindir}
    install -d ${D}/${systemd_unitdir}/system
    install -d ${D}/${sysconfdir}/bluetooth

    install -m 0755 ${WORKDIR}/bluez_tool.sh ${D}/${bindir}
    install -m 0644 ${WORKDIR}/bluez.service ${D}/${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/main.conf ${D}/${sysconfdir}/bluetooth

    echo "MACHINE_ARCH is ${MACHINE_ARCH}"
    case ${MACHINE_ARCH} in
    mesons4*)
        sed -i '/Debug=0/a Device=qca' ${D}${sysconfdir}/bluetooth/main.conf
    ;;
    esac
}

FILES_${PN} += "${bindir}/*"
FILES_${PN} += "${systemd_unitdir}/system/*"

SYSTEMD_SERVICE_${PN} += "bluez.service"
