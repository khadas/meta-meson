SUMMARY = "aml libvideorender clients"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

do_configure[noexec] = "1"
inherit autotools pkgconfig
DEPENDS += "liblog libdrm-meson wayland wayland-protocols wayland-native"
DEPENDS +="${@bb.utils.contains('DISTRO_FEATURES', 'wayland', bb.utils.contains('DISTRO_FEATURES', 'weston', '', 'westeros', d), ' ', d)}"
RDEPENDS:${PN} += "libdrm-meson liblog"
#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/libvideorender.git;protocol=${AML_GIT_PROTOCOL};branch=master"

#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git"

SOURCE_DIR = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', bb.utils.contains('DISTRO_FEATURES', 'weston', 'weston', 'westeros', d), bb.utils.contains('DISTRO_FEATURES', 'videotunnel', 'videotunnel', 'drm', d), d)}"
EXTRA_OEMAKE = " OUT_DIR=${B}/${SOURCE_DIR} CROSS=${TARGET_PREFIX} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D}"

do_compile(){
    cd ${S}/${SOURCE_DIR}
    export SRC_PATH=${S}/${SOURCE_DIR}
    export SCANNER_TOOL=${STAGING_BINDIR_NATIVE}/wayland-scanner
    oe_runmake all
}

do_install() {
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}/usr/include

    install -D -m 0644 ${S}/render_common.h ${D}/usr/include
    install -D -m 0644 ${S}/render_plugin.h ${D}/usr/include
    install -D -m 0644 ${B}/${SOURCE_DIR}/libvideorender_client.so ${D}${libdir}
}

FILES:${PN} = "${bindir}/* ${libdir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "ldflags"
