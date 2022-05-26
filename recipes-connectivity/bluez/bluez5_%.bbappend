inherit systemd
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-bluez5_utils-add-qca9377-bt-support-1-3.patch"
SRC_URI += "file://0001-BT-add-qca6174-bt-support-2-3.patch"
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "aml-w1", \
            "file://0001-BT-add-amlbt-w1-5-5.patch \
            file://0001-BT-when-iperf-BT-play-caton-1-2.patch", "", d)}"
SRC_URI += "file://main.conf"
SRC_URI += "file://bluez.service"
SRC_URI += "file://bluez_tool.sh"
SRC_URI += "file://0001-RDK-fix-issue-in-bluez5.55-1-1.patch"
SRC_URI += "file://0001-bluez5-add-default_agent-3-5.patch"

do_install_append(){
    install -d ${D}${bindir}
    install -d ${D}/${systemd_unitdir}/system
    install -d ${D}/${sysconfdir}/bluetooth

    install -m 0755 ${B}/client/default_agent ${D}/${bindir}
    install -m 0755 ${WORKDIR}/bluez_tool.sh ${D}/${bindir}
    install -m 0644 ${WORKDIR}/bluez.service ${D}/${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/main.conf ${D}/${sysconfdir}/bluetooth

    if ${@bb.utils.contains("DISTRO_FEATURES", "aml-w1", "true", "false", d)}; then
        sed -i '/Debug=0/a Device=aml' ${D}${sysconfdir}/bluetooth/main.conf
    else
        echo "MACHINE_ARCH is ${MACHINE_ARCH}"
        case ${MACHINE_ARCH} in
        mesonsc2* | mesons4*)
            sed -i '/Debug=0/a Device=qca' ${D}${sysconfdir}/bluetooth/main.conf
        ;;
        mesont5d* | mesont5w* | mesont3*)
            sed -i '/Debug=0/a Device=rtk' ${D}${sysconfdir}/bluetooth/main.conf
        ;;
        esac
    fi

    case ${MACHINE_ARCH} in
    mesona213y*)
        sed -i '/Debug=0/a TTY=/dev/ttyS2' ${D}${sysconfdir}/bluetooth/main.conf
    ;;
    *)
        sed -i '/Debug=0/a TTY=/dev/ttyS1' ${D}${sysconfdir}/bluetooth/main.conf
    ;;
    esac
}

FILES_${PN} += "${bindir}/*"
FILES_${PN} += "${systemd_unitdir}/system/*"

SYSTEMD_SERVICE_${PN} += "bluez.service"
