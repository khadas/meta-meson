SUMMARY = "Weston, a Wayland compositor"
DESCRIPTION = "Weston is the reference implementation of a Wayland compositor"
HOMEPAGE = "http://wayland.freedesktop.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d79ee9e66bb0f95d3386a7acae780b70 \
                    file://libweston/compositor.c;endline=27;md5=eb6d5297798cabe2ddc65e2af519bcf0"

SRC_URI = "https://wayland.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
           file://weston.png \
           file://weston.desktop \
           file://xwayland.weston-start \
           file://0001-10.0.0-weston-launch-Provide-a-default-version-that-doesn-t.patch \
           file://0002-8.0.0-Enable-Connector-by-priority.patch \
           file://0003-10.0.0-punch-video-hole-for-meson-dri-video-dmabuff-on-vide.patch \
           file://0004-8.0.0-don-t-touch-the-occluded-region-when-glrenderer.patch \
           file://0005-10.0.0-Fix-the-UI-size-for-enable-1080p-on-4k-display.patch \
           file://0006-10.0.0-video-plane-src-axis-should-match-the-real.patch \
           file://0007-cursor-hide-cursor-be-for-have-point-motion-after-we.patch \
           file://0017-10.0.0-add-video-fence-check-dpk.patch \
           file://0019-9.0.0-read_display_mode_from_drm_output.patch \
           file://0020-10.0.0-define_EGL_DRM_RENDER_NODE_FILE_EXT.patch \
           file://0021-10.0.0-MOD_INVALID-to-MOD_LINEAR-for-low-dmabuf-version.patch \
           file://0022-10.0.0-revert_gl-renderer_Dont_require_buffer_age_when_using_partial_update.patch \
           file://0023-10.0.0-add-z-order-force-and-background-transparent-options.patch \
           file://0024-10.0.0-modify-keyboard-focus-change-flow.patch \
           file://0025-10.0.0-play-video-in-video-layer.patch \
           file://0026-10.0.0-add-drm-help-functions.patch \
           file://0027-10.0.0-add-debug-tool.patch \
           file://0028-10.0.0-add-pts-check-flow.patch \
           file://0029-10.0.0-optimize-video-frame-drop-issue.patch \
           file://0030-10.0.0-modify-connector-change-flow.patch \
           "

SRC_URI[md5sum] = "bc4abe2ee6904a4890a0c641c6257f91"
SRC_URI[sha256sum] = "5c23964112b90238bed39e5dd1e41cd71a79398813cdc3bbb15a9fdc94e547ae"

UPSTREAM_CHECK_URI = "https://wayland.freedesktop.org/releases.html"

inherit meson pkgconfig useradd features_check
# depends on virtual/egl
REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "libxkbcommon gdk-pixbuf pixman cairo glib-2.0"
DEPENDS += "wayland wayland-protocols libinput virtual/egl pango wayland-native meson-display"

WESTON_MAJOR_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:1])}"

EXTRA_OEMESON += "-Dbackend-rdp=false -Dpipewire=false -DWESTON_MAX_OUTPUT_PIPLINE=1 -DWESTON_USE_DEFAULT_Z_ORDER=true  -DWESTON_FORCE_BACKGROUND_TRANSPARENT=true"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'kms wayland egl clients', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 wayland', 'xwayland', '', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'pam systemd x11', d)} \
                   ${@bb.utils.contains_any('DISTRO_FEATURES', 'wayland x11', '', 'headless', d)} \
                   launch \
                   image-jpeg \
                   screenshare \
                   shell-desktop \
                   shell-fullscreen \
                   shell-ivi"

