inherit systemd
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

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
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "bt-qca", "file://0001-BT-add-qca-bt-wakeup-1-3.patch", "", d)}"
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "bt-qca", "file://0001-BT-qca-fix-adv-auto-wakeup.patch", "", d)}"
SRC_URI += "file://0001-BT-fix-repeatedly-connect-disconnect.patch"
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "aml-w1", "file://0001-BT-add-amlbt-w1-wakeup.patch", "", d)}"
SRC_URI += "file://0001-bluez5-fix-rcu-reconnect-1-1.patch"
#SRC_URI += "file://bluez_checkhci.sh"
SRC_URI += "file://0001-BT-fix-pair-inturrpt-error.patch"
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "aml-w1", "file://0002-BT-W1-fw-use-bin-format.patch", "", d)}"

TTY = "ttyS1"

do_install:append(){
    install -d ${D}${bindir}
    install -d ${D}/${systemd_unitdir}/system
    install -d ${D}/${sysconfdir}/bluetooth

    install -m 0755 ${B}/client/default_agent ${D}/${bindir}
    install -m 0755 ${WORKDIR}/bluez_tool.sh ${D}/${bindir}
    #install -m 0755 ${WORKDIR}/bluez_checkhci.sh ${D}/${bindir}
    install -m 0644 ${WORKDIR}/bluez.service ${D}/${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/main.conf ${D}/${sysconfdir}/bluetooth

    if ${@bb.utils.contains("DISTRO_FEATURES", "aml-w1", "true", "false", d)}; then
        sed -i '/Debug/a Device=aml' ${D}${sysconfdir}/bluetooth/main.conf
    elif ${@bb.utils.contains("DISTRO_FEATURES", "bt-qca", "true", "false", d)}; then
        sed -i '/Debug/a Device=qca' ${D}${sysconfdir}/bluetooth/main.conf
    else
        sed -i '/Debug/a Device=rtk' ${D}${sysconfdir}/bluetooth/main.conf
    fi

    sed -i "/Debug/a TTY=/dev/${TTY}" ${D}${sysconfdir}/bluetooth/main.conf
    sed -i '/^ExecStart=.*/d' ${D}/${systemd_unitdir}/system/bluetooth.service
}

FILES:${PN} += "${bindir}/*"
FILES:${PN} += "${systemd_unitdir}/system/*"
FILES:${PN}-obex += "${bindir}/obexctl"

SYSTEMD_SERVICE:${PN} += "bluez.service"

RDEPENDS:${PN}-obex:append:libc-glibc = " glibc-gconv-utf-16"
