
DESCRIPTION = "aml drmplayer library"
PN = 'drmplayer-bin'
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libmediadrm/drmplayer-bin/prebuilt')}"
#PV = "git${SRCPV}"

S = "${WORKDIR}/git"
SRCREV ?= "${AUTOREV}"

do_compile[noexec] = "1"



INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

DEPENDS += "aml-dvb aml-mediahal-sdk optee-userspace aml-secmem liblog aml-mp-sdk ffmpeg"

EXTRA_OEMAKE=" STAGING_DIR=${STAGING_DIR_TARGET} \
                 TARGET_DIR=${D} \
                 "
ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET:aarch64 = "aarch64.lp64."
TA_TARGET="noarch"

do_install() {
    install -d -m 0755 ${D}${libdir}
    install -D -m 0644 ${S}/${ARM_TARGET}/libdrmp.so ${D}${libdir}
    install -D -m 0644 ${S}/${ARM_TARGET}/libdec_ca.so ${D}${libdir}
    install -D -m 0644 ${S}/${ARM_TARGET}/libstbwrapper.so ${D}${libdir}

    install -d -m 0755 ${D}/usr/bin
    install -D -m 0755 ${S}/${ARM_TARGET}/drmptest ${D}/usr/bin

}

INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"

FILES:${PN} += " ${bindir}/* ${libdir}/*.so"
FILES:${PN}-dev = "${includedir}/* "

