SUMMARY = "Amlogic WIFI"
LICENSE = "CLOSED"

SRC_URI += "file://wifi.service"

SYSTEMD_AUTO_ENABLE = "enable"

inherit systemd

RDEPENDS_${PN} += "aml-utils-wifi-power "

SYSTEMD_SERVICE_${PN} = "wifi.service"
FILES_${PN} += "${systemd_unitdir}/system/wifi.service"

do_install() {
    install -d ${D}/${sysconfdir}/wifi

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/wifi.service ${D}${systemd_unitdir}/system

    if ${@bb.utils.contains("DISTRO_FEATURES", "aml-w1", "true", "false", d)}; then
        sed -i '/\/usr\/bin\/wifi_power/a ExecStart=\/sbin\/modprobe vlsicomm conf_path=\/etc\/wifi\/w1' ${D}${systemd_unitdir}/system/wifi.service
    fi
}
