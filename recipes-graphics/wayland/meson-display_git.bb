DESCRIPTION = "Meson Display"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRCREV ?= "${AUTOREV}"
#PV = "${SRCPV}"

DEPENDS += " libdrm libdrm-meson"
DEPENDS += "linux-uapi-headers"
RDEPENDS:${PN} += " libdrm-meson"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'weston', 'json-c', '', d)}"
#do_configure[noexec] = "1"
inherit autotools pkgconfig

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/meson_display;protocol=${AML_GIT_PROTOCOL};branch=master;"

#For common patches
#SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/meson_display')}"

COMPOSITOR_TYPE = "${@bb.utils.contains('DISTRO_FEATURES', 'westeros','westeros', bb.utils.contains('DISTRO_FEATURES', 'weston', 'weston', 'meson', d), d)}"

S = "${WORKDIR}/git/meson-display"

COMPOSITOR_TYPE = "${@bb.utils.contains('DISTRO_FEATURES', 'westeros','westeros', bb.utils.contains('DISTRO_FEATURES', 'weston', 'weston', 'meson', d), d)}"

#do_package_qa[noexec] = "1"

EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D} COMPOSITOR_TYPE=${COMPOSITOR_TYPE}"
FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN} += "/usr/lib/gstreamer-1.0/*"
FILES:${PN}-dev = "${includedir}/* "

INSANE_SKIP:${PN}-dev = "dev-so"
INSANE_SKIP:${PN} = "ldflags dev-so dev-deps"
