inherit systemd
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://main.conf"
SRC_URI += "file://bluez.service"
SRC_URI += "file://bluez_tool.sh"
SRC_URI += "file://0001-bluez5-add-default_agent-3-5.patch"
#SRC_URI += "file://bluez_checkhci.sh"

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
}

FILES:${PN} += "${bindir}/*"
FILES:${PN} += "${systemd_unitdir}/system/*"
FILES:${PN}-obex += "${bindir}/obexctl"

SYSTEMD_SERVICE:${PN} += "bluez.service"

# triggered by udev rule
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

RDEPENDS:${PN}-obex:append:libc-glibc = " glibc-gconv-utf-16"
