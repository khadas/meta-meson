DESCRIPTION = "Meson Display"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRCREV ?= "${AUTOREV}"
#PV = "${SRCPV}"

DEPENDS += "libdrm udev"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/libdrm_amlogic;protocol=${AML_GIT_PROTOCOL};branch=master;"

#For common patches
#SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/libdrm_amlogic')}"

S = "${WORKDIR}/git/meson"

inherit autotools pkgconfig
do_configure[noexec] = "1"
#do_package_qa[noexec] = "1"

EXTRA_OEMAKE = "STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D}"

do_compile() {
  cd ${S}
  cp -af ../meson_drm.h ${STAGING_DIR_TARGET}${includedir}
  cp -af ../drm_fourcc.h ${STAGING_DIR_TARGET}${includedir}
  cd ../
  oe_runmake  all
}

do_install() {
  cd ${S}
  install -d ${D}${libdir}
  install -d ${D}${bindir}
  install -d ${D}${includedir}/libdrm_meson
  install -m 0644 ${S}/libdrm_meson.so ${D}${libdir}
  install -m 755  ${S}/meson_drm_test ${D}${bindir}
  install -m 755  ${S}/meson_drm_display_test ${D}${bindir}
  install -m 755  ${S}/../drm-setcrtc/drm_setcrtc ${D}${bindir}
  install -m 0644 ${S}/meson_drmif.h ${D}${includedir}
  install -m 0644 ${S}/meson_drm_util.h ${D}${includedir}
  install -m 0644 ${S}/../meson_drm.h ${D}${includedir}/libdrm_meson
  install -m 0644 ${S}/../drm_fourcc.h ${D}${includedir}/libdrm_meson
  install -m 0644 ${S}/libdrm_meson_connector.h ${D}${includedir}/libdrm_meson
  install -m 0644 ${S}/libdrm_meson_property.h ${D}${includedir}/libdrm_meson
  install -m 0644 ${S}/meson_drm_display.h ${D}${includedir}/libdrm_meson
  install -m 0644 ${S}/meson_drm_event.h ${D}${includedir}/libdrm_meson
  install -m 0644 ${S}/meson_drm_settings.h ${D}${includedir}/libdrm_meson
}

FILES_${PN} = "${libdir}/*  ${bindir}/"
FILES_${PN}-dev = "${includedir}/*"
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP_${PN}-dev = "dev-so ldflags dev-elf"

