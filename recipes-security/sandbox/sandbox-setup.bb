inherit systemd

SUMMARY = "sandbox setup"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

SRC_URI += "file://sandbox-setup-before@.service \
            file://sandbox-setup-after@.service \
            "
SRC_URI += "file://sandbox-setup.env \
            file://sandbox-setup-after.basic.sh \
            file://sandbox-setup-after.audioserver.sh \
            file://sandbox-setup-before.wpeframework.sh \
            "


do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/sandbox-setup-before@.service ${D}/${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/sandbox-setup-after@.service ${D}/${systemd_unitdir}/system
    ln -sfr ${D}/${systemd_unitdir}/system/sandbox-setup-after@.service \
        ${D}/${systemd_unitdir}/system/sandbox-setup-after@basic.service
    ln -sfr ${D}/${systemd_unitdir}/system/sandbox-setup-after@.service \
        ${D}/${systemd_unitdir}/system/sandbox-setup-after@audioserver.service
    ln -sfr ${D}/${systemd_unitdir}/system/sandbox-setup-before@.service \
        ${D}/${systemd_unitdir}/system/sandbox-setup-before@wpeframework.service

    mkdir -p ${D}${bindir}
    install -m 0644 ${WORKDIR}/sandbox-setup.env ${D}/${bindir}
    install -m 0755 ${WORKDIR}/sandbox-setup-after.basic.sh ${D}/${bindir}
    install -m 0755 ${WORKDIR}/sandbox-setup-after.audioserver.sh ${D}/${bindir}
    install -m 0755 ${WORKDIR}/sandbox-setup-before.wpeframework.sh ${D}/${bindir}
}

do_install:append:t5d() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', 'true', 'false', d)}; then
        cat >> ${D}/${bindir}/sandbox-setup-before.wpeframework.sh <<EOF
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
    cat >> ${D}/${bindir}/sandbox-setup-before.wpeframework.sh <<EOF
echo codec_mm.default_tvp_pool_size_0=130023424 > /sys/class/codec_mm/config
echo codec_mm.default_tvp_pool_size_1=75497472 > /sys/class/codec_mm/config
echo codec_mm.default_tvp_pool_size_2=50331648 > /sys/class/codec_mm/config
EOF
}

FILES:${PN} += "${bindir}/*"
FILES:${PN} += "${systemd_unitdir}/system/*"

SYSTEMD_SERVICE:${PN} += "sandbox-setup-before@.service \
                          sandbox-setup-after@.service \
                          sandbox-setup-after@basic.service \
                          sandbox-setup-after@audioserver.service  \
                          sandbox-setup-before@wpeframework.service\
"
