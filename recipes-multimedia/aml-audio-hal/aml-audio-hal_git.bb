SUMMARY = "aml audio utils"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/amlogic/audio;protocol=${AML_GIT_PROTOCOL};branch=linux-buildroot"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/aml_audio_hal')}"

DEPENDS += "liblog aml-amaudioutils expat aml-dvbaudioutils aml-dvb"
RDEPENDS:${PN} += "liblog aml-amaudioutils aml-dvbaudioutils aml-dvb libfaad-aml libmad-aml libflac-aml libadpcm-aml"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-msync', '', 'aml-avsync', d)}"
RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-msync', '', 'aml-avsync', d)}"

EXTRA_OECMAKE = "-DAML_BUILD_DIR=${B}"
inherit cmake pkgconfig

S="${WORKDIR}/git"
TARGET_CFLAGS += "-fPIC"

PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'aml-dtv', 'dtv', '', d)}"
PACKAGECONFIG[dtv] = "-DUSE_DTV=ON,-DUSE_DTV=OFF,"

PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-msync', '', 'msync', d)}"
PACKAGECONFIG[msync] = "-DUSE_MSYNC=ON,-DUSE_MSYNC=OFF,"

PACKAGECONFIG:append:sc2 = " sc2"
PACKAGECONFIG:append:s4 = " sc2"
PACKAGECONFIG[sc2] = "-DUSE_SC2=ON,-DUSE_SC2=OFF,"

PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'eq_drc', '', d)}"
PACKAGECONFIG[eq_drc] = "-DUSE_EQ_DRC=ON,-DUSE_EQ_DRC=OFF,"

PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'disable-amadec', '', 'amadec', d)}"
PACKAGECONFIG[amadec] = "-DUSE_AMADEC=ON,-DUSE_AMADEC=OFF,"

PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'disable-audio-server', 'rm_audioserver', '', d)}"
PACKAGECONFIG[rm_audioserver] = "-DDISABLE_SERVER=ON,-DDISABLE_SERVER=OFF,"

SRC_URI  += "\
  file://aml_audio_config.json \
  file://aml_audio_config.ah212.json \
  file://aml_audio_config.am301.json \
  file://aml_audio_config.at301.json \
  file://aml_audio_config.ap222.json \
  file://aml_audio_config.u212.json \
  file://aml_audio_config.tv.json \
  file://aml_audio_config.s1a.json \
  file://mixer_paths.xml \
  file://mixer_paths.at301.xml \
  file://mixer_paths.t7.xml \
  file://mixer_paths.t5d.xml \
  file://audio_hal_delay_base.json \
  file://audio_hal_delay_base.at301.json \
  file://audio_hal_delay_base.am301.json \
  file://audio_hal_delay_base.ap222.json \
  file://audio_hal_delay_base.ah212.json \
  file://audio_hal_delay_base.ap232.json \
  file://audio_hal_delay_base.s1a.json \
  file://ms12_audio_profiles.ini \
"

PROPERTY_SET_CONF = "aml_audio_config.json"
PROPERTY_SET_CONF:ah212 = "aml_audio_config.ah212.json"
PROPERTY_SET_CONF:u212 = "aml_audio_config.u212.json"
PROPERTY_SET_CONF:am301 = "aml_audio_config.am301.json"
PROPERTY_SET_CONF:at301 = "aml_audio_config.at301.json"
PROPERTY_SET_CONF:ap222 = "aml_audio_config.ap222.json"
PROPERTY_SET_CONF:t7 = "aml_audio_config.tv.json"
PROPERTY_SET_CONF:s1a = "aml_audio_config.s1a.json"

PROPERTY_SET_MIXER = "mixer_paths.xml"
PROPERTY_SET_MIXER:at301 = "mixer_paths.at301.xml"
PROPERTY_SET_MIXER:t7 = "mixer_paths.t7.xml"
PROPERTY_SET_MIXER:t5d = "mixer_paths.t5d.xml"
PROPERTY_SET_AVSYNC = "audio_hal_delay_base.json"
PROPERTY_SET_AVSYNC:at301 = "audio_hal_delay_base.at301.json"
PROPERTY_SET_AVSYNC:am301 = "audio_hal_delay_base.am301.json"
PROPERTY_SET_AVSYNC:ah212 = "audio_hal_delay_base.ah212.json"
PROPERTY_SET_AVSYNC:ap222 = "audio_hal_delay_base.ap222.json"
PROPERTY_SET_AVSYNC:ap232 = "audio_hal_delay_base.ap232.json"
PROPERTY_SET_AVSYNC:s1a = "audio_hal_delay_base.s1a.json"

do_install:append() {
    install -d ${D}/usr/include/hardware
    install -d ${D}/usr/include/system
    install -d ${D}/${sysconfdir}/halaudio
    install -m 0755 ${WORKDIR}/${PROPERTY_SET_CONF} ${D}/${sysconfdir}/halaudio/aml_audio_config.json
    install -m 0755 ${WORKDIR}/${PROPERTY_SET_AVSYNC} ${D}/${sysconfdir}/halaudio/audio_hal_delay_base.json
    install -m 0755 ${WORKDIR}/ms12_audio_profiles.ini ${D}/${sysconfdir}/halaudio/ms12_audio_profiles.ini
    install -m 0644 ${WORKDIR}/${PROPERTY_SET_MIXER} ${D}/${sysconfdir}/mixer_paths.xml
    if ${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', 'true', 'false', d)}; then
        sed -i '/Codec_Support_List/i \\t"Audio_Delay_Max":100,' ${D}/${sysconfdir}/halaudio/aml_audio_config.json
    fi

    for f in ${S}/include/hardware/*.h; do \
        install -m 644 -D ${f} -t ${D}/usr/include/hardware; \
    done
    for f in ${S}/include/system/*.h; do \
        install -m 644 -D ${f} -t ${D}/usr/include/system; \
    done
}

FILES:${PN} = "${libdir}/* ${bindir}/* ${sysconfdir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf already-stripped"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
