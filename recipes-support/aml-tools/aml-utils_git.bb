SUMMARY = "amlogic utils"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=726a766df559f36316aa5261724ee8cd"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_commonlib;protocol=${AML_GIT_PROTOCOL};branch=master;"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/utils"

DEPENDS += "libamldeviceproperty"
RDEPENDS:${PN}-fota-upgrade += "libamldeviceproperty"

PACKAGES = "\
    ${PN}-wifi-power \
    ${PN}-simulate-key \
    ${PN}-usb-monitor \
    ${PN}-fota-upgrade \
"

FILES:${PN}-wifi-power = "${bindir}/wifi_power "
FILES:${PN}-simulate-key = "${bindir}/simulate_key "
FILES:${PN}-usb-monitor = "${bindir}/usb_monitor "
FILES:${PN}-fota-upgrade = "${bindir}/fota_upgrade "

IR_REMOTE_DEVICE ?= "/dev/input/event0"
EXTRA_OEMAKE = "IR_REMOTE_DEVICE=${IR_REMOTE_DEVICE}"

UTILS_CMDS = "wifi_power simulate_key usb_monitor"
UTILS_CMDS += "${@bb.utils.contains("DISTRO_FEATURES", "fota-upgrade", " fota_upgrade", "", d)}"

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
    if ${@bb.utils.contains("DISTRO_FEATURES", "fota-upgrade", "true", "false", d)}; then
        install -m 0755 ${S}/fota_upgrade ${D}${bindir}
    fi
}
INSANE_SKIP:${PN} = "installed-vs-shipped"
