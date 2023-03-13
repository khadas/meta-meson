SUMMARY = "aml ubootenv"
LICENSE = "CLOSED"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_commonlib;protocol=${AML_GIT_PROTOCOL};branch=master;"

RDEPENDS_${PN} += "aml-ubootenv"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/ubootenv"
B = "${WORKDIR}/build"

do_compile(){
    ${MAKE} -C ${S} all OUT_DIR=${B}
}

do_install() {
    install -d ${D}${includedir}
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -m 0644 ${S}/ubootenv.h ${D}${includedir}
    install -m 0644 ${B}/libubootenv.so ${D}${libdir}
    install -m 0755 ${B}/uenv ${D}${bindir}
}

FILES_${PN} = "${libdir}/* ${bindir}/*"
FILES_${PN}-dev = "${includedir}/* "
