SUMMARY = "amlogic gstreamer ts h/w demux plugin for ts container"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base aml-libdvr gst-aml-drm-plugins  aml-mediahal-sdk "

RDEPENDS:${PN} = " aml-mediahal-sdk aml-libdvr"

LDFLAGS:append  = " -L${STAGING_LIBDIR}/gstreamer-1.0 -Wl,-rpath -Wl,/usr/lib/gstreamer-1.0 "

#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/gst-plugin-aml-demux-1.0"

EXTRA_OECONF += "--enable-hwdemux=yes"
EXTRA_OECONF += "--enable-amlqtdemux=yes"

EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"
inherit autotools pkgconfig

FILES:${PN} += "/usr/lib/gstreamer-1.0/*"
INSANE_SKIP:${PN} = "ldflags dev-so "
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
