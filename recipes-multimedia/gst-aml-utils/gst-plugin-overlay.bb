SUMMARY = "aml gst plugs draw rect and text overlay to video layer"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base"
DEPENDS += " gst-aml-dma-allocator gst-aml-gfx2d "
RDEPENDS:${PN} += " dma-allocator gst-aml-dma-allocator gst-aml-gfx2d libge2d "

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${COREBASE}/../aml-patches/multimedia/gst-plugin-overlay/', '../')}"

#SRCREV ?= "${AUTOREV}"

#S = "${WORKDIR}/git/"

DEPENDS += "${@bb.utils.contains('EXTRA_OECONF', ' ', ' ', ' ', d)}"
RDEPENDS:${PN} += "${@bb.utils.contains('EXTRA_OECONF', ' ', ' ', ' ', d)}"

EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D} CROSS=${TARGET_PREFIX}"

inherit autotools pkgconfig

do_install:append() {
    #remove unused temp files
    rm -rf ${S}/INSTALL
    rm -rf ${S}/Makefile.in
    rm -rf ${S}/aclocal.m4
    rm -rf ${S}/autoscan.log
    rm -rf ${S}/autom4te.cache/
    rm -rf ${S}/COPYING
    rm -rf ${S}/m4
    rm -rf ${S}/compile
    rm -rf ${S}/config.guess
    rm -rf ${S}/config.h.in
    rm -rf ${S}/config.h.in~
    rm -rf ${S}/config.log
    rm -rf ${S}/config.sub
    rm -rf ${S}/configure
    rm -rf ${S}/configure.scan
    rm -rf ${S}/depcomp
    rm -rf ${S}/install-sh
    rm -rf ${S}/ltmain.sh
    rm -rf ${S}/missing
    rm -rf ${S}/oe-logs
    rm -rf ${S}/oe-workdir
    rm -rf ${S}/src/Makefile.in
    rm -rf ${S}/src/*/Makefile.in
}

FILES:${PN} += "${libdir}/gstreamer-1.0/*"
INSANE_SKIP:${PN} = "ldflags dev-so "
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
