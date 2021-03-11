SUMMARY = "amlogic gstreamer drmbufferpool plugin"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/gstreamer_plugin.git;protocol=${AML_GIT_PROTOCOL};branch=buildroot-gstdrmbufferpool1.x"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/gst-aml-drmbufferpool1', '../')}"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base libdrm "

S = "${WORKDIR}/git/gst-aml-drmbufferpool-1.0"

EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"
inherit autotools pkgconfig distro_features_check
FILES_${PN} += "/usr/lib/gstreamer-1.0/*"
INSANE_SKIP_${PN} = "ldflags dev-so "
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
