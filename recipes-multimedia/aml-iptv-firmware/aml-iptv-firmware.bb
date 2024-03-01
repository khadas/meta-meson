DESCRIPTION = "aml iptv firmware library"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/aml-iptv-firmware/')}"

#DEPENDS += "aml-dvb aml-mediahal-sdk optee-userspace aml-secmem liblog aml-mp-sdk ffmpeg-vendor"
#DEPENDS += "aml-dvb aml-mediahal-sdk optee-userspace aml-secmem liblog aml-mp-sdk ffmpeg-vendor"
#RDEPENDS:${PN} += "aml-audio-service libdrm-meson"

SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET_aarch64 = "aarch64.lp64."
TA_TARGET="noarch"


do_compile[noexec] = "1"

do_install() {
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}/usr/include

    install -D -m 0644 ${S}/prebuilt/${TA_TARGET}/include/*.h ${D}/usr/include
    install -D -m 0644 ${S}/prebuilt/${ARM_TARGET}/libAmIptvMedia.so ${D}${libdir}
}


FILES:${PN} += "${libdir}/*.so ${includedir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
