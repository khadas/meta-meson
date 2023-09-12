SUMMARY = "Arka zapper application"
LICENSE = "CLOSED"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "git://${AML_GIT_ROOT}${AML_GIT_ROOT_YOCTO_SUFFIX}/zapper/arka;protocol=${AML_GIT_PROTOCOL};branch=master;nobranch=1"
SRC_URI:append = " file://arka.service "
SRC_URI:append = " file://arka.init "
SRCREV = "${AUTOREV}"

PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake pkgconfig systemd update-rc.d

INITSCRIPT_NAME = "arka"
INITSCRIPT_PARAMS = "start 80 2 3 4 5 . stop 80 0 6 1 ."

SYSTEMD_AUTO_ENABLE:${PN} = "enable"

DEPENDS = " dtvkit-release-prebuilt jsoncpp libbinder aml-audio-service meson-display udev aml-hdmicec aml-mp-sdk"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', 'westeros freetype', 'directfb', d)}"

RDEPENDS:${PN} = "dtvkit-release-prebuilt aml-audio-service"

OECMAKE_GENERATOR = "Unix Makefiles"
EXTRA_OEMAKE="STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D}  -D_STBLABS_SAT_SCAN_"
EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-audio-server', '-DDISABLE_AUDIO_SERVER=1', '', d)}"
EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'arka-project-sun', '-DARKA_PROJECT=sunepg', \
    bb.utils.contains('DISTRO_FEATURES', 'arka-project-aml', '-DARKA_PROJECT=amlepg', '-DARKA_PROJECT=aslepg', d), d)}"
EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', '-DUSE_EGL=ON', '', d)}"

INCLUDE_DIRS = " \
    -I${STAGING_DIR_TARGET}${libdir}/include/ \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm_meson/ \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm/ \
    -I${STAGING_DIR_TARGET}${includedir}/display_settings/ \
    "

INCLUDE_DIRS += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', ' \
     -I${STAGING_DIR_TARGET}${includedir}/ \
     -I${STAGING_DIR_TARGET}${includedir}/freetype2/', \
    '-I${STAGING_DIR_TARGET}${includedir}/directfb/', d)} \
    "

TARGET_CFLAGS += "-fPIC -D_REENTRANT ${INCLUDE_DIRS}"
TARGET_CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', ' -shared -rdynamic ', '', d)}"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'ArkaDvb', 'false', 'true', d)}; then
      install -D -m 0644 ${WORKDIR}/arka.service ${D}${systemd_unitdir}/system/arka.service

      install -d ${D}${sysconfdir}/init.d
      install -m 0755 ${WORKDIR}/arka.init ${D}${sysconfdir}/init.d/arka
    fi
}
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'ArkaDvb', '', 'arka.service', d)}"

FILES:${PN} += "${bindir} ${sysconfdir} /usr/share/fonts/ /usr/share/Arka/png ${systemd_unitdir}/system/"
FILES_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', '${libdir}', '', d)}"
FILES_${PN}-dev = ""
