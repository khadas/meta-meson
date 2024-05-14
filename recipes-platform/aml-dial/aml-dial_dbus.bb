SUMMARY = "Amlogic DIALServer Plugin"
LICENSE = "CLOSED"

SRC_URI += "file://amldial.service"
inherit autotools pkgconfig systemd
S = "${WORKDIR}/git"

EXTRA_OEMAKE = "'STAGING_DIR=${STAGING_DIR_TARGET}' \
                ${@bb.utils.contains('DISTRO_FEATURES', 'appmanager', 'WITH_APP_MANAGER=y', '', d)} \
                "
DEPENDS = "aml-platformserver \
           ${@bb.utils.contains('DISTRO_FEATURES', 'appmanager', 'aml-dbus aml-appmanager', 'wpeframework wpeframework-interfaces', d)} \
           "
do_compile(){
    cd ${S}
    oe_runmake BUILDDIR=${B} all
}
do_install() {
    install -d ${D}${sysconfdir}/amldial
    if [ "${@bb.utils.contains('DISTRO_FEATURES', 'appmanager', 'yes', 'no', d)}" = "yes"  ]; then
        install -m 0644 ${S}/amldial.conf ${D}${sysconfdir}/amldial
    else
        install -m 0644 ${S}/AMLDIAL.json ${D}${sysconfdir}/amldial
        install -D -m 0644 ${WORKDIR}/amldial.service ${D}${systemd_unitdir}/system/amldial.service
    fi
	install -d ${D}/usr/bin
	install -m 0755 ${B}/dialserver ${D}/usr/bin
}

FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'appmanager', '${sysconfdir}/amldial/amldial.conf', '${sysconfdir}/amldial/AMLDIAL.json', d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'appmanager', '', 'amldial.service', d)}"
