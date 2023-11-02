inherit systemd

SUMMARY = "sandbox setup"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

SRC_URI += "file://sandbox-setup-before@.service \
            file://sandbox-setup-after@.service \
            "
SRC_URI += "file://sandbox-setup.env \
            file://sandbox-setup-after.basic.sh \
            file://sandbox-setup-after.audioserver.sh \
            file://sandbox-setup-before.wpeframework.sh \
            "


do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/sandbox-setup-before@.service ${D}/${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/sandbox-setup-after@.service ${D}/${systemd_unitdir}/system
    ln -sfr ${D}/${systemd_unitdir}/system/sandbox-setup-after@.service \
        ${D}/${systemd_unitdir}/system/sandbox-setup-after@basic.service
    ln -sfr ${D}/${systemd_unitdir}/system/sandbox-setup-after@.service \
        ${D}/${systemd_unitdir}/system/sandbox-setup-after@audioserver.service
    ln -sfr ${D}/${systemd_unitdir}/system/sandbox-setup-before@.service \
        ${D}/${systemd_unitdir}/system/sandbox-setup-before@wpeframework.service

    mkdir -p ${D}${bindir}
    install -m 0644 ${WORKDIR}/sandbox-setup.env ${D}/${bindir}
    install -m 0755 ${WORKDIR}/sandbox-setup-after.basic.sh ${D}/${bindir}
    install -m 0755 ${WORKDIR}/sandbox-setup-after.audioserver.sh ${D}/${bindir}
    install -m 0755 ${WORKDIR}/sandbox-setup-before.wpeframework.sh ${D}/${bindir}
}

FILES:${PN} += "${bindir}/*"
FILES:${PN} += "${systemd_unitdir}/system/*"

SYSTEMD_SERVICE:${PN} += "sandbox-setup-before@.service \
                          sandbox-setup-after@.service \
                          sandbox-setup-after@basic.service \
                          sandbox-setup-after@audioserver.service  \
                          sandbox-setup-before@wpeframework.service\
"
