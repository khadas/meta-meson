SUMMARY = "amlogic gstreamer plugin for audio sink"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = " gstreamer1.0 gstreamer1.0-plugins-base libdrm aml-avsync libdrm-meson linux-meson gst-plugin-aml-asink"
RDEPENDS_${PN} = " libdrm aml-avsync libdrm-meson"
#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/gst_plugin_vsink.git;protocol=${AML_GIT_PROTOCOL};branch=master"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${COREBASE}/../aml-patches/multimedia/gst-plugin-vsink/', '../')}"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git/"
EXTRA_OEMAKE = "CROSS=${TARGET_PREFIX} TARGET_DIR=${STAGING_DIR_TARGET} STAGING_DIR=${D} DESTDIR=${D}"
inherit autotools pkgconfig features_check

do_configure_append() {
  #Special patch
  if [ -n "$(basename ${STAGING_DIR_TARGET} | grep -- lib32)" ]; then
      rm -f ${STAGING_DIR_TARGET}/usr/include/linux/videodev2.h
      ln -sf ../../../../recipe-sysroot/usr/include/linux-meson/include/linux/videodev2.h  ${STAGING_DIR_TARGET}/usr/include/linux/
  else
      cp ${STAGING_DIR_TARGET}/usr/include/linux-meson/include/linux/videodev2.h ${STAGING_DIR_TARGET}/usr/include/linux/
  fi

  cp -af ${STAGING_DIR_TARGET}/usr/include/libdrm_meson/meson_drm.h ${STAGING_DIR_TARGET}/usr/include/
  cp ${STAGING_DIR_TARGET}/usr/include/libdrm_meson/drm_fourcc.h ${STAGING_DIR_TARGET}/usr/include/
}

FILES_${PN} += "${libdir}/gstreamer-1.0/*"
INSANE_SKIP_${PN} = "ldflags dev-so "
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
