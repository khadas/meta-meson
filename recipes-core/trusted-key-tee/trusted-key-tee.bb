SUMMARY = "Startup script and systemd unit file for trusted key with tee backend"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI  = " file://trusted-key-tee.service"

S = "${WORKDIR}"

do_install() {
    # systemd service file
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/trusted-key-tee.service ${D}${systemd_unitdir}/system/
}

inherit allarch systemd

RDEPENDS_${PN} = "optee-userspace"
SYSTEMD_SERVICE_${PN} = "trusted-key-tee.service"
FILES_${PN} += "${systemd_unitdir}/system/trusted-key-tee.service"
