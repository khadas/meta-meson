DESCRIPTION = "Android Verified Boot 2.0 Test Keys"
LICENSE = "APACHE"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/APACHE;md5=b8228f2369d92593f53f0a0685ebd3c0"

SRC_URI += "file://testkey_rsa2048.pem"
SRC_URI += "file://testkey_rsa2048.avbpubkey"
SRC_URI += "file://testkey_rsa4096.avbpubkey"
SRC_URI += "file://testkey_rsa4096.pem"
SRC_URI += "file://testkey_rsa2048_2.avbpubkey"
SRC_URI += "file://testkey_rsa2048_2.pem"

inherit native
S = "${WORKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0755 ${S}/testkey_rsa2048.pem ${D}${sysconfdir}/vbmeta_rsa2048.pem
    install -m 0755 ${S}/testkey_rsa2048_2.pem ${D}${sysconfdir}/system_rsa2048.pem
    install -m 0755 ${S}/testkey_rsa2048_2.pem ${D}${sysconfdir}/vendor_rsa2048.pem
    install -m 0755 ${S}/testkey_rsa2048_2.pem ${D}${sysconfdir}/recovery_rsa2048.pem
    install -m 0755 ${S}/testkey_rsa2048.avbpubkey ${D}${sysconfdir}/vbmeta_rsa2048.avbpubkey
    install -m 0755 ${S}/testkey_rsa2048_2.avbpubkey ${D}${sysconfdir}/system_rsa2048.avbpubkey
    install -m 0755 ${S}/testkey_rsa2048_2.avbpubkey ${D}${sysconfdir}/vendor_rsa2048.avbpubkey
    install -m 0755 ${S}/testkey_rsa2048_2.avbpubkey ${D}${sysconfdir}/recovery_rsa2048.avbpubkey
}
