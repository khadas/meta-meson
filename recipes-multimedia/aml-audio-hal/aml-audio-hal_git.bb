SUMMARY = "aml audio utils"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/amlogic/audio;protocol=${AML_GIT_PROTOCOL};branch=linux-buildroot"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/aml_audio_hal')}"

DEPENDS += "liblog aml-amaudioutils expat aml-avsync aml-dvbaudioutils aml-dvb"
RDEPENDS_${PN} += "liblog aml-amaudioutils aml-avsync aml-dvbaudioutils aml-dvb libfaad-aml libmad-aml"

inherit cmake pkgconfig

S="${WORKDIR}/git"
TARGET_CFLAGS += "-fPIC"

PACKAGECONFIG_append_sc2 += "dtv"
PACKAGECONFIG_append_s4 += "dtv"
PACKAGECONFIG_append_t5d += "dtv"
PACKAGECONFIG_append_t5w += "dtv"
PACKAGECONFIG_append_t3 += "dtv"
PACKAGECONFIG[dtv] = "-DUSE_DTV=ON,-DUSE_DTV=OFF,"

PACKAGECONFIG += "msync"
PACKAGECONFIG[msync] = "-DUSE_MSYNC=ON,-DUSE_MSYNC=OFF,"

PACKAGECONFIG_append_sc2 += "sc2"
PACKAGECONFIG_append_s4 += "sc2"
PACKAGECONFIG[sc2] = "-DUSE_SC2=ON,-DUSE_SC2=OFF,"

PACKAGECONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'eq_drc', '', d)}"
PACKAGECONFIG[eq_drc] = "-DUSE_EQ_DRC=ON,-DUSE_EQ_DRC=OFF,"

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
PROPERTY_SET_CONF_ah212 = "aml_audio_config.ah212.json"
PROPERTY_SET_CONF_u212 = "aml_audio_config.u212.json"
PROPERTY_SET_CONF_am301 = "aml_audio_config.am301.json"
PROPERTY_SET_CONF_at301 = "aml_audio_config.at301.json"
PROPERTY_SET_CONF_ap222 = "aml_audio_config.ap222.json"
PROPERTY_SET_MIXER = "mixer_paths.xml"
PROPERTY_SET_MIXER_at301 = "mixer_paths.at301.xml"
PROPERTY_SET_MIXER_t5d = "mixer_paths.t5d.xml"

do_install_append() {
    install -d ${D}/${sysconfdir}/halaudio
    install -m 0755 ${WORKDIR}/${PROPERTY_SET_CONF} ${D}/${sysconfdir}/halaudio/aml_audio_config.json
    install -m 0644 ${WORKDIR}/${PROPERTY_SET_MIXER} ${D}/${sysconfdir}/mixer_paths.xml
}

FILES_${PN} = "${libdir}/* ${bindir}/* ${sysconfdir}/*"
FILES_${PN}-dev = "${includedir}/* "
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf already-stripped"
INSANE_SKIP_${PN}-dev = "dev-so ldflags dev-elf"
