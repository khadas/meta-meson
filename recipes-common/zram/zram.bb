inherit systemd update-rc.d

SUMMARY = "ZRAM"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

INITSCRIPT_NAME = "zram"
INITSCRIPT_PARAMS = "start 01 2 3 4 5 . stop 80 0 6 1 ."

SRC_URI += "file://zram.service"
SRC_URI += "file://zram.sh"
SRC_URI += "file://zram.init"

ZRAM_FRACTION ?= "25"

do_install:append(){
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/zram.service ${D}/${systemd_unitdir}/system

    mkdir -p ${D}${bindir}
    install -m 0755 ${WORKDIR}/zram.sh ${D}/${bindir}
    sed -ri 's/(FRACTION=)[^a]*/\1${ZRAM_FRACTION}/' ${D}/${bindir}/zram.sh

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/zram.init ${D}${sysconfdir}/init.d/zram
}

FILES:${PN} += "${bindir}/*"
FILES:${PN} += "${systemd_unitdir}/system/*"

SYSTEMD_SERVICE:${PN} += "zram.service"
