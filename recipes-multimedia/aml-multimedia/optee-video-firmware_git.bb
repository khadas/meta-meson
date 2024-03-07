SUMMARY = "amlogic optee video firmware"

inherit systemd update-rc.d

INITSCRIPT_NAME = "videoFirmwarePreload"
INITSCRIPT_PARAMS = "start 30 2 3 4 5 . stop 80 0 6 1 ."

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = "bzip2 libxml2"
RDEPENDS:${PN} = "libbz2 optee-userspace"
include aml-multimedia.inc

SRC_URI += "file://videoFirmwarePreload.service"
SRC_URI += "file://videoFirmwarePreload.init"
#PR = "${INC_PR}.${TDK_VERSION}"

S = "${WORKDIR}/git/secfirmload/secloadbin"
TA_ARCH = "noarch"
TAR_ARCH = "arm.aapcs-linux.hard"
TAR_ARCH:aarch64 = "aarch64.lp64."

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
	mkdir -p ${D}/usr/bin
	mkdir -p ${D}${libdir}
	mkdir -p ${D}/lib/optee_armtz

    install -m 0644 ${S}/${TA_ARCH}/ta/${TDK_VERSION}/*.ta ${D}/lib/optee_armtz
    install -m 0644 ${S}/${PLATFORM_TDK_VERSION}/${TAR_ARCH}/libtee_preload_fw.so  ${D}${libdir}
    install -m 0755 ${S}/${PLATFORM_TDK_VERSION}/${TAR_ARCH}/tee_preload_fw  ${D}/usr/bin

    # systemd service file
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/videoFirmwarePreload.service ${D}${systemd_unitdir}/system/

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/videoFirmwarePreload.init ${D}${sysconfdir}/init.d/videoFirmwarePreload
}

FILES:${PN} = "/lib/optee_armtz/* ${libdir}/* /usr/bin/* ${sysconfdir}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} = "ldflags dev-elf already-stripped"

FILES:${PN} += "${systemd_unitdir}/system/videoFirmwarePreload.service"
SYSTEMD_SERVICE:${PN} = "videoFirmwarePreload.service"
