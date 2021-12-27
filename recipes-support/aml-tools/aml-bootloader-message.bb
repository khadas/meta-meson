SUMMARY = "aml bootloader_message"
LICENSE = "CLOSED"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_commonlib;protocol=${AML_GIT_PROTOCOL};branch=master;"

DEPENDS += "zlib"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/bootloader_message"

do_compile(){
    ${MAKE} -C ${S} all
}

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -m 0644 ${S}/libbootloader_message.a ${D}${libdir}
    install -m 0755 ${S}/urlmisc ${D}${bindir}
}

FILES_${PN} = "${libdir}/* ${bindir}/*"
