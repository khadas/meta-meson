FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# Required for WifiControl: 'SetKey("autoscan", "periodic:120")'
SRC_URI_append_thunder = " file://0001-Set-CONFIG_AUTOSCAN_PERIODIC-y.patch"
SRC_URI_append = " file://0002-enlarge-cmd-reply-buf.patch"

SRC_URI += " \
            file://wpa_supplicant.service \
            file://createDefaultWPASupplciantConfigFile.sh \
            "
inherit systemd

do_install_append () {
    install -d ${D}${bindir}
    install -m 755 ${WORKDIR}/createDefaultWPASupplciantConfigFile.sh ${D}${bindir}
    if ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "true", "false", d)}
    then
        install -d ${D}${systemd_unitdir}/system
        install -D -m 0644 ${WORKDIR}/wpa_supplicant.service ${D}${systemd_unitdir}/system/wpa_supplicant.service
    fi

}

SYSTEMD_SERVICE_${PN} = "wpa_supplicant.service"

