SUMMARY = "Arka zapper application"
LICENSE = "CLOSED"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://arka.service "
SRC_URI:append = " file://arka.init "
SRC_URI:append = " file://Kolkata "
#SRCREV = "${AUTOREV}"

PV = "git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake pkgconfig systemd update-rc.d
FILES_SOLIBSDEV = ""
INITSCRIPT_NAME = "arka"
INITSCRIPT_PARAMS = "start 80 2 3 4 5 . stop 80 0 6 1 ."

SYSTEMD_AUTO_ENABLE:${PN} = "enable"

DEPENDS = " dtvkit-release-prebuilt jsoncpp aml-audio-service udev aml-mp-sdk aml-ubootenv"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', \
    bb.utils.contains('DISTRO_FEATURES', 'weston', 'weston freetype', 'westeros freetype curl', d), 'meson-display aml-hdmicec jpeg', d)}"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'arka-hdi', 'aml-hdi freetype', ' directfb ', d)}"

RDEPENDS:${PN} = "dtvkit-release-prebuilt aml-audio-service aml-ubootenv"

OECMAKE_GENERATOR = "Unix Makefiles"
EXTRA_OEMAKE="STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D}  -D_STBLABS_SAT_SCAN_"
EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-audio-server', '-DDISABLE_AUDIO_SERVER=1', '', d)}"
EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'arka-project-sun', '-DARKA_PROJECT=sunepg', \
    bb.utils.contains('DISTRO_FEATURES', 'arka-project-aml', '-DARKA_PROJECT=amlepg', '-DARKA_PROJECT=aslepg', d), d)}"
EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', \
    bb.utils.contains('DISTRO_FEATURES', 'weston', '-DUSE_EGL=ON -DUSE_WESTON=ON', '-DUSE_EGL=ON -DUSE_AML_MP=ON', d), '', d)}"
EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'arka-hdi',  '-DUSE_AML_HDI=ON',  ' ', d)}"

INCLUDE_DIRS = " \
    -I${STAGING_DIR_TARGET}${libdir}/include/ \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm_meson/ \
    -I${STAGING_DIR_TARGET}${includedir}/libdrm/ \
    -I${STAGING_DIR_TARGET}${includedir}/display_settings/ \
    "

INCLUDE_DIRS += "${@bb.utils.contains('DISTRO_FEATURES', 'arka-hdi', ' \
     -I${STAGING_DIR_TARGET}${includedir}/ \
     -I${STAGING_DIR_TARGET}${includedir}/freetype2/', \
    '-I${STAGING_DIR_TARGET}${includedir}/directfb/', d)} \
    "

INCLUDE_DIRS += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', ' \
     -I${STAGING_DIR_TARGET}${includedir}/ \
     -I${STAGING_DIR_TARGET}${includedir}/freetype2/', \
    ' ', d)} \
    "

TARGET_CFLAGS += "-fPIC -D_REENTRANT ${INCLUDE_DIRS}"
TARGET_CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', ' -shared -rdynamic ', '', d)}"
TARGET_CXXFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', ' -fpermissive ', '', d)}"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'arka-launcher', 'true', 'false', d)}; then
      install -D -m 0644 ${WORKDIR}/arka.service ${D}${systemd_unitdir}/system/arka.service

      install -d ${D}${sysconfdir}/init.d
      install -m 0755 ${WORKDIR}/arka.init ${D}${sysconfdir}/init.d/arka

      if ${@bb.utils.contains("DISTRO_FEATURES", "zapper-128m-sei", "true", "false", d)}
      then
        sed -i '/# Starts arka service\./i\export IRDETO_USING_DEVICE_INFO=1' ${D}${sysconfdir}/init.d/arka
      fi
    fi

   if ${@bb.utils.contains('DISTRO_FEATURES', 'zapper', 'true', 'false', d)}; then
    install -d ${D}/usr/share/zoneinfo/Asia/
    install -m 0755 ${WORKDIR}/Kolkata ${D}/usr/share/zoneinfo/Asia
   fi

   if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
      install -d ${D}/etc/systemd/system.conf.d
      cat << EOF > ${D}/etc/systemd/system.conf.d/arka.conf
[Manager]
DefaultEnvironment=vendor_amlmp_disable_subtitle=1 iptv_streamtype=0 TSPLAYER_PIPELINE=1 TSPLAYER_LOW_MEM=1 TSPLAYER_SUBTILTEFLAG=2
EOF
   fi
}

SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'arka-launcher', 'arka.service', '', d)}"

FILES:${PN} += "${bindir} ${sysconfdir} /usr/share/fonts/ /usr/share/Arka/png /usr/share/Arka/jpg ${systemd_unitdir}/system/"
FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', '${libdir}', '', d)}"
FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', '/usr/share/zoneinfo/Asia', '', d)}"
FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '/etc/systemd/system.conf.d/* ', ' ', d)}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} = "installed-vs-shipped"
