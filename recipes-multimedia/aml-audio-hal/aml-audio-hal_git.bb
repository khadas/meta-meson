SUMMARY = "aml audio utils"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/amlogic/audio;protocol=${AML_GIT_PROTOCOL};branch=linux-buildroot"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/aml_audio_hal')}"

DEPENDS += "aml-amaudioutils expat tinyalsa libamavutils liblog aml-avsync"
DEPENDS += "aml-dvbaudioutils libamadec aml-dvb"
RDEPENDS_${PN} += "liblog aml-amaudioutils aml-avsync aml-dvbaudioutils aml-dvb libamadec libamavutils"

inherit cmake pkgconfig

S="${WORKDIR}/git"
TARGET_CFLAGS += "-fPIC"

PACKAGECONFIG_sc2-5.4 += "dtv"
PACKAGECONFIG_s4 += "dtv"
PACKAGECONFIG[dtv] = "-DUSE_DTV=ON,-DUSE_DTV=OFF,"

PACKAGECONFIG += "msync"
PACKAGECONFIG[msync] = "-DUSE_MSYNC=ON,-DUSE_MSYNC=OFF,"

PACKAGECONFIG_sc2-5.4 += "sc2"
PACKAGECONFIG_s4 += "sc2"
PACKAGECONFIG[sc2] = "-DUSE_SC2=ON,-DUSE_SC2=OFF,"

FILES_${PN} = "${libdir}/* ${bindir}/* ${sysconfdir}/*"
FILES_${PN}-dev = "${includedir}/* "
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP_${PN}-dev = "dev-so ldflags dev-elf"
