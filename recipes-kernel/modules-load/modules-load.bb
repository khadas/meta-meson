inherit module

SUMMARY = "Post process modules load"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

SRC_URI_append = " file://modules-load.sh"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}/etc
    install -m 0755 ${WORKDIR}/modules-load.sh ${D}/etc
    if ${@bb.utils.contains('DISTRO_FEATURES','amlogic-tv','true','false', d)}; then
        sed -i 's/dovi.ko/dovi_tv.ko/' ${D}/etc/modules-load.sh
    fi
}

do_install_append_t5d() {
    sed -i 's@PATH/.*/dvb_demux.ko@PATH/media/aml_hardware_dmx.ko@' ${D}/etc/modules-load.sh
}

FILES_${PN} = " /etc/modules-load.sh"
