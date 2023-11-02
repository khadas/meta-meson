inherit systemd

SUMMARY = "system config"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

SRC_URI += "file://system-config.service"
SRC_URI += "file://system-config.sh"


do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/system-config.service ${D}/${systemd_unitdir}/system

    mkdir -p ${D}${bindir}
    install -m 0755 ${WORKDIR}/system-config.sh ${D}/${bindir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'true', 'false', d)}; then
        cat >> ${D}/${bindir}/system-config.sh <<EOF
echo 0x222 > /sys/class/hdmirx/hdmirx0/edid_select
echo reset0 > /sys/class/hdmirx/hdmirx0/debug
EOF
    fi
}

do_install:append:t5d() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', 'true', 'false', d)}; then
        cat >> ${D}/${bindir}/system-config.sh <<EOF
# Set tvp to 3 phase 44, 24, 16
echo 1 > /sys/module/amvdec_h265_v4l/parameters/mv_buf_dynamic_alloc
echo 1 > /sys/module/amvdec_vp9_v4l/parameters/mv_buf_dynamic_alloc
echo 1 > /sys/module/amvdec_av1_v4l/parameters/mv_buf_dynamic_alloc
echo codec_mm.default_tvp_pool_size_0=58720256 > /sys/class/codec_mm/config
echo codec_mm.default_tvp_pool_size_1=23068672 > /sys/class/codec_mm/config
#echo codec_mm.default_tvp_pool_size_2=16777216 > /sys/class/codec_mm/config
echo "codec_mm.default_tvp_4k_size=81788928" > /sys/class/codec_mm/config
echo codec_mm.default_tvp_size=81788928 > /sys/class/codec_mm/config
EOF
    fi
}

do_install:append:t5w() {
    cat >> ${D}/${bindir}/system-config.sh <<EOF
echo codec_mm.default_tvp_pool_size_0=130023424 > /sys/class/codec_mm/config
echo codec_mm.default_tvp_pool_size_1=75497472 > /sys/class/codec_mm/config
echo codec_mm.default_tvp_pool_size_2=50331648 > /sys/class/codec_mm/config
EOF
}

FILES:${PN} += "${bindir}/*"
FILES:${PN} += "${systemd_unitdir}/system/*"

SYSTEMD_SERVICE:${PN} += "system-config.service"
