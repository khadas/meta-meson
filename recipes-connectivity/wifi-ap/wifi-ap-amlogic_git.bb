SUMMARY = "Amlogic WIFI AP"
LICENSE = "CLOSED"

SRC_URI += "file://wifi-ap.service \
            file://entropy.bin \
            file://wifi_ap_init"

SYSTEMD_AUTO_ENABLE = "enable"

inherit systemd

RDEPENDS:${PN} += "aml-utils-wifi-power "

SYSTEMD_SERVICE:${PN} = "wifi-ap.service"
FILES:${PN} += "${systemd_unitdir}/system/wifi-ap.service"

do_install() {
    install -d ${D}/${sysconfdir}/wifi

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/wifi-ap.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/entropy.bin ${D}${sysconfdir}
    install -m 0777 ${WORKDIR}/wifi_ap_init ${D}/${sysconfdir}/wifi

    if ${@bb.utils.contains("DISTRO_FEATURES", "softap", "true", "false", d)}; then
        sed -i '/Before=systemd-networkd.service/a After=wifi.service' ${D}${systemd_unitdir}/system/wifi-ap.service
    fi

    if ${@bb.utils.contains("MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS", "wififw-ap6398", "true", "false", d)}; then
        sed -i '/HT40+/d' ${D}${sysconfdir}/wifi/wifi_ap_init
        sed -i '/hw_mode=a/a echo "ht_capab=[HT20][SHORT-GI-20][DSSS_CCK-40]" >> \/etc\/hostapd_temp.conf' ${D}${sysconfdir}/wifi/wifi_ap_init
    fi

    # qca6174 use p2p0(not wlan1) as ap mode interface
    if ${@bb.utils.contains("MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS", "wififw-qca6174", "true", "false", d)}; then
        sed -i 's/wlan1/p2p0/g' ${D}${sysconfdir}/wifi/wifi_ap_init
    fi

    # RTL8723DU not support 5G
    if ${@bb.utils.contains("MACHINE_EXTRA_RRECOMMENDS", "rtk8723du", "true", "false", d)}; then
        sed -i 's/hw_mode=a/hw_mode=g/g' ${D}${sysconfdir}/wifi/wifi_ap_init
        sed -i 's/channel=36/channel=7/g' ${D}${sysconfdir}/wifi/wifi_ap_init
    fi

}
