SUMMARY = "aml audio utils"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/amlogic/audio;protocol=${AML_GIT_PROTOCOL};branch=linux-buildroot"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/aml_audio_hal')}"

DEPENDS += "liblog aml-amaudioutils expat aml-avsync aml-dvbaudioutils aml-dvb"
RDEPENDS:${PN} += "liblog aml-amaudioutils aml-avsync aml-dvbaudioutils aml-dvb libfaad-aml libmad-aml libflac-aml libadpcm-aml"

inherit cmake pkgconfig

S="${WORKDIR}/git"
TARGET_CFLAGS += "-fPIC"

PACKAGECONFIG:append:sc2 += "dtv"
PACKAGECONFIG:append:s4 += "dtv"
PACKAGECONFIG:append:t5d += "dtv"
PACKAGECONFIG:append:t5w += "dtv"
PACKAGECONFIG:append:t3 += "dtv"
PACKAGECONFIG[dtv] = "-DUSE_DTV=ON,-DUSE_DTV=OFF,"

PACKAGECONFIG += "msync"
PACKAGECONFIG[msync] = "-DUSE_MSYNC=ON,-DUSE_MSYNC=OFF,"

PACKAGECONFIG:append:sc2 += "sc2"
PACKAGECONFIG:append:s4 += "sc2"
PACKAGECONFIG[sc2] = "-DUSE_SC2=ON,-DUSE_SC2=OFF,"

PACKAGECONFIG:append += "${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'eq_drc', '', d)}"
PACKAGECONFIG[eq_drc] = "-DUSE_EQ_DRC=ON,-DUSE_EQ_DRC=OFF,"

PACKAGECONFIG:append += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-amadec', '', 'amadec', d)}"
PACKAGECONFIG[amadec] = "-DUSE_AMADEC=ON,-DUSE_AMADEC=OFF,"

SRC_URI  += "\
  file://aml_audio_config.json \
  file://aml_audio_config.ah212.json \
  file://aml_audio_config.am301.json \
  file://aml_audio_config.at301.json \
  file://aml_audio_config.ap222.json \
  file://aml_audio_config.u212.json \
  file://mixer_paths.xml \
  file://mixer_paths.at301.xml \
  file://mixer_paths.t5d.xml \
"

PROPERTY_SET_CONF = "aml_audio_config.json"
PROPERTY_SET_CONF:ah212 = "aml_audio_config.ah212.json"
PROPERTY_SET_CONF:u212 = "aml_audio_config.u212.json"
PROPERTY_SET_CONF:am301 = "aml_audio_config.am301.json"
PROPERTY_SET_CONF:at301 = "aml_audio_config.at301.json"
PROPERTY_SET_CONF:ap222 = "aml_audio_config.ap222.json"
PROPERTY_SET_MIXER = "mixer_paths.xml"
PROPERTY_SET_MIXER:at301 = "mixer_paths.at301.xml"
PROPERTY_SET_MIXER:t5d = "mixer_paths.t5d.xml"

do_install:append() {
    install -d ${D}/${sysconfdir}/halaudio
    install -m 0755 ${WORKDIR}/${PROPERTY_SET_CONF} ${D}/${sysconfdir}/halaudio/aml_audio_config.json
    install -m 0644 ${WORKDIR}/${PROPERTY_SET_MIXER} ${D}/${sysconfdir}/mixer_paths.xml
    if ${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', 'true', 'false', d)}; then
        sed -i '/Codec_Support_List/i \\t"Audio_Delay_Max":100,' ${D}/${sysconfdir}/halaudio/aml_audio_config.json
    fi
}

FILES:${PN} = "${libdir}/* ${bindir}/* ${sysconfdir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf already-stripped"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
