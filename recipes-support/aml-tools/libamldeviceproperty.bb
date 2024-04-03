SUMMARY = "libaml_deviceproperty.so for  Amlogic platform support."
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"


#SRCREV ?= "${AUTOREV}"
PV = "git${SRCPV}"
#For common patches
S = "${WORKDIR}/git/aml_deviceproperty"

RPROVIDES:${PN} += " libamldeviceproperty.so"

do_configure[noexec] = "1"
EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D}"

do_compile(){
    ${MAKE} ${EXTRA_OEMAKE} -C ${S}
}

do_install(){
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -m 0644 ${B}/libamldeviceproperty.so ${D}${libdir}
    cp -ra ${S}/aml_device_property.h ${D}${includedir}
}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN}-dev = "dev-so"
INSANE_SKIP:${PN} = "dev-so ldflags"
