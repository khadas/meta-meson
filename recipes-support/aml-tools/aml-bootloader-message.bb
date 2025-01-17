inherit systemd

SUMMARY = "aml bootloader_message"
LICENSE = "CLOSED"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_commonlib;protocol=${AML_GIT_PROTOCOL};branch=master;"
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "absystem", "file://success-boot.service", "", d)}"

DEPENDS += "zlib"

#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/bootloader_message"

do_configure[noexec] = "1"
EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D}"

CFLAGS += " -DBOOTCTOL_AVB "

do_compile(){
    export CFLAGS="${CFLAGS}"
    ${MAKE} -C ${S} ${EXTRA_OEMAKE} all
}

do_install() {
    install -d ${D}${includedir}
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -m 0644 ${S}/bootloader_message.h ${D}${includedir}

    install -m 0644 ${B}/libbootloader_message.a ${D}${libdir}
    install -m 0755 ${B}/urlmisc ${D}${bindir}
    install -m 0755 ${B}/bootloader_slot ${D}${bindir}

    if ${@bb.utils.contains("DISTRO_FEATURES", "absystem", 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/success-boot.service ${D}/${systemd_unitdir}/system
    fi
}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN} += "${@bb.utils.contains("DISTRO_FEATURES", "absystem", "${systemd_unitdir}/system/*", "", d)}"
FILES:${PN}-dev = "${includedir}/* "

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains("DISTRO_FEATURES", "absystem", "success-boot.service", "", d)}"
