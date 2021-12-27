SUMMARY = "Realtek bluetooth"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=726a766df559f36316aa5261724ee8cd"

#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/realtek/bluetooth.git;protocol=${AML_GIT_PROTOCOL};branch=master"

SRCREV = "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/rtk_hciattach"

do_compile(){
    ${MAKE} -C ${S} CC='${CC}'
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/rtk_hciattach ${D}${bindir}
}
