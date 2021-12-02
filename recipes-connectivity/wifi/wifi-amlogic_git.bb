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

    #when connect wifi AP, wpa-supplicant need rename /etc/wpa_supplicant.conf, which can not on read-only /etc partition.
    #so when enable read-only, let wpa-supplicant to find wpa_supplicant under /data instead of /etc
    if [ "${READONLY}" = "y" ];then
        WPA_PATH="/data/persistent"
        sed -i "s#/etc/wpa_supplicant.conf#$WPA_PATH/wpa_supplicant.conf#g" ${D}${systemd_unitdir}/system/wifi.service
        sed -i "/^ExecStart=\/usr\/sbin\/wpa_supplicant/i \ExecStart=/bin/sh -c 'if [ ! -e $WPA_PATH/wpa_supplicant.conf ]; then mkdir -p $WPA_PATH;cp --preserve=context /etc/wpa_supplicant.conf $WPA_PATH/; fi'" ${D}${systemd_unitdir}/system/wifi.service
    fi
}
