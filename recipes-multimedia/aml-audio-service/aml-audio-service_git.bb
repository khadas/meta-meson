SUMMARY = "aml audio service"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/audio_server;protocol=${AML_GIT_PROTOCOL};branch=master"
SRC_URI:append = " file://audioserver.service"
SRC_URI:append = " file://audioserver.init"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/hal_audio_service')}"

#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

PROVIDES = "${PN}-testapps"
PACKAGES =+ "\
    ${PN}-testapps \
    "

do_configure[noexec] = "1"
inherit autotools pkgconfig systemd
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'disable-audio-server', ' ', 'update-rc.d', d)}

INITSCRIPT_NAME = "audioserver"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 6 1 ."

S="${WORKDIR}/git"

EXTRA_OEMAKE = "AML_BUILD_DIR=${B}"
ENABLE_APLUGIN = "no"
EXTRA_OEMAKE:append = "${@bb.utils.contains('ENABLE_APLUGIN', 'yes', ' aplugin=y', '', d)}"
DEPENDS += " aml-amaudioutils liblog aml-audio-hal"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-audio-server', '', ' grpc grpc-native boost protobuf-native dolby-ms12', d)}"
DEPENDS:append = "${@bb.utils.contains('ENABLE_APLUGIN', 'yes', ' alsa-lib', '', d)}"
RDEPENDS:${PN} += " aml-amaudioutils liblog aml-audio-hal"
RDEPENDS:${PN}-testapps += " ${PN} liblog"

EXTRA_OEMAKE:append = "${@bb.utils.contains('DISTRO_FEATURES', 'disable-audio-server', ' rm_audioserver=y', '', d)}"

export TARGET_DIR = "${D}"
export HOST_DIR = "${STAGING_DIR_NATIVE}/usr/"

#To remove --as-needed compile option which is causing issue with linking
#ASNEEDED = ""
#PARALLEL_MAKE = ""
do_compile() {
    oe_runmake  -C ${S} all
}
do_install() {
        install -d ${D}${libdir}
        install -d ${D}/usr/bin
        install -d ${D}/usr/include
        install -m 755 -D ${B}/halplay ${D}/usr/bin/
        install -m 755 -D ${B}/hal_dump ${D}/usr/bin/
        install -m 755 -D ${B}/hal_capture ${D}/usr/bin/
        install -m 755 -D ${B}/hal_param ${D}/usr/bin/
        install -m 755 -D ${B}/master_vol ${D}/usr/bin/
        install -m 755 -D ${B}/dap_setting ${D}/usr/bin/
        install -m 755 -D ${B}/digital_mode ${D}/usr/bin/
        install -m 755 -D ${B}/speaker_delay ${D}/usr/bin/
        install -m 755 -D ${B}/start_arc ${D}/usr/bin/
        install -m 755 -D ${B}/test_arc ${D}/usr/bin/
        install -m 644 -D ${B}/libaudio_client.so -t ${D}${libdir}
        install -m 644 -D ${S}/include/audio_if.h -t ${D}/usr/include

        if ${@bb.utils.contains("ENABLE_APLUGIN", "yes", "true", "false", d)}; then
            install -m 644 -D ${B}/libasound_module_pcm_ahal.so -t ${D}${libdir}/alsa-lib/
        fi

        if ${@bb.utils.contains('DISTRO_FEATURES', 'disable-audio-server', 'false', 'true', d)}; then
            install -m 755 -D ${B}/audio_server -t ${D}/usr/bin/
            install -m 755 -D ${B}/audio_client_test -t ${D}/usr/bin/
            install -m 755 -D ${B}/audio_client_test_ac3 ${D}/usr/bin/
            install -m 644 -D ${S}/include/audio_if_client.h -t ${D}/usr/include
            install -m 644 -D ${S}/include/audio_effect_if.h -t ${D}/usr/include
            install -m 644 -D ${S}/include/audio_effect_params.h -t ${D}/usr/include

            if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
                install -D -m 0644 ${WORKDIR}/audioserver.service ${D}${systemd_unitdir}/system/audioserver.service
                install -d ${D}/etc/systemd/system.conf.d
                cat << EOF > ${D}/etc/systemd/system.conf.d/audioserver.conf
[Manager]
DefaultEnvironment=AUDIO_SERVER_SOCKET=unix:///run/audio_socket
EOF
                if ${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', 'true', 'false', d)}; then
                    sed -i '/Environment/a\Environment=\"AUDIO_SERVER_SHMEM_SIZE=4194304\"' ${D}${systemd_unitdir}/system/audioserver.service
                fi
            fi

            install -d ${D}${sysconfdir}/init.d
            install -m 0755 ${WORKDIR}/audioserver.init ${D}${sysconfdir}/init.d/audioserver
        else
            install -m 644 -D ${B}/libamlaudiosetting.so -t ${D}${libdir}
            install -m 644 -D ${S}/include/AML_Audio_Setting.h -t ${D}/usr/include
        fi
}

SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'disable-audio-server', ' ', 'audioserver.service ', d)}"
FILES:${PN} = "${libdir}/* ${sysconfdir} "
FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-audio-server', ' ', '${bindir}/audio_server ', d)}"
FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '/etc/systemd/system.conf.d/* ', ' ', d)}"
FILES:${PN}-testapps = "\
                        ${bindir}/audio_client_test \
                        ${bindir}/audio_client_test_ac3 \
                        ${bindir}/halplay \
                        ${bindir}/hal_dump \
                        ${bindir}/hal_capture \
                        ${bindir}/hal_param \
                        ${bindir}/master_vol \
                        ${bindir}/dap_setting \
                        ${bindir}/digital_mode \
                        ${bindir}/speaker_delay \
                        ${bindir}/start_arc \
                        ${bindir}/test_arc \
                        "
FILES:${PN}-testapps-dev = ""
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-testapps = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
