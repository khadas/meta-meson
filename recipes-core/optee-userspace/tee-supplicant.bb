SUMMARY = "Startup script and systemd unit file for tee"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

FILESEXTRAPATHS:prepend := "${THISDIR}/optee-userspace:"
SRC_URI  = " file://tee-supplicant.service"
SRC_URI  += " file://tee-supplicant.init"

S = "${WORKDIR}"

do_install() {
    # systemd service file
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/tee-supplicant.service ${D}${systemd_unitdir}/system/

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/tee-supplicant.init ${D}${sysconfdir}/init.d/tee-supplicant
}

inherit allarch systemd update-rc.d

INITSCRIPT_NAME = "tee-supplicant"
INITSCRIPT_PARAMS = "start 20 2 3 4 5 . stop 80 0 6 1 ."

RDEPENDS:${PN} = "optee-userspace"
SYSTEMD_SERVICE:${PN} = "tee-supplicant.service"
FILES:${PN} += "${systemd_unitdir}/system/tee-supplicant.service"
