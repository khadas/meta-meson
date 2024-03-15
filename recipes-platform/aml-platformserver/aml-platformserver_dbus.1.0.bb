SUMMARY = "Amlogic Platform Server"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "file://aml_platformserver.service"
SRC_URI += "file://aml_platformserver.socket"
SRC_URI += "file://aml_platformserver.conf"

#SRCREV ?= "${AUTOREV}"

S = "${WORKDIR}/git"
inherit pkgconfig systemd
do_configure[noexec] = "1"

TOOLCHAIN = "gcc"
CODEGENDIR = "${S}/generated"
BUILDDIR = "${WORKDIR}/build"
EXTRA_OEMAKE="BUILDDIR=${BUILDDIR} \
              DESTDIR=${D} \
              CODEGENDIR=${CODEGENDIR} \
             "

EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', '', 'PLF_CONFIG_DISABLE_BLUETOOTH=y', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'wifi', '', 'PLF_CONFIG_DISABLE_WIFI=y', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'cec', '', 'PLF_CONFIG_DISABLE_CEC=y', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'voiceinput', '', 'PLF_CONFIG_DISABLE_VOICE_INPUT=y', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'screencapture', '', 'PLF_CONFIG_DISABLE_SCREEN_CAPTURE=y', d)}"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez-alsa', '', d)}"
DEPENDS += "wpa-supplicant"
DEPENDS += "libdrm"
DEPENDS += "tinyalsa aml-audio-service"
DEPENDS += "aml-hdmicec"
DEPENDS += "python3-native"
DEPENDS += "aml-ubootenv"
DEPENDS += "leveldb"
DEPENDS += "linux-uapi-headers"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'screencapture', 'libjpeg-turbo', '', d)}"
DEPENDS += "linux-uapi-headers"
DEPENDS += "aml-dbus"

RDEPENDS:${PN} += "wpa-supplicant"
RDEPENDS:${PN} += "aml-audio-service"
RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez5 bluez-alsa', '', d)}"
RDEPENDS:${PN} += "aml-ubootenv"
RDEPENDS:${PN} += "leveldb"
RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'screencapture', 'libjpeg-turbo', '', d)}"
RDEPENDS:${PN} += "aml-property"

INCLUDE_DIRS = " \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm \
    "

EXTRA_CFLAGS = " \
           ${INCLUDE_DIRS} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', ' -DBUILD_AML_TV', ' -DBUILD_AML_STB', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'arc-via-spdif', ' -DARC_FROM_SPDIF', '', d)} \
           "
CFLAGS += "${EXTRA_CFLAGS}"
CXXFLAGS += "${EXTRA_CFLAGS}"

# tts related
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'tts', '', 'PLF_CONFIG_DISABLE_TTS=y', d)}"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'tts', 'gstreamer1.0 gstreamer1.0-plugins-base gst-plugin-aml-asink picotts', '', d)}"
RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'tts', 'gstreamer1.0 gstreamer1.0-plugins-base gst-plugin-aml-asink picotts', '', d)}"

do_compile(){
    cd ${S}
    oe_runmake -f dbus/Makefile all
}

do_install() {
    cd ${S}
    install -d ${D}${includedir}/platform_service ${D}${libdir}/pkgconfig ${D}${systemd_unitdir}/system ${D}${sysconfdir}/systemd/system.conf.d ${D}${sysconfdir}/dbus-1/system.d ${D}${libdir} ${D}${bindir}
    cp --no-preserve=ownership -af ${S}/client/*.h ${D}${includedir}/platform_service
    cp --no-preserve=ownership -af ${S}/lib/*.h ${D}${includedir}/platform_service
    cp --no-preserve=ownership -af ${S}/include/*.h ${D}${includedir}/platform_service
    cp --no-preserve=ownership -af ${BUILDDIR}/*.so.0 ${BUILDDIR}/*.so ${D}${libdir}
    cp --no-preserve=ownership -af ${BUILDDIR}/aml_platformservice ${BUILDDIR}/aml_platformtest ${D}${bindir}
    install -m 644 ${S}/aml-platform-client.pc ${D}${libdir}/pkgconfig
    install -m 0644 ${WORKDIR}/aml_platformserver.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/aml_platformserver.socket ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/aml_platformserver.conf ${D}${sysconfdir}/systemd/system.conf.d
    install -m 0644 ${S}/dbus/amlogic.yocto.sdk.conf ${D}${sysconfdir}/dbus-1/system.d/
    if ${@bb.utils.contains("DISTRO_FEATURES", "system-user", "true", "false", d)}; then
      sed -i 's/\(SocketGroup=\).*/\1session/' ${D}${systemd_unitdir}/system/aml_platformserver.socket
    fi
    if ${@bb.utils.contains("DISTRO_FEATURES", "amlogic-tv", "false", "true", d)}; then
      sed -i '/ExecStart=\/usr\/bin\/aml_platformservice/i\ExecStartPre=\/bin\/sh -c '\''echo 1 > /sys\/class\/amhdmitx\/amhdmitx0\/rxsense_policy'\''' ${D}${systemd_unitdir}/system/aml_platformserver.service
    fi
}

SYSTEMD_SERVICE:${PN} = "aml_platformserver.service aml_platformserver.socket"

# ----------------------------------------------------------------------------

FILES:${PN} += "${libdir}/* ${bindir}/* ${sysconfdir}/systemd/system.conf.d/* ${sysconfdir}/dbus-1/system.d/*"
FILES:${PN}-dev += "${includedir}/* ${libdir}/pkgconfig/*"

PROVIDES = " libaml_platform.so.0 libaml_platform_client.so.0"
RPROVIDES:${PN} += " libaml_platform.so.0()(64bit) libaml_platform_client.so.0()(64bit)"
INSANE_SKIP:${PN} = "ldflags"
