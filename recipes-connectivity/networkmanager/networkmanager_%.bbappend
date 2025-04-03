FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " file://NetworkManager.conf"

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
}
