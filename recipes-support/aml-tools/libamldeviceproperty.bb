SUMMARY = "libaml_deviceproperty.so for  Amlogic platform support."
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"


SRCREV ?= "${AUTOREV}"
PV = "git${SRCPV}"
#For common patches
S = "${WORKDIR}/git/aml_deviceproperty"

RPROVIDES_${PN} += " libamldeviceproperty.so"

do_compile(){
    ${MAKE} -C ${S}
}

do_install(){
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -m 0644 ${S}/libamldeviceproperty.so ${D}${libdir}
    cp -ra ${S}/aml_device_property.h ${D}${includedir}
}

FILES_${PN} = "${libdir}/* ${bindir}/*"
FILES_${PN}-dev = "${includedir}/* "

INSANE_SKIP_${PN}-dev = "dev-so"