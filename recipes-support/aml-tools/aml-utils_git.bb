SUMMARY = "amlogic utils"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=726a766df559f36316aa5261724ee8cd"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_commonlib;protocol=${AML_GIT_PROTOCOL};branch=master;"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/utils"

PACKAGES =+ "\
    ${PN}-wifi-power \
    ${PN}-simulate-key \
    ${PN}-usb-monitor \
"

FILES_${PN}-wifi-power = "${bindir}/wifi_power "

FILES_${PN}-simulate-key = "${bindir}/simulate_key "

FILES_${PN}-usb-monitor = "${bindir}/usb_monitor "

IR_REMOTE_DEVICE ?= "/dev/input/event0"
EXTRA_OEMAKE = "IR_REMOTE_DEVICE=${IR_REMOTE_DEVICE}"

UTILS_CMDS = "wifi_power simulate_key usb_monitor"

do_compile() {
    for CMD in ${UTILS_CMDS} ; do
        oe_runmake -C ${S} ${CMD}
    done
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/wifi_power ${D}${bindir}
    install -m 0755 ${S}/simulate_key ${D}${bindir}
    install -m 0755 ${S}/usb_monitor ${D}${bindir}
}
