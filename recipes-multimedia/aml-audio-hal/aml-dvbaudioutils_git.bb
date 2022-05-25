SUMMARY = "aml dvb audio utils"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/amlogic/audio;protocol=${AML_GIT_PROTOCOL};branch=linux-buildroot"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/aml_audio_hal')}"
EXTRA_OEMAKE = "TARGET_DIR=${D} STAGING_DIR=${D}"

DEPENDS += "liblog aml-amaudioutils alsa-lib"
RDEPENDS_${PN} += "liblog"
do_compile () {
    cd ${S}/dtv_audio_utils
    oe_runmake -j1 ${EXTRA_OEMAKE} all
    cd ${S}/amadec
    oe_runmake -j1 ${EXTRA_OEMAKE} all
}

do_install () {
    install -d ${D}${libdir}
    install -d ${D}/usr/include/
    install -m 0644 -D ${S}/dtv_audio_utils/libdvbaudioutils.so ${D}${libdir}
    install -m 0644 ${S}/dtv_audio_utils/sync/*.h ${D}/usr/include
    install -m 0644 ${S}/dtv_audio_utils/audio_read_api/*.h ${D}/usr/include
    install -m 0644 ${S}/dtv_audio_utils/audio_read_api/audio_es.h ${D}/usr/include
    install -m 0644 -D ${S}/amadec/libaudamadec.so ${D}${libdir}
}

S="${WORKDIR}/git"
TARGET_CFLAGS += "-fPIC"

FILES_${PN} = "${libdir}/* ${bindir}/* ${sysconfdir}/*"
FILES_${PN}-dev = "${includedir}/* "
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP_${PN}-dev = "dev-so ldflags dev-elf"