#
# Compositor choices
#
# Weston on KMS
PACKAGECONFIG[kms] = "-Dbackend-drm=true,-Dbackend-drm=false,drm udev virtual/egl virtual/libgles2 virtual/libgbm mtdev"
# Weston on Wayland (nested Weston)
PACKAGECONFIG[wayland] = "-Dbackend-wayland=true,-Dbackend-wayland=false,virtual/egl virtual/libgles2"
# Weston on X11
PACKAGECONFIG[x11] = "-Dbackend-x11=true,-Dbackend-x11=false,virtual/libx11 libxcb libxcb libxcursor cairo"
# Headless Weston
PACKAGECONFIG[headless] = "-Dbackend-headless=true,-Dbackend-headless=false"
# Weston on framebuffer
PACKAGECONFIG[fbdev] = "-Ddeprecated-backend-fbdev=true,-Ddeprecated-backend-fbdev=false,udev mtdev"
# weston-launch
PACKAGECONFIG[launch] = "-Ddeprecated-weston-launch=true,-Ddeprecated-weston-launch=false,drm"
# VA-API desktop recorder
PACKAGECONFIG[vaapi] = "-Dbackend-drm-screencast-vaapi=true,-Dbackend-drm-screencast-vaapi=false,libva"
# Weston with EGL support
PACKAGECONFIG[egl] = "-Drenderer-gl=true,-Drenderer-gl=false,virtual/egl"
# Weston with lcms support
PACKAGECONFIG[lcms] = "-Dcolor-management-lcms=true,-Dcolor-management-lcms=false,lcms"
# Weston with webp support
PACKAGECONFIG[webp] = "-Dimage-webp=true,-Dimage-webp=false,libwebp"
# Weston with systemd-login support
PACKAGECONFIG[systemd] = "-Dsystemd=true -Dlauncher-logind=true,-Dsystemd=false -Dlauncher-logind=false,systemd dbus"
# Weston with Xwayland support (requires X11 and Wayland)
PACKAGECONFIG[xwayland] = "-Dxwayland=true,-Dxwayland=false"
# colord CMS support
PACKAGECONFIG[colord] = "-Dcolor-management-colord=true,-Dcolor-management-colord=false,colord"
# Clients support
#PACKAGECONFIG[clients] = "-Dsimple-clients=all -Ddemo-clients=true,-Dsimple-clients= -Ddemo-clients=false"
# Virtual remote output with GStreamer on DRM backend
PACKAGECONFIG[remoting] = "-Dremoting=true,-Dremoting=false,gstreamer-1.0"
# Weston with PAM support
PACKAGECONFIG[pam] = "-Dpam=true,-Dpam=false,libpam"
# Weston with screen-share support
PACKAGECONFIG[screenshare] = "-Dscreenshare=true,-Dscreenshare=false"
# Traditional desktop shell
PACKAGECONFIG[shell-desktop] = "-Dshell-desktop=true,-Dshell-desktop=false"
# Fullscreen shell
PACKAGECONFIG[shell-fullscreen] = "-Dshell-fullscreen=true,-Dshell-fullscreen=false"
# In-Vehicle Infotainment (IVI) shell
PACKAGECONFIG[shell-ivi] = "-Dshell-ivi=true,-Dshell-ivi=false"
# JPEG image loading support
PACKAGECONFIG[image-jpeg] = "-Dimage-jpeg=true,-Dimage-jpeg=false, jpeg"
# support libseat based launch
PACKAGECONFIG[launcher-libseat] = "-Dlauncher-libseat=true,-Dlauncher-libseat=false,seatd"

# disbale 'dmabuf-feedback'
EXTRA_OEMESON +="-Dsimple-clients=damage,im,egl,shm,touch,dmabuf-v4l,dmabuf-egl"

do_install:append() {
	# Weston doesn't need the .la files to load modules, so wipe them
	rm -f ${D}/${libdir}/libweston-${WESTON_MAJOR_VERSION}/*.la

	# If X11, ship a desktop file to launch it
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}" ]; then
		install -d ${D}${datadir}/applications
		install ${WORKDIR}/weston.desktop ${D}${datadir}/applications

		install -d ${D}${datadir}/icons/hicolor/48x48/apps
		install ${WORKDIR}/weston.png ${D}${datadir}/icons/hicolor/48x48/apps
	fi

	if [ "${@bb.utils.contains('PACKAGECONFIG', 'xwayland', 'yes', 'no', d)}" = "yes" ]; then
		install -Dm 644 ${WORKDIR}/xwayland.weston-start ${D}${datadir}/weston-start/xwayland
	fi

	if [ "${@bb.utils.contains('PACKAGECONFIG', 'launch', 'yes', 'no', d)}" = "yes" ]; then
		chmod u+s ${D}${bindir}/weston-launch
	fi
}

PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'xwayland', '${PN}-xwayland', '', d)} \
             libweston-${WESTON_MAJOR_VERSION} ${PN}-examples"

FILES:${PN}-dev += "${libdir}/${BPN}/libexec_weston.so"
FILES:${PN} = "${bindir}/weston ${bindir}/weston-terminal ${bindir}/weston-info ${bindir}/weston-launch ${bindir}/wcap-decode ${libexecdir} ${libdir}/${BPN}/*.so* ${datadir}"

FILES:libweston-${WESTON_MAJOR_VERSION} = "${libdir}/lib*${SOLIBS} ${libdir}/libweston-${WESTON_MAJOR_VERSION}/*.so"
SUMMARY:libweston-${WESTON_MAJOR_VERSION} = "Helper library for implementing 'wayland window managers'."

FILES:${PN}-examples = "${bindir}/*"

FILES:${PN}-xwayland = "${libdir}/libweston-${WESTON_MAJOR_VERSION}/xwayland.so"
RDEPENDS:${PN}-xwayland += "xserver-xorg-xwayland"

RDEPENDS:${PN} += "xkeyboard-config"
RRECOMMENDS:${PN} = "weston-init liberation-fonts"
RRECOMMENDS:${PN}-dev += "wayland-protocols"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system weston-launch"
