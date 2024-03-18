DESCRIPTION = "aml secury memory allocator"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../${AML_META_LAYER}/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = " git://${AML_GIT_ROOT}/vendor/amlogic/prebuilt/libmediadrm;protocol=${AML_GIT_PROTOCOL};branch=linux-buildroot"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libmediadrm')}"

#SRCREV ?= "${AUTOREV}"

S = "${WORKDIR}/git"

ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET:aarch64 = "aarch64.lp64."
TA_TARGET="noarch"

do_install() {
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}/usr/include
    install -d -m 0755 ${D}/lib/optee_armtz

    install -D -m 0755 ${S}/cleartvp-bin/prebuilt/${TA_TARGET}/ta/${TDK_VERSION}/*.ta ${D}/lib/optee_armtz/
    install -D -m 0644 ${S}/cleartvp-bin/prebuilt/${TA_TARGET}/include/*.h ${D}/usr/include
    install -D -m 0644 ${S}/cleartvp-bin/prebuilt/${ARM_TARGET}/libclearTVP_ca.so ${D}${libdir}
}

FILES:${PN} = "${libdir}/* ${includedir}/* /lib/optee_armtz/*"
FILES:${PN}-dev = "${includedir}/* "
