SUMMARY = "amlogic gstreamer plugin for audio sink"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base aml-audio-service aml-avsync"
RDEPENDS_${PN} = " aml-audio-service aml-avsync"
#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/gst_plugin_asink.git;protocol=${AML_GIT_PROTOCOL};branch=master"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/gst-plugin-asink/', '../')}"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/"

EXTRA_OECONF += "--enable-xrun-detection=yes"
EXTRA_OECONF += "--enable-ms12=yes"
EXTRA_OECONF += "--enable-dts=yes"
EXTRA_OECONF += "--enable-mediasync=no"

DEPENDS += "${@bb.utils.contains('EXTRA_OECONF', '--enable-mediasync=yes', 'aml-mediahal-sdk', '', d)}"
RDEPENDS_${PN} += "${@bb.utils.contains('EXTRA_OECONF', '--enable-mediasync=yes', 'aml-mediahal-sdk', '', d)}"

EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"
inherit autotools pkgconfig features_check
FILES_${PN} += "${libdir}/gstreamer-1.0/*"
INSANE_SKIP_${PN} = "ldflags dev-so "
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
