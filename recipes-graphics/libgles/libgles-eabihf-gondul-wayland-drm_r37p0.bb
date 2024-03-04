DESCRIPTION = "libGLES with wayland for Mali gondul (Drm)"

LICENSE = "CLOSED"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"

# These libraries shouldn't get installed in world builds unless something
# explicitly depends upon them.
EXCLUDE_FROM_WORLD = "1"
PROVIDES = "virtual/libgles1 virtual/libgles2 virtual/egl virtual/libgbm virtual/mesa virtual/mesa-gl"
RPROVIDES:${PN} += "libGLESv2.so libEGL.so libGLESv1_CM.so libMali.so"
DEPENDS += "patchelf-native libdrm wayland"

# Add wayland
RPROVIDES:${PN} += "libwayland-egl.so"

#SRC_URI = "git://${AML_GIT_ROOT}/linux/amlogic/meson_mali.git;protocol=${AML_GIT_PROTOCOL};branch=master;"

S = "${WORKDIR}/git"
GPU_MODEL = "gondul"
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
    install -d -m 0755 ${D}${includedir}/KHR
    install -m 0755 ${S}/include/KHR/*.h ${D}${includedir}/KHR/
    # wayland headers
    install -m 0755 ${S}/include/EGL_platform/platform_wayland/*.h ${D}${includedir}/EGL
    # gbm headers
    install -m 0755 ${S}/include/EGL_platform/platform_wayland/gbm/gbm.h ${D}${includedir}

    # Copy the .pc files
    install -d -m 0755 ${D}${libdir}/pkgconfig
    install -m 0644 ${S}/lib/pkgconfig/*.pc ${D}${libdir}/pkgconfig/
    # gbm.pc
    install -m 0644 ${S}/lib/pkgconfig/gbm/*.pc ${D}${libdir}/pkgconfig/

    install -d ${D}${libdir}
    install -d ${D}${includedir}

    #patchelf --set-soname libMali.so ${S}/lib/eabihf/${GPU_MODEL}/${PV}/wayland/drm/libMali.so
    # wayland lib
    case ${LINUXLIBCVERSION} in
    5.15%*)
        case ${MACHINE_ARCH} in
        mesong12b*)
            install -m 0755 ${S}/lib/${MALI_ARCH}/${GPU_MODEL}/${PV}/wayland/drm/libMali_dmaheap.so ${D}${libdir}/libMali.so
        ;;
        *)
            install -m 0755 ${S}/lib/${MALI_ARCH}/${GPU_MODEL}/${PV}/wayland/drm/libMali_r1p0_dmaheap.so ${D}${libdir}/libMali.so
        ;;
        esac
    ;;
    *)
        case ${MACHINE_ARCH} in
        mesong12b*)
            install -m 0755 ${S}/lib/${MALI_ARCH}/${GPU_MODEL}/${PV}/wayland/drm/libMali.so ${D}${libdir}/libMali.so
        ;;
        *)
            install -m 0755 ${S}/lib/${MALI_ARCH}/${GPU_MODEL}/${PV}/wayland/drm/libMali_r1p0.so ${D}${libdir}/libMali.so
        ;;
        esac
    ;;
    esac

    # add a link for Debian
    ln -s libMali.so ${D}${libdir}/libEGL.so.1.1.0
    ln -s libMali.so ${D}${libdir}/libwayland-egl.so.1.20.0

    # add links for yocto linux
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

    mkdir -p ${D}/${datadir}/vulkan/icd.d
    install -m 0644 ${S}/lib/${MALI_ARCH}/vulkan/${PV}/icd.d/* ${D}${datadir}/vulkan/icd.d/
    mkdir -p ${D}/${datadir}/vulkan/implicit_layer.d
    install -m 0644 ${S}/lib/${MALI_ARCH}/vulkan/${PV}/implicit_layer.d/* ${D}${datadir}/vulkan/implicit_layer.d/
}

FILES:${PN} += "${libdir}/*.so ${datadir}"
FILES:${PN}-dev = "${includedir} ${libdir}/pkgconfig/*"
INSANE_SKIP:${PN} = "ldflags dev-so already-stripped"
