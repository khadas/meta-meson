SUMMARY = "aml ubootenv"
LICENSE = "CLOSED"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_commonlib;protocol=${AML_GIT_PROTOCOL};branch=master;"

DEPENDS += "zlib"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git"

do_compile(){
    ${MAKE} -C ${S}/ubootenv all
}

do_install() {
    install -d ${D}${includedir}
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -m 0644 ${S}/ubootenv/ubootenv.h ${D}${includedir}
    install -m 0644 ${S}/ubootenv/libubootenv.a ${D}${libdir}
    install -m 0755 ${S}/ubootenv/uenv ${D}${bindir}
}

FILES_${PN} = "${libdir}/* ${bindir}/*"
FILES_${PN}-dev = "${includedir}/* "
