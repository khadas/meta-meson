SUMMARY = "WIFI"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=726a766df559f36316aa5261724ee8cd"

SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_commonlib;protocol=${AML_GIT_PROTOCOL};branch=master;"
SRC_URI += "file://wifi.service"

SRCREV = "${AUTOREV}"
PV = "${SRCPV}"

SYSTEMD_AUTO_ENABLE = "enable"

inherit systemd

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} = "wifi.service"
FILES_${PN} += "${systemd_unitdir}/system/wifi.service"

do_compile(){
    ${MAKE} -C ${S}/utils wifi_power
}


do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/utils/wifi_power ${D}${bindir}

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/wifi.service ${D}${systemd_unitdir}/system

}

