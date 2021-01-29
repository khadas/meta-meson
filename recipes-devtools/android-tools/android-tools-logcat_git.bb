DESCRIPTION = "liblog for android tools"
PR = "r0"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://${THISDIR}/android-tools-logcat/LICENSE-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

#DEPENDS = "aml-amaudioutils"
DEPENDS = "liblog"

SRCREV = "${AUTOREV}"

PV = "${SRCPV}"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_commonlib;protocol=${AML_GIT_PROTOCOL};branch=master;"
#SRC_URI += "file://logcat.patch"
SRC_URI += "file://LICENSE-2.0"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/aml_commonlib')}"

S = "${WORKDIR}/git/"

do_compile(){
    ${MAKE} -C ${S}/logcat
}

do_install(){
    install -d ${D}${bindir}
    install -m 0755 ${S}/logcat/logcat ${D}${bindir}
    install -m 0755 ${S}/logcat/logcat_test ${D}${bindir}
}

