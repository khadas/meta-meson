SUMMARY = "amlogic gstreamer plugin for picture sink"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base libdrm"
RDEPENDS:${PN} = " libdrm"
#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/gst_plugin_aml_pic.git;protocol=${AML_GIT_PROTOCOL};branch=master"

#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"

inherit autotools pkgconfig
FILES:${PN} += "${libdir}/gstreamer-1.0/*"
INSANE_SKIP:${PN} = "ldflags dev-so "
