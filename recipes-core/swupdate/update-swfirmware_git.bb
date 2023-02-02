SUMMARY = "update sw firmware"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

SRC_URI += "file://update_swfirmware.sh"

SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "swupdate-download", \
            "file://backup_info.sh file://swupdate-url.sh \
            file://apply_info.sh file://start_wifi.sh", "", d)}"

SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "swupdate-dvb-ota", \
            "file://swupdate-dvb.sh", "", d)}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"

do_install() {
    mkdir -p ${D}${bindir}
    install -m 0755 ${WORKDIR}/update_swfirmware.sh ${D}/${bindir}

    if ${@bb.utils.contains("DISTRO_FEATURES", "swupdate-download", "true", "false", d)}; then
        mkdir -p ${D}/etc/swupdate
        install -m 0755 ${WORKDIR}/backup_info.sh ${D}/etc/swupdate
        install -m 0755 ${WORKDIR}/swupdate-url.sh ${D}/etc/swupdate
        install -m 0755 ${WORKDIR}/apply_info.sh ${D}/etc/swupdate
        install -m 0755 ${WORKDIR}/start_wifi.sh ${D}/etc/swupdate
    fi

    if ${@bb.utils.contains("DISTRO_FEATURES", "swupdate-dvb-ota", "true", "false", d)}; then
        mkdir -p ${D}/etc/swupdate
        install -m 0755 ${WORKDIR}/swupdate-dvb.sh ${D}/etc/swupdate
    fi
}

FILES_${PN} += "${bindir}/*"
FILES_${PN} += "@bb.utils.contains("DISTRO_FEATURES", "swupdate-download", "/etc/swupdate/*", "", d)"
FILES_${PN} += "@bb.utils.contains("DISTRO_FEATURES", "swupdate-dvb-ota", "/etc/swupdate/*", "", d)"
