SUMMARY = "aml pqserver"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_pqserver.git;protocol=${AML_GIT_PROTOCOL};branch=master"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"
SRC_URI +="file://pqserver.service"
SRC_URI +="file://pqserver_precheck.sh"

DEPENDS = " libbinder liblog sqlite3 aml-audio-service "
RDEPENDS_${PN} = " liblog libbinder aml-audio-service"
do_configure[noexec] = "1"
inherit autotools pkgconfig systemd
S="${WORKDIR}/git"

IPC_TYPE = "TV_BINDER"

EXTRA_OEMAKE="STAGING_DIR=${STAGING_DIR_TARGET} \
              TV_IPC_TYPE=${IPC_TYPE} \
              TARGET_DIR=${D} \
             "
do_compile() {
    cd ${S}
    oe_runmake  all
}
do_install() {
   install -d ${D}${libdir}
   install -d ${D}${bindir}
   install -d ${D}${includedir}

    cd ${S}
#    oe_runmake  install
    install -m 0644 ${S}/libpqclient.so ${D}${libdir}
    install -m 0644 ${S}/libpq.so ${D}${libdir}
    install -m 755 ${S}/pqservice ${D}/usr/bin/
    install -m 755 ${S}/pqtest ${D}/usr/bin/
    install -m 0644 ${S}/client/include/*.h ${D}${includedir}
    if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
        install -D -m 0644 ${WORKDIR}/pqserver.service ${D}${systemd_unitdir}/system/pqserver.service
        install -m 755 ${WORKDIR}/pqserver_precheck.sh ${D}/usr/bin/
    fi
}
SYSTEMD_SERVICE_${PN} = "pqserver.service "

FILES_${PN} = "${libdir}/* ${bindir}/*"
FILES_${PN}-dev = "${includedir}/* "
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP_${PN}-dev = "dev-so ldflags dev-elf"
