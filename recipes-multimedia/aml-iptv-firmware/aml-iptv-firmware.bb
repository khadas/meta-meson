DESCRIPTION = "aml iptv firmware library"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/aml-iptv-firmware/')}"

#DEPENDS += "aml-dvb aml-mediahal-sdk optee-userspace aml-secmem liblog aml-mp-sdk ffmpeg-vendor"
#DEPENDS += "aml-dvb aml-mediahal-sdk optee-userspace aml-secmem liblog aml-mp-sdk ffmpeg-vendor"
#RDEPENDS_${PN} += "aml-audio-service libdrm-meson"

SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET_aarch64 = "aarch64.lp64."
TA_TARGET="noarch"


do_compile[noexec] = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

do_install() {
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}/usr/include

    install -D -m 0644 ${S}/prebuilt/${TA_TARGET}/include/*.h ${D}/usr/include
    install -D -m 0644 ${S}/prebuilt/${ARM_TARGET}/libAmIptvMedia.so ${D}${libdir}
}


FILES_${PN} += "${libdir}/*.so ${includedir}/*"
FILES_${PN}-dev = "${includedir}/* "
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP_${PN}-dev = "dev-so ldflags dev-elf"
