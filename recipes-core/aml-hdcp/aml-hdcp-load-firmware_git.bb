SUMMARY = "aml hdcp firmware loading service"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRC_URI_append = "file://load_hdcp2.2_firmware_tx22.service "
SRC_URI_append = "file://load_hdcp2.2_firmware_rx22.service "

do_configure[noexec] = "1"
do_compile[noexec] = "1"

S = "${WORKDIR}"

SYSTEMD_AUTO_ENABLE = "enable"

inherit systemd

do_install() {
    install -d -m 0755 ${D}/lib/firmware/hdcp/
    install -d ${D}/${systemd_unitdir}/system
    touch ${D}/lib/firmware/hdcp/firmware.le
    if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
        if [ "${@bb.utils.contains("DISTRO_FEATURES", "amlogic-tv", "yes", "no", d)}" = "yes"  ]; then
            install -D -m 0644 ${S}/load_hdcp2.2_firmware_rx22.service ${D}${systemd_unitdir}/system/load_hdcp2.2_firmware.service
        else
            install -D -m 0644 ${S}/load_hdcp2.2_firmware_tx22.service ${D}${systemd_unitdir}/system/load_hdcp2.2_firmware.service
        fi
    fi
}

SYSTEMD_SERVICE_${PN} = "load_hdcp2.2_firmware.service "
FILES_${PN} += "/lib/firmware/hdcp/* "
INSANE_SKIP_${PN} = "ldflags dev-so dev-elf"
INSANE_SKIP_${PN}-dev = "ldflags dev-so dev-elf"
