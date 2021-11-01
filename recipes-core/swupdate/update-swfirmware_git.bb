SUMMARY = "update sw firmware"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

SRC_URI += "file://update_swfirmware.sh"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"

do_install() {
    mkdir -p ${D}${bindir}
    install -m 0755 ${WORKDIR}/update_swfirmware.sh ${D}/${bindir}
}

FILES_${PN} += "${bindir}/*"
