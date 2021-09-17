DESCRIPTION = "aml secury memory allocator"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = " git://${AML_GIT_ROOT}/vendor/amlogic/prebuilt/libmediadrm;protocol=${AML_GIT_PROTOCOL};branch=linux-buildroot"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libmediadrm')}"

SRCREV ?= "${AUTOREV}"

S = "${WORKDIR}/git"
DEPENDS = "aml-mediahal-sdk"
#RDEPENDS_${PN} = "aml-mediahal-sdk"

ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET_aarch64 = "aarch64.lp64."
TA_TARGET="noarch"

do_install() {
    install -d -m 0644 ${D}/usr/lib
    install -d -m 0644 ${D}/usr/include
    install -d -m 0644 ${D}/lib/teetz
    install -d -m 0644 ${D}/usr/bin

    install -D -m 0755 ${S}/libsecmem-bin/prebuilt/${TA_TARGET}/ta/${TDK_VERSION}/*.ta ${D}/lib/teetz/
    install -D -m 0644 ${S}/libsecmem-bin/prebuilt/${TA_TARGET}/include/*.h ${D}/usr/include
#    install -D -m 0755 ${S}/libsecmem-bin/prebuilt/${ARM_TARGET}/secmem_test ${D}/usr/bin
    install -D -m 0644 ${S}/libsecmem-bin/prebuilt/${ARM_TARGET}/libsecmem.so ${D}/usr/lib
}

FILES_${PN} = "${libdir}/* ${bindir}/* ${includedir}/* /lib/teetz/*"
FILES_${PN}-dev = "${includedir}/* "
