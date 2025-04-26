FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = "file://NetworkManager.conf \
                  file://10-override-random-mac.conf \
                  file://zz-override-wifi-powersave-off.conf"

#PACKAGECONFIG += " \
#    'wifi \
#     nmcli \
#     dnsmasq \
#     modemmanager \
#     nss \
#    "

do_install:append() {
    install -d ${D}${sysconfdir}/NetworkManager/conf.d
    install -m 600 ${WORKDIR}/NetworkManager.conf ${D}${sysconfdir}/NetworkManager/conf.d/00-default.conf
	install -m 600 ${WORKDIR}/10-override-random-mac.conf ${D}${sysconfdir}/NetworkManager/conf.d/10-override-random-mac.conf
	install -m 600 ${WORKDIR}/zz-override-wifi-powersave-off.conf ${D}${sysconfdir}/NetworkManager/conf.d/zz-override-wifi-powersave-off.conf
}
