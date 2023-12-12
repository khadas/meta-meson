SUMMARY = "Boa Web Server"
HOMEPAGE = "http://www.boa.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "http://www.boa.org/boa-0.94.14rc21.tar.gz \
           file://0001-use-name-max.patch \
        "
SRC_URI[sha256sum] = "02c51bf25f29d56e641b662f0767759654c28d88ec31f55c5a73d57edfe13cf6"

SRCREV ?="${AUTOREV}"

inherit autotools pkgconfig systemd

EXTRA_OEMAKE += "CFLAGS+=-DINET6"

do_install() {
    install -d ${D}/usr/sbin
    install -d ${D}/usr/lib/boa
    install -d ${D}/etc/boa

    install -D -m 0755 ${B}/src/boa ${D}/usr/sbin/boa
    install -D -m 0755 ${B}/src/boa_indexer ${D}/usr/lib/boa/boa_indexer
    install -D -m 0644 ${THISDIR}/files/boa.conf ${D}/etc/boa/boa.conf
    install -D -m 0644 ${THISDIR}/files/mime.types ${D}/etc/mime.types
    install -D -m 0644 ${THISDIR}/files/boa.service ${D}${systemd_unitdir}/system/boa.service
}

SYSTEMD_SERVICE:${PN} = "boa.service "

FILES:${PN} = " /usr/lib/* /usr/sbin/* /etc/*"
FILES:${PN}-dev = " /usr/include/*"
