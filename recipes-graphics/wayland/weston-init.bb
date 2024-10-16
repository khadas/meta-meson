SUMMARY = "Startup script and systemd unit file for the Weston Wayland compositor"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://init \
           file://weston.service \
           file://weston-start \
           file://aml-weston.ini \
           file://aml-weston-hdmi.ini"

S = "${WORKDIR}"

do_install() {
  install -Dm755 ${WORKDIR}/init ${D}/${sysconfdir}/init.d/weston
  install -Dm644 ${WORKDIR}/aml-weston.ini ${D}/${sysconfdir}/
  install -Dm644 ${WORKDIR}/aml-weston-hdmi.ini ${D}/${sysconfdir}/
  install -Dm0644 ${WORKDIR}/weston.service ${D}${systemd_system_unitdir}/weston.service

# Install weston-start script
  install -Dm755 ${WORKDIR}/weston-start ${D}${bindir}/weston-start
  sed -i 's,@DATADIR@,${datadir},g' ${D}${bindir}/weston-start
  sed -i 's,@LOCALSTATEDIR@,${localstatedir},g' ${D}${bindir}/weston-start
  if ${@bb.utils.contains('DISTRO_FEATURES', 'UI_720P', 'true', 'false', d)};then
    sed -i '/^ui-size/ s/.*/ui-size=1280x720/g' ${D}${sysconfdir}/aml-weston.ini
    sed -i '/^ui-size/ s/.*/ui-size=1280x720/g' ${D}${sysconfdir}/aml-weston-hdmi.ini
  fi  
}

inherit allarch update-rc.d features_check systemd

# rdepends on weston which depends on virtual/egl
REQUIRED_DISTRO_FEATURES = "opengl"

RDEPENDS:${PN} = "weston kbd"

INITSCRIPT_NAME = "weston"
INITSCRIPT_PARAMS = "start 9 5 2 . stop 20 0 1 6 ."

SYSTEMD_SERVICE:${PN} = "weston.service"
