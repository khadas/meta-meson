SUMMARY = "amlogic gstreamer plugin for video sink"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base gst-aml-drmbufferpool-plugins gst-plugin-aml-asink linux-meson aml-mediahal-sdk"
RDEPENDS:${PN} = " aml-mediahal-sdk "

# LDFLAGS:append  = " -lgstamlhalasink -lmediahal_videorender -lgstvideo-1.0 -lgstdrmbufferpool -lgstdrmallocator -L${STAGING_LIBDIR}/gstreamer-1.0 -Wl,-rpath -Wl,/usr/lib/gstreamer-1.0"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/gst-plugin-video-sink-1.0"
EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"
inherit autotools pkgconfig

do_configure:append() {
  cd ${S}
  bash version_config.sh
}

FILES:${PN} += "/usr/lib/gstreamer-1.0/*"
INSANE_SKIP:${PN} = "ldflags dev-so "
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
