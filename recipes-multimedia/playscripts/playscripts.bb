SUMMARY  = "scripts for play"
DESCRIPTION = "Some scripts for configure audio decoders and alsa conf."
LICENSE  = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI  = "\
  file://alsactl.conf \
  file://asound.conf \
  file://asound.dev0.conf \
  file://asound.dev2.conf \
  file://property_set.sh \
  file://COPYING \
"
S = "${WORKDIR}"

ASOUND_CONF = "asound.conf"
ASOUND_CONF_u212 = "asound.conf"
ASOUND_CONF_ab311 = "asound.dev0.conf"
ASOUND_CONF_ap222 = "asound.dev2.conf"

do_install() {
    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/profile.d
    install -d ${D}${sysconfdir}/rc5.d
    install -m 0755 ${WORKDIR}/alsactl.conf ${D}${sysconfdir}/
    install -m 0755 ${WORKDIR}/${ASOUND_CONF} ${D}${sysconfdir}/asound.conf
    case ${MACHINE_ARCH} in
    "mesontm2*")
        install -m 0755 ${WORKDIR}/${ASOUND_CONF_ab311} ${D}${sysconfdir}/asound.conf
    ;;
    "mesons4_ap222")
        install -m 0755 ${WORKDIR}/${ASOUND_CONF_ap222} ${D}${sysconfdir}/asound.conf
    ;;
    esac
    install -m 0755 ${WORKDIR}/property_set.sh ${D}${sysconfdir}/profile.d/
}
