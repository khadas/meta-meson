DESCRIPTION = "nagra-sdk"
SECTION = "nagra-sdk"
LICENSE = "CLOSE"
PV = "git${SRCPV}"
PR = "r0"

LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"
#Only enable it in OpenLinux
#SRC_URI_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'nagra', 'git://${AML_GIT_ROOT_OP}/nagra-sdk-nocs.git;protocol=${AML_GIT_ROOT_PROTOCOL};branch=projects/openlinux/v3.2-rdk','', d)}"
SRC_URI_append = " ${@get_patch_list_with_path('${COREBASE}/aml-patches/../vendor/nagra/nagra-sdk')}"

PN = 'nagra-sdk'
SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

do_install() {
    install -d -m 0755 ${D}/usr/lib
    install -D -m 0644 ${S}/lib/ca/libnagra_dal.a ${D}/usr/lib
    install -D -m 0644 ${S}/lib/ca/libnagra_dal.so ${D}/usr/lib

    install -d -m 0755 ${D}/lib/teetz
    install -D -m 0644 ${S}/lib/ta/bc2f95bc-14b6-4445-a43c-a1796e7cac31.ta ${D}/lib/teetz
    install -D -m 0644 ${S}/lib/ta/efdfed0c-a6bd-44d3-9c64-de426fc5fb89.ta ${D}/lib/teetz

}

FILES_${PN} = "${libdir}/* /usr/lib/* /lib/teetz/*"
FILES_${PN}-dev = "${includedir}/* "
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"