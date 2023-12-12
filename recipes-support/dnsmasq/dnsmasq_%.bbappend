inherit systemd

do_install:append(){

    if ${@bb.utils.contains("DISTRO_FEATURES", "softap", "true", "false", d)}; then
        sed -i 's/After=network.target/After=wifi-ap.service network.target/g' ${D}${systemd_unitdir}/system/dnsmasq.service
        sed -i '/ExecStart=/d' ${D}${systemd_unitdir}/system/dnsmasq.service
        sed -i '/test/a ExecStart=\/usr\/bin\/dnsmasq -x \/run\/dnsmasq.pid -7 \/etc\/dnsmasq.d -iwlan1  --dhcp-option=3,192.168.2.1 --dhcp-range=192.168.2.50,192.168.2.200,12h -p100' ${D}${systemd_unitdir}/system/dnsmasq.service
    fi

    # qca6174 use p2p0(not wlan1) as ap mode interface
    if ${@bb.utils.contains("MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS", "wififw-qca6174", "true", "false", d)}; then
        sed -i 's/wlan1/p2p0/g' ${D}${systemd_unitdir}/system/dnsmasq.service
    fi
}