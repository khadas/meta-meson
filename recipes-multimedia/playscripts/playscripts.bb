SUMMARY  = "scripts for play"
DESCRIPTION = "Some scripts for configure audio decoders and alsa conf."
LICENSE  = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI  = "\
  file://alsactl.conf \
  file://asound.conf \
  file://asound.dev0.conf \
  file://property_set.sh \
  file://COPYING \
"
S = "${WORKDIR}"

ASOUND_CONF = "asound.conf"
ASOUND_CONF:g12a = "asound.conf"
ASOUND_CONF:tm2 = "asound.dev0.conf"

do_install() {
    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/profile.d
    install -d ${D}${sysconfdir}/rc5.d
    install -m 0644 ${WORKDIR}/alsactl.conf ${D}${sysconfdir}/
    install -m 0644 ${WORKDIR}/${ASOUND_CONF} ${D}${sysconfdir}/asound.conf
    install -m 0755 ${WORKDIR}/property_set.sh ${D}${sysconfdir}/profile.d/

    if ${@bb.utils.contains("DISTRO_FEATURES", "irdeto", "true", "false", d)}
    then
        sed -i '$a\export XDG_RUNTIME_DIR=\/run' ${D}${sysconfdir}/profile.d/property_set.sh
    fi

    if ${@bb.utils.contains("DISTRO_FEATURES", "zapper", "true", "false", d)}
    then
        cat << EOF >> ${D}${sysconfdir}/profile.d/property_set.sh
echo 0 > /sys/module/amvdec_mmpeg12/parameters/dynamic_buf_num_margin
echo 1 > /sys/module/amvdec_mh264/parameters/reference_buf_margin
echo 4 > /sys/module/amvdec_mh264/parameters/reorder_dpb_size_margin
echo codec_mm.scatter.keep_size_PAGE=0 > /sys/class/codec_mm/config
EOF
    fi
}

