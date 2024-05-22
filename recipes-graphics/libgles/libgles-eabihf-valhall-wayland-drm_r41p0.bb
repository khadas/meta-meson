DESCRIPTION = "libGLES with wayland for 32bit Mali valhall (Drm)"

LICENSE = "Proprietary"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"

# These libraries shouldn't get installed in world builds unless something
# explicitly depends upon them.
EXCLUDE_FROM_WORLD = "1"
PROVIDES = "virtual/libgles1 virtual/libgles2 virtual/egl virtual/libgbm virtual/mesa virtual/mesa-gl virtual/nativesdk-libgl virtual/nativesdk-egl virtual/libgl-native virtual/egl-native virtual/nativesdk-libgbm virtual/libgbm-native"
RPROVIDES:${PN} += "libGLESv2.so libEGL.so libGLESv1_CM.so libMali.so"
DEPENDS += "patchelf-native libdrm wayland"

# Add wayland
RPROVIDES:${PN} += "libwayland-egl.so"

#SRC_URI = "git://${AML_GIT_ROOT}/linux/amlogic/meson_mali.git;protocol=${AML_GIT_PROTOCOL};branch=master;"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/meson_mali')}"

#SRCREV ?= "${AUTOREV}"

S = "${WORKDIR}/git"
GPU_MODEL = "valhall"
MALI_ARCH="eabihf"
MALI_ARCH:aarch64="arm64"

inherit autotools pkgconfig

do_install() {
    # install headers
    install -d -m 0755 ${D}${includedir}/EGL
    install -m 0755 ${S}/include/EGL/*.h ${D}${includedir}/EGL/
    install -d -m 0755 ${D}${includedir}/GLES
    install -m 0755 ${S}/include/GLES/*.h ${D}${includedir}/GLES/
    install -d -m 0755 ${D}${includedir}/GLES2
    install -m 0755 ${S}/include/GLES2/*.h ${D}${includedir}/GLES2/
    install -d -m 0755 ${D}${includedir}/GLES3
    install -m 0755 ${S}/include/GLES3/*.h ${D}${includedir}/GLES3/
    #install -m 0755 ${WORKDIR}/gl3ext.h ${D}${includedir}/GLES3/
    install -d -m 0755 ${D}${includedir}/KHR
    install -m 0755 ${S}/include/KHR/*.h ${D}${includedir}/KHR/
    # wayland headers
    install -m 0755 ${S}/include/EGL_platform/platform_wayland/*.h ${D}${includedir}/EGL
    # gbm headers
    install -m 0755 ${S}/include/EGL_platform/platform_wayland/gbm/gbm.h ${D}${includedir}
    install -m 0755 ${S}/include/EGL_platform/platform_wayland/weston-egl-ext.h ${D}${includedir}

    # Copy the .pc files
    install -d -m 0755 ${D}${libdir}/pkgconfig
    install -m 0644 ${S}/lib/pkgconfig/*.pc ${D}${libdir}/pkgconfig/
    # gbm.pc
    install -m 0644 ${S}/lib/pkgconfig/gbm/*.pc ${D}${libdir}/pkgconfig/

    install -d ${D}${libdir}
    install -d ${D}${includedir}
    # wayland lib
    # install -m 0755 ${S}/lib/${MALI_ARCH}/${GPU_MODEL}/${PV}/wayland/drm/libMali.so ${D}${libdir}/libMali.so

    install -m 0755 ${S}/lib/${MALI_ARCH}/${GPU_MODEL}/${PV}/wayland/drm/libMali_dmaheap.so ${D}${libdir}/libMali.so

    ln -s libMali.so ${D}${libdir}/libEGL.so.1.4.0
    ln -s libEGL.so.1.4.0 ${D}${libdir}/libEGL.so.1
    ln -s libEGL.so.1 ${D}${libdir}/libEGL.so

    ln -s libMali.so ${D}${libdir}/libGLESv1_CM.so.1.1.0
    ln -s libGLESv1_CM.so.1.1.0 ${D}${libdir}/libGLESv1_CM.so.1
    ln -s libGLESv1_CM.so.1 ${D}${libdir}/libGLESv1_CM.so

    ln -s libMali.so ${D}${libdir}/libGLESv2.so.2.1.0
    ln -s libGLESv2.so.2.1.0 ${D}${libdir}/libGLESv2.so.2
    ln -s libGLESv2.so.2 ${D}${libdir}/libGLESv2.so

    ln -s libMali.so ${D}${libdir}/libwayland-egl.so.1.0.0
    ln -s libwayland-egl.so.1.0.0 ${D}${libdir}/libwayland-egl.so.1
    ln -s libwayland-egl.so.1 ${D}${libdir}/libwayland-egl.so

    ln -s libMali.so ${D}${libdir}/libgbm.so.1.0.0
    ln -s libgbm.so.1.0.0 ${D}${libdir}/libgbm.so.1
    ln -s libgbm.so.1 ${D}${libdir}/libgbm.so

    # mkdir -p ${D}/${datadir}/vulkan/icd.d/
    # install -m 0644 ${S}/lib/${MALI_ARCH}/valhall/${PV}/mali.json ${D}${datadir}/vulkan/icd.d/
}

FILES:${PN} += "${libdir}/*.so ${datadir}"
FILES:${PN}-dev = "${includedir} ${libdir}/pkgconfig/*"
INSANE_SKIP:${PN} = "ldflags dev-so already-stripped libdir"
