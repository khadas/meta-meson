DESCRIPTION = "aml mediahal sdk"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/mediahal_sdk;protocol=${AML_GIT_PROTOCOL};branch=linux-master"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/mediahal-sdk')}"

DEPENDS += "aml-audio-service libdrm-meson"
RDEPENDS:${PN} += "aml-audio-service libdrm-meson aml-amaudioutils"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '' ,d)}"
RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '' ,d)}"

#do_compile[noexec] = "1"

#SRCREV ?= "${AUTOREV}"

PV = "git${SRCPV}"

S = "${WORKDIR}/git"

ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET:aarch64 = "aarch64.lp64."
TA_TARGET="noarch"

EXTRA_OEMAKE="STAGING_DIR=${STAGING_DIR_TARGET} \
                 TARGET_DIR=${D} \
                 EXTRA_CFLAGS=-I${S}/prebuilt/${TA_TARGET}/include/ \
                 EXTRA_LDFLAGS=-L${S}/prebuilt/${ARM_TARGET}/ \
                               "
do_compile(){
    cd ${S}/example/AmTsPlayerExample
    oe_runmake
    cd ${S}/example/AmTsPlayerMultiExample
    oe_runmake
    cd ${S}/example/EsVideoDecPlayer
    oe_runmake
}

do_install() {
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}/usr/include

    install -D -m 0644 ${S}/prebuilt/${TA_TARGET}/include/*.h ${D}/usr/include
    install -D -m 0644 ${S}/prebuilt/${ARM_TARGET}/libmediahal_resman.so ${D}${libdir}
    install -D -m 0644 ${S}/prebuilt/${ARM_TARGET}/libmediahal_tsplayer.so ${D}${libdir}
    install -D -m 0644 ${S}/prebuilt/${ARM_TARGET}/libmediahal_videodec.so ${D}${libdir}
    install -D -m 0644 ${S}/prebuilt/${ARM_TARGET}/libmediahal_mediasync.so ${D}${libdir}
    install -D -m 0644 ${S}/prebuilt/${ARM_TARGET}/libmediahal_dmabufmanage.so ${D}${libdir}
    install -d -m 0755 ${D}/usr/bin
    install -D -m 0755 ${S}/example/AmTsPlayerExample/AmTsPlayerExample ${D}/usr/bin
    install -D -m 0755 ${S}/example/EsVideoDecPlayer/EsVideoDecPlayer ${D}/usr/bin
if ${@bb.utils.contains('DISTRO_FEATURES','zapper-2k','false','true', d)}; then
    install -D -m 0644 ${S}/prebuilt/${ARM_TARGET}/libmediahal_videorender.so ${D}${libdir}
    install -D -m 0755 ${S}/example/AmTsPlayerMultiExample/AmTsPlayerMultiExample ${D}/usr/bin
fi
}


FILES:${PN} = "${libdir}/* ${includedir}/* /usr/bin/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf already-stripped"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
