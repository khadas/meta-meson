SUMMARY = "Bluetooth Audio ALSA Backend"
HOMEPAGE = "https://github.com/Arkq/bluez-alsa"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=bb3e99e80c5d718213f35ae1def4c106"

SRC_URI = " \
    git://github.com/Arkq/bluez-alsa.git;protocol=https;branch=master \
"
SRCREV = "9045edb436ea755f395a2e09e4525b5defad286a"

SRC_URI += "file://0001-BT-a2dp-sink-add-aml-hal-audio-support-1-1.patch"
SRC_URI += "file://0001-bt-fix-bluealsa-pcm-device-can-t-found-1-2.patch"
SRC_URI += "file://0001-BT-add-a2dp_ctl-hfp_ctl-1-1.patch"
SRC_URI += "file://0001-Allow-user-to-enable-profiles-selectively.patch"
SRC_URI += "file://0001-BT-provide-A2DP-control-API-via-lib-1-1.patch"
SRC_URI += "file://bluez-alsa.sh"
SRC_URI += "file://0001-BT-provide-A2DP-alsa-fix-48000-1-1.patch"
SRC_URI += "file://0001-BT-provide-A2DP-alsa-fix-mute-1-1.patch"


S  = "${WORKDIR}/git"

DEPENDS += "alsa-lib bluez5 systemd glib-2.0 sbc"
DEPENDS += "aml-audio-service"
RDEPENDS:${PN} += "aml-audio-service"

PACKAGECONFIG[debug]  = "--enable-debug"
#PACKAGECONFIG[datadir]  = "--datadir=/etc"
PACKAGECONFIG[aac]  = "--enable-aac, --disable-aac, "
PACKAGECONFIG[aptx] = "--enable-aptx,--disable-aptx,"
PACKAGECONFIG[hcitop]   = "--enable-hcitop,  --disable-hcitop,  libbsd ncurses"

inherit autotools pkgconfig
inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
PACKAGECONFIG += "debug"
#PACKAGECONFIG += "datadir"


do_install:append () {
    install -d ${D}${base_libdir}/systemd/system
    install -m 0755 ${WORKDIR}/bluez-alsa.sh ${D}${bindir}
    install -m 0755 ${B}/utils/bt-halplay ${D}${bindir}
    install -m 0755 ${B}/utils/a2dp_ctl ${D}${bindir}
    install -m 0755 ${B}/utils/hfp_ctl ${D}${bindir}
}

FILES:${PN} += "\
    ${libdir}/liba2dp_ctl.so\
    ${libdir}/alsa-lib/libasound_module_ctl_bluealsa.so\
    ${libdir}/alsa-lib/libasound_module_pcm_bluealsa.so\
"

FILES:${PN}-staticdev += "\
    ${libdir}/liba2dp_ctl.a\
    ${libdir}/alsa-lib/libasound_module_ctl_bluealsa.a\
    ${libdir}/alsa-lib/libasound_module_pcm_bluealsa.a\
