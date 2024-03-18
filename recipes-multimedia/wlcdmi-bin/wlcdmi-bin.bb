DESCRIPTION = "aml wlcdmi library"
PN = 'wlcdmi-bin'
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libmediadrm/wlcdmi-bin')}"

S = "${WORKDIR}/git"
#SRCREV ?= "${AUTOREV}"

EXTRA_OEMAKE=" STAGING_DIR=${STAGING_DIR_TARGET} \
                 TARGET_DIR=${D} \
                 "

DEPENDS = " wayland glib-2.0 curl"
DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'playready', ' playready', '', d)}"
DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'widevine', ' aml-mediadrm-widevine', '', d)}"
DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', ' vmx-sdk-rel vmx-release-binaries vmx-plugin', '', d)}"

ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET:aarch64 = "aarch64.lp64."

def get_widevine_version(datastore):
    return datastore.getVar("WIDEVINE_VERSION", True)

def get_playready_version(datastore):
    return datastore.getVar("PLAYREADY_VERSION", True)

WIDEVINE_VER = "${@get_widevine_version(d)}"
PLAYREADY_VER = "${@get_playready_version(d)}"

do_install() {
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}${libdir}/wlcdmidrm
    install -d -m 0755 ${D}/usr/bin
    install -d -m 0755 ${D}/usr/include
    install -d -m 0755 ${D}${libdir}/pkgconfig

    install -D -m 0644 ${S}/${ARM_TARGET}/libwlcdmi.so ${D}${libdir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'playready', 'true', 'false', d)}; then
         install -D -m 0644 ${S}/${ARM_TARGET}/plugins/libplayready_wlcdmi_plugin.so.${PLAYREADY_VER} ${D}${libdir}/wlcdmidrm
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'widevine', 'true', 'false', d)}; then
         install -D -m 0644 ${S}/${ARM_TARGET}/plugins/libwidevine_wlcdmi_plugin.so.${WIDEVINE_VER} ${D}${libdir}/wlcdmidrm
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', 'true', 'false', d)}; then
         install -D -m 0644 ${S}/${ARM_TARGET}/plugins/libverimatrix_wlcdmi_plugin.so ${D}${libdir}/wlcdmidrm
    fi

    install -D -m 0755 ${S}/${ARM_TARGET}/wlcdmi_server ${D}/usr/bin
    install -D -m 0644 ${S}/noarch/include/*.h ${D}/usr/include
    install -D -m 0644 ${S}/noarch/pkgconfig/wlcdmi.pc ${D}${libdir}/pkgconfig

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d -m 0755 ${D}${systemd_system_unitdir}
        install -D -m 0644 ${S}/noarch/systemd/wlcdmi.service ${D}${systemd_system_unitdir}
    fi
}

INSANE_SKIP:${PN} = "dev-so ldflags dev-elf installed-vs-shipped already-stripped"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"

SYSTEMD_SERVICE:${PN} = "wlcdmi.service"

FILES:${PN} += "${bindir}/* ${libdir}/*.so"
FILES:${PN} += "${libdir}/wlcdmidrm {libdir}/wlcdmidrm/*"
FILES:${PN} += "${systemd_unitdir}/system/*"
FILES:${PN}-dev = "${includedir}/*"
