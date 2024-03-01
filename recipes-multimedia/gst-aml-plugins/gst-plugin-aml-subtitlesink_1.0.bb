SUMMARY = "amlogic gstreamer plugin for subtitle sink"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base aml-subtitleserver"
#RDEPENDS:${PN} = " "
#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/gst_plugin_aml_subtitlesink.git;protocol=${AML_GIT_PROTOCOL};branch=master"


SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/"
EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"
inherit autotools pkgconfig features_check

FILES:${PN} += "${libdir}/gstreamer-1.0/*"
INSANE_SKIP:${PN} = "ldflags dev-so "
