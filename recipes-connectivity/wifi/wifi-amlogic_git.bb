SUMMARY = "Amlogic WIFI"
LICENSE = "CLOSED"

SRC_URI += "file://wifi.service"

SYSTEMD_AUTO_ENABLE = "enable"

inherit systemd

RDEPENDS:${PN} += "aml-utils-wifi-power "

SYSTEMD_SERVICE:${PN} = "wifi.service"
FILES:${PN} += "${systemd_unitdir}/system/wifi.service"

do_install() {
    install -d ${D}/${sysconfdir}/wifi

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/wifi.service ${D}${systemd_unitdir}/system

    if ${@bb.utils.contains("DISTRO_FEATURES", "aml-w1", "true", "false", d)}; then
        sed -i '/\/usr\/bin\/wifi_power/a ExecStart=\/sbin\/modprobe vlsicomm conf_path=\/etc\/wifi\/w1' ${D}${systemd_unitdir}/system/wifi.service
    fi

    if ${@bb.utils.contains("DISTRO_FEATURES", "softap aml-w1", "true", "false", d)}; then
        sed -i '/modprobe/d' ${D}${systemd_unitdir}/system/wifi.service
        sed -i '/\/usr\/bin\/wifi_power/a ExecStart=\/sbin\/modprobe vlsicomm country_code=WW conf_path=\/etc\/wifi\/w1 vmac1=wlan1 vif1opmode=2 con_mode=0x06 plt_ver=gva' ${D}${systemd_unitdir}/system/wifi.service
    fi

    if ${@bb.utils.contains("DISTRO_FEATURES", "aml-w2", "true", "false", d)}; then
        sed -i '/\/usr\/bin\/wifi_power/a ExecStart=\/sbin\/modprobe w2 ' ${D}${systemd_unitdir}/system/wifi.service
        sed -i '/\/usr\/bin\/wifi_power/a ExecStart=\/sbin\/modprobe w2_comm bus_type=sdio ' ${D}${systemd_unitdir}/system/wifi.service
        sed -i 's@#ExecStart=/usr/bin/wifi_power@ExecStart=/usr/bin/wifi_power@' ${D}${systemd_unitdir}/system/wifi.service
    fi

}
