SUMMARY = "amlogic gstreamer plugin for amlv4l2 video decode"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base "

RDEPENDS:${PN} = " "

LDFLAGS:append  = " -L${STAGING_LIBDIR}/gstreamer-1.0 -Wl,-rpath -Wl,/usr/lib/gstreamer-1.0 "

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/gst-plugin-aml-v4l2dec-1.0"
EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"
inherit autotools pkgconfig features_check

FILES:${PN} += "/usr/lib/gstreamer-1.0/*"
INSANE_SKIP:${PN} = "ldflags dev-so "
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
