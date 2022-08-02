DESCRIPTION = "Android Verified Boot 2.0 with Linux DM-Verity support"
LICENSE = "APACHE"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/APACHE;md5=b8228f2369d92593f53f0a0685ebd3c0"

SRC_URI = "file://avbtool-dm-verity.py"
S = "${WORKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/avbtool-dm-verity.py ${D}${bindir}
}

BBCLASSEXTEND = "native"
