SUMMARY = "aml audio service"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/audio_server;protocol=${AML_GIT_PROTOCOL};branch=master"
SRC_URI:append = " file://audioserver.service"
SRC_URI:append = " file://audioserver.init"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/hal_audio_service')}"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

PROVIDES = "${PN}-testapps"
PACKAGES =+ "\
    ${PN}-testapps \
    "

do_configure[noexec] = "1"
inherit autotools pkgconfig systemd update-rc.d

INITSCRIPT_NAME = "audioserver"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 6 1 ."

S="${WORKDIR}/git"

ENABLE_APLUGIN = "no"
EXTRA_OEMAKE:append = "${@bb.utils.contains('ENABLE_APLUGIN', 'yes', ' aplugin=y', '', d)}"
DEPENDS += " grpc grpc-native boost aml-amaudioutils protobuf-native liblog dolby-ms12"
DEPENDS:append = "${@bb.utils.contains('ENABLE_APLUGIN', 'yes', ' alsa-lib', '', d)}"
RDEPENDS:${PN} += " aml-amaudioutils liblog"
RDEPENDS:${PN}-testapps += " ${PN} liblog"

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
        install -d ${D}/usr/include/hardware
        install -d ${D}/usr/include/system
        install -m 755 -D ${S}/audio_server -t ${D}/usr/bin/
        install -m 755 -D ${S}/audio_client_test -t ${D}/usr/bin/
        install -m 755 -D ${S}/audio_client_test_ac3 ${D}/usr/bin/
        install -m 755 -D ${S}/halplay ${D}/usr/bin/
        install -m 755 -D ${S}/hal_dump ${D}/usr/bin/
        install -m 755 -D ${S}/hal_capture ${D}/usr/bin/
        install -m 755 -D ${S}/hal_param ${D}/usr/bin/
        install -m 755 -D ${S}/master_vol ${D}/usr/bin/
        install -m 755 -D ${S}/dap_setting ${D}/usr/bin/
        install -m 755 -D ${S}/digital_mode ${D}/usr/bin/
        install -m 755 -D ${S}/speaker_delay ${D}/usr/bin/
        install -m 755 -D ${S}/start_arc ${D}/usr/bin/
        install -m 755 -D ${S}/test_arc ${D}/usr/bin/
        install -m 644 -D ${S}/libaudio_client.so -t ${D}${libdir}
        install -m 644 -D ${S}/include/audio_if_client.h -t ${D}/usr/include
        install -m 644 -D ${S}/include/audio_if.h -t ${D}/usr/include
        install -m 644 -D ${S}/include/audio_effect_if.h -t ${D}/usr/include
        install -m 644 -D ${S}/include/audio_effect_params.h -t ${D}/usr/include
        if ${@bb.utils.contains("ENABLE_APLUGIN", "yes", "true", "false", d)}; then
            install -m 644 -D ${S}/libasound_module_pcm_ahal.so -t ${D}${libdir}/alsa-lib/
        fi
        for f in ${S}/include/hardware/*.h; do \
            install -m 644 -D ${f} -t ${D}/usr/include/hardware; \
        done
        for f in ${S}/include/system/*.h; do \
            install -m 644 -D ${f} -t ${D}/usr/include/system; \
        done
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
}

SYSTEMD_SERVICE:${PN} = "audioserver.service "
FILES:${PN} = "${libdir}/* ${bindir}/audio_server /etc/systemd/system.conf.d/* ${sysconfdir} "
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
