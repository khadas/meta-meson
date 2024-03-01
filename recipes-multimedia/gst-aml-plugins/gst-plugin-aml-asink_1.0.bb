SUMMARY = "amlogic gstreamer plugin for audio sink"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base aml-audio-service aml-avsync"
RDEPENDS:${PN} = " aml-audio-service aml-avsync"
#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/gst_plugin_asink.git;protocol=${AML_GIT_PROTOCOL};branch=master"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/gst-plugin-asink/', '../')}"

#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git"

EXTRA_OECONF += "--enable-ms12=yes"
EXTRA_OECONF += "--enable-dts=yes"
EXTRA_OECONF += "--enable-mediasync=no"

DEPENDS += "${@bb.utils.contains('EXTRA_OECONF', '--enable-mediasync=yes', 'aml-mediahal-sdk', '', d)}"
RDEPENDS:${PN} += "${@bb.utils.contains('EXTRA_OECONF', '--enable-mediasync=yes', 'aml-mediahal-sdk', '', d)}"

EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"
inherit autotools pkgconfig

do_configure:append() {
  cd ${S}
  bash version_config.sh
}

FILES:${PN} += "${libdir}/gstreamer-1.0/*"
INSANE_SKIP:${PN} = "ldflags dev-so "
