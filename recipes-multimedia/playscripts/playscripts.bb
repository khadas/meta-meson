SUMMARY  = "scripts for play"
DESCRIPTION = "Some scripts for configure audio decoders."
LICENSE  = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI  = "\
  file://property_set.sh \
  file://COPYING \
"
S = "${WORKDIR}"

ASOUND_CONF = "asound.conf"

do_install() {
    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/profile.d
    install -d ${D}${sysconfdir}/rc5.d
    install -m 0755 ${WORKDIR}/property_set.sh ${D}${sysconfdir}/profile.d/

    if ${@bb.utils.contains("DISTRO_FEATURES", "irdeto", "true", "false", d)}
    then
        sed -i '$a\export XDG_RUNTIME_DIR=\/run' ${D}${sysconfdir}/profile.d/property_set.sh
    fi

    if ${@bb.utils.contains("DISTRO_FEATURES", "zapper-2k", "true", "false", d)}
    then
        cat << EOF >> ${D}${sysconfdir}/profile.d/property_set.sh
## adjust dmx filter number from 64 to 16 to reduce memory use
echo adjust 64 16 > /sys/class/dmx/cache_status
#reduce pes buf size for subtitle for low memory requirement
echo 131072 > /sys/module/amlogic_dvb_demux/parameters/pes_buf_size

echo 0 > /sys/module/amvdec_mmpeg12/parameters/dynamic_buf_num_margin
echo 1 > /sys/module/amvdec_mh264/parameters/reference_buf_margin
echo 2 > /sys/module/amvdec_mh264/parameters/reorder_dpb_size_margin
echo 1 > /sys/module/amvdec_mh264/parameters/save_buffer
echo 0x1 > /sys/module/amvdec_h265/parameters/dynamic_buf_num_margin

echo codec_mm.scatter.keep_size_PAGE=0 > /sys/class/codec_mm/config

echo schedutil > /sys/devices/system/cpu/cpufreq/policy0/scaling_governor
EOF
    fi
}

