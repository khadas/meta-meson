SUMMARY = "Bluetooth Audio ALSA Backend"
HOMEPAGE = "https://github.com/Arkq/bluez-alsa"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8449a4f133a93f6254b496d4fb476e83"

SRC_URI = "git://github.com/Arkq/bluez-alsa.git;protocol=https;branch=master"
SRCREV = "dd05a1baa261b94da9ba977b5e3392c8b4dc7a5e"
PV = "4.0.0+git${SRCPV}"

SRC_URI += "file://bluez-alsa.sh"
SRC_URI += "file://0001-bluealsa-add-client-interface.patch"
SRC_URI += "file://0002-bluealsa-add-support-system-user.patch"

S  = "${WORKDIR}/git"

DEPENDS += "alsa-lib bluez5 dbus glib-2.0 sbc"
DEPENDS += "aml-audio-service"
RDEPENDS:${PN} += "aml-audio-service"


PACKAGECONFIG ??= "aplay cli hcitop aml ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[a2dpconf]  = "--enable-a2dpconf,--disable-a2dpconf"
PACKAGECONFIG[aac]  = "--enable-aac,--disable-aac,fdk-aac"
PACKAGECONFIG[aplay] = "--enable-aplay,--disable-aplay"
PACKAGECONFIG[cli] = "--enable-cli,--disable-cli"
PACKAGECONFIG[aml] = "--enable-aml,--disable-aml"
PACKAGECONFIG[coverage] = "--with-coverage,--without-coverage,lcov-native"
PACKAGECONFIG[debug] = "--enable-debug,--disable-debug"
PACKAGECONFIG[debug-time] = "--enable-debug-time,--disable-debug-time"
PACKAGECONFIG[faststream] = "--enable-faststream,--disable-faststream"
PACKAGECONFIG[hcitop] = "--enable-hcitop,--disable-hcitop,libbsd ncurses"
PACKAGECONFIG[libunwind] = "--with-libunwind,--without-libunwind,libunwind"
PACKAGECONFIG[mp3lame] = "--enable-mp3lame,--disable-mp3lame,lame"
PACKAGECONFIG[mpg123] = "--enable-mpg123,--disable-mpg123,mpg123,mpg123"
PACKAGECONFIG[ofono] = "--enable-ofono,--disable-ofono,ofono"
PACKAGECONFIG[payloadcheck] = "--enable-payloadcheck,--disable-payloadcheck"
PACKAGECONFIG[rfcomm] = "--enable-rfcomm,--disable-rfcomm"
PACKAGECONFIG[systemd] = "--enable-systemd --with-systemdsystemunitdir=${systemd_system_unitdir} \
                          --with-systemdbluealsaargs='${SYSTEMD_BLUEALSA_ARGS}' --with-systemdbluealsaaplayargs='${SYSTEMD_BLUEALSA_APLAY_ARGS}',--disable-systemd,systemd"
PACKAGECONFIG[test] = "--enable-test,--disable-test,libcheck libsndfile1"
PACKAGECONFIG[upower] = "--enable-upower,--disable-upower,,upower"

inherit autotools pkgconfig systemd

EXTRA_OECONF = " \
    --disable-aptx \
    --disable-lc3plus \
    --disable-ldac \
    --disable-manpages \
"

PACKAGE_BEFORE_PN = "${PN}-aplay"
RRECOMMENDS:${PN} = "${PN}-aplay"

do_configure:prepend () {
    cp -dR ${THISDIR}/files/aml/ ${S}/utils/
}

do_install:append () {
    install -d ${D}${base_libdir}/systemd/system
    install -m 0755 ${WORKDIR}/bluez-alsa.sh ${D}${bindir}
}

FILES:${PN} += "\
    ${libdir}/liba2dp_ctl.so\
    ${libdir}/alsa-lib/libasound_module_ctl_bluealsa.so\
    ${libdir}/alsa-lib/libasound_module_pcm_bluealsa.so\
"

FILES:${PN}-aplay = "${bindir}/bluealsa-aplay"
FILES:${PN} += "${libdir}/alsa-lib/*"

SYSTEMD_PACKAGES += "${PN}-aplay"
#SYSTEMD_SERVICE:${PN} = "bluealsa.service"
#SYSTEMD_SERVICE:${PN}-aplay = "bluealsa-aplay.service"

SYSTEMD_AUTO_ENABLE:${PN}-aplay = "disable"

# Choose bluez-alsa arguments to be used in bluealsa systemd service
# Usually could choose profiles with it: a2dp-source a2dp-sink hfp-hf hfp-ag hsp-hs hsp-ag hfp-ofono
# Enable bluez-alsa arguments by default:
SYSTEMD_BLUEALSA_ARGS ?= "-p a2dp-source -p a2dp-sink"

# Choose bluealsa-aplay arguments to be used in bluealsa-aplay systemd service
# Defaults to be empty:
SYSTEMD_BLUEALSA_APLAY_ARGS ?= ""
INSANE_SKIP:${PN} = "installed-vs-shipped"
