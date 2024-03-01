SUMMARY = "amlogic gstreamer ts h/w demux plugin for ts container"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base aml-libdvr gst-aml-drm-plugins  aml-mediahal-sdk "

RDEPENDS_${PN} = " "

LDFLAGS_append  = " -L${STAGING_LIBDIR}/gstreamer-1.0 -Wl,-rpath -Wl,/usr/lib/gstreamer-1.0 "

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/gst-plugin-aml-demux-1.0"
EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"
inherit autotools pkgconfig features_check

FILES_${PN} += "/usr/lib/gstreamer-1.0/*"
INSANE_SKIP_${PN} = "ldflags dev-so "
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
