SUMMARY = "amlogic gstreamer plugin"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/gstreamer_plugin.git;protocol=${AML_GIT_PROTOCOL};branch=buildroot-gstdrmplugin1.x"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/gst-aml-drm-plugins1', '../')}"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base glib-2.0 zlib aml-secmem "
DEPENDS += "gstreamer1.0-plugins-bad"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'miraclecast', 'wfd-hdcp', '', d)}"
RDEPENDS:${PN} += " aml-secmem wfd-hdcp"

S = "${WORKDIR}/git/gst-aml-drm-plugins-1.0"

EXTRA_OECONF += "--enable-essos-rm=no"

EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"
inherit autotools pkgconfig
FILES:${PN} += "${libdir}/gstreamer-1.0/*"
INSANE_SKIP:${PN} = "ldflags dev-so "
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
