include wpa-supplicant.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
            file://openssl_no_md4.patch \
            file://0001-Set-CONFIG_AUTOSCAN_PERIODIC-y.patch \
            file://0002-enlarge-cmd-reply-buf.patch \
            "

do_configure:append () {
   echo "OPENSSL_NO_MD4=y" >> wpa_supplicant/.config
}

do_install:append () {
   install -d ${D}${includedir}
   install -m 0644 ${S}/src/common/wpa_ctrl.h ${D}${includedir}
}
