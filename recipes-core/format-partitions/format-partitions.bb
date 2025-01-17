SUMMARY = "Systemd service to format partitions"
SECTION = "core"
LICENSE = "CLOSED"

SRC_URI = " file://ext4format@.service"
SRC_URI:append = " file://format-to-ext4.sh"

inherit systemd

S = "${WORKDIR}"

SYSTEMD_AUTO_ENABLE ?= "${@bb.utils.contains('DISTRO_FEATURES','systemd','enable','',d)}"
SYSTEMD_PACKAGES += "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','ext4format@tee.service','',d)}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/format-to-ext4.sh ${D}${bindir}
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/ext4format@.service ${D}${systemd_unitdir}/system/
}

FILES:${PN} += " ${bindir}/* ${systemd_unitdir}/* "
