SUMMARY = "amlogic gstreamer plugin"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base glib-2.0 zlib aml-audio-service "
RDEPENDS:${PN} = " aml-audio-service"
#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/gstreamer_plugin.git;protocol=${AML_GIT_PROTOCOL};branch=buildroot-gstplugin1.x"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/gst-aml-plugins1/', '../')}"

#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/gst-aml-plugins-1.0"

EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"
inherit autotools pkgconfig
FILES:${PN} += "${libdir}/gstreamer-1.0/*"
INSANE_SKIP:${PN} = "ldflags dev-so "
