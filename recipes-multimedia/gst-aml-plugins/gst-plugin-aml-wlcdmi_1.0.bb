
DESCRIPTION = "aml gst-plugin-aml-wlcdmi"
PN = 'gst-plugin-aml-wlcdmi'
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/gst-plugin-aml-wlcdmi')}"

#inherit externalsrc
#EXTERNALSRC = "${TOPDIR}/../aml-comp/multimedia/gst-plugin-aml-wlcdmi"
#EXTERNALSRC_BUILD = "${WORKDIR}/build"

inherit meson pkgconfig

#SRCREV ?= "${AUTOREV}"
#PV = "${SRCPV}"
S = "${WORKDIR}"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base curl wlcdmi-bin gst-aml-drm-plugins "

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
