DESCRIPTION = "irdeto-imw"
SECTION = "irdeto-imw"
LICENSE = "CLOSE"
PV = "git${SRCPV}"
PR = "r0"

#Only enable it in OpenLinux
#IRDETO_BRANCH = "TBD"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/irdeto/irdeto-app/irdeto_imw')}"

inherit cmake pkgconfig systemd update-rc.d

INITSCRIPT_NAME = "irdeto-imw"
INITSCRIPT_PARAMS = "start 80 2 3 4 5 . stop 80 0 6 1 ."

SYSTEMD_AUTO_ENABLE:${PN} = "enable"

#PN = 'irdeto-imw'

#SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "liblog aml-libdvr aml-mediahal-sdk irdeto-sdk directfb meson-display libdrm"
RDEPENDS:${PN} += "liblog aml-libdvr aml-mediahal-sdk irdeto-sdk directfb meson-display libdrm"

EXTRA_OEMAKE = "STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D} \
          idw_cas=cca \
        "

do_configure () {
}

do_compile () {
    cd ${S}
    oe_runmake -j1 ${EXTRA_OEMAKE} all
}

do_install() {
    install -d ${D}${bindir}
    cp -rf ${S}/target/*.target.mw/idway/ ${D}/usr/bin/
    chmod a+x ${D}/usr/bin/idway/startIDwayJ.sh

    install -d -m 0644 ${D}/etc/cas/irdeto
    if [ -e ${S}/vendor/aml_hal/prebuilts/conf/irdeto_hal.conf ] ; then
        install -D -m 0644 ${S}/vendor/aml_hal/prebuilts/conf/irdeto_hal.conf ${D}/etc/cas/irdeto/
    fi

    if [ -e ${S}/vendor/prebuilts/conf/irdeto_hal.conf ] ; then
         install -D -m 0644 ${S}/vendor/prebuilts/conf/irdeto_hal.conf ${D}/etc/cas/irdeto/
    fi

    install -d ${D}/${systemd_unitdir}/system
    if [ -e ${S}/files/irdeto-imw.service ] ; then
        install -D -m 0644 ${S}/files/irdeto-imw.service ${D}${systemd_unitdir}/system/irdeto-imw.service
    fi

    install -d ${D}${sysconfdir}/init.d
    if [ -e ${S}/files/irdeto-imw.init ] ; then
        install -m 0755 ${S}/files/irdeto-imw.init ${D}${sysconfdir}/init.d/irdeto-imw
    fi
}

SYSTEMD_SERVICE:${PN} = "irdeto-imw.service"

FILES:${PN} = "${bindir}/* /etc/cas/irdeto/* ${sysconfdir} ${systemd_unitdir}/system/"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
