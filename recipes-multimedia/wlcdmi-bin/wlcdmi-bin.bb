DESCRIPTION = "aml wlcdmi library"
PN = 'wlcdmi-bin'
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libmediadrm/wlcdmi-bin')}"

S = "${WORKDIR}/git"
SRCREV ?= "${AUTOREV}"

EXTRA_OEMAKE=" STAGING_DIR=${STAGING_DIR_TARGET} \
                 TARGET_DIR=${D} \
                 "

DEPENDS = " wayland glib-2.0 curl"

ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET_aarch64 = "aarch64.lp64."

do_install() {
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}${libdir}/wlcdmidrm
    install -d -m 0755 ${D}/usr/bin
    install -d -m 0755 ${D}/usr/include
    install -d -m 0755 ${D}${libdir}/pkgconfig

    install -D -m 0644 ${S}/${ARM_TARGET}/libwlcdmi.so ${D}${libdir}
    install -D -m 0644 ${S}/${ARM_TARGET}/plugins/* ${D}${libdir}/wlcdmidrm
    install -D -m 0755 ${S}/${ARM_TARGET}/wlcdmi_server ${D}/usr/bin
    install -D -m 0644 ${S}/noarch/include/*.h ${D}/usr/include
    install -D -m 0644 ${S}/noarch/pkgconfig/wlcdmi.pc ${D}${libdir}/pkgconfig

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d -m 0755 ${D}${systemd_system_unitdir}
        install -D -m 0644 ${S}/noarch/systemd/wlcdmi.service ${D}${systemd_system_unitdir}
    fi
}

INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP_${PN}-dev = "dev-so ldflags dev-elf"

SYSTEMD_SERVICE_${PN} = "wlcdmi.service"

FILES_${PN} += "${bindir}/* ${libdir}/*.so"
FILES_${PN} += "${systemd_unitdir}/system/*"
FILES_${PN}-dev = "${includedir}/*"
