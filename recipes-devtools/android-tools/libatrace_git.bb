DESCRIPTION = "libatrace"
PR = "r0"
LICENSE = "Apache-2.0"

FILESEXTRAPATHS:prepend := "${THISDIR}/android-tools-libatrace:"

LIC_FILES_CHKSUM = "file://${THISDIR}/android-tools-libatrace/LICENSE-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "zlib"

#SRCREV = "${AUTOREV}"

PV = "${SRCPV}"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_commonlib;protocol=${AML_GIT_PROTOCOL};branch=master;"
SRC_URI += "file://LICENSE-2.0"
PROVIDES = "libatrace"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/aml_commonlib')}"

S = "${WORKDIR}/git/libatrace"

#do_package_qa[noexec] = "1"

do_compile(){
    ${MAKE} -C ${S}
}

do_install(){
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -m 0644 ${S}/libatrace.so ${D}${libdir}
    install -m 0755 ${S}/atrace ${D}${bindir}
    #install -m 0644 ${S}/lib/* ${D}${libdir}
    cp -ra ${S}/*.h ${D}${includedir}
}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "

INSANE_SKIP:${PN}-dev = "dev-so"
