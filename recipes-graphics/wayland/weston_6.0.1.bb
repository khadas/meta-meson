SUMMARY = "Weston, a Wayland compositor"
DESCRIPTION = "Weston is the reference implementation of a Wayland compositor"
HOMEPAGE = "http://wayland.freedesktop.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d79ee9e66bb0f95d3386a7acae780b70 \
                    file://libweston/compositor.c;endline=27;md5=6c53bbbd99273f4f7c4affa855c33c0a"

SRC_URI = "https://wayland.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
           file://weston.png \
           file://weston.desktop \
           file://xwayland.weston-start \
           file://0001-os-compatibility-define-CLOCK_BOOTTIME-when-not-avai.patch \
           file://0003-compositor-fbdev.patch \
           file://0004-punch-video-hole.patch \
           file://0005-Fix-the-UI-size-for-enable-1080p-on-4k-display.patch \
           file://0006-Add-the-drm-help-funcitons-for-dynamic-mode-set-get-.patch \
           file://0007-cursor-hide-cursor-be-for-have-point-motion-after-we.patch \
           file://0008-Enable-Connector-by-priority.patch \
           file://0009-Fix-display-hotplugging-unplugging-crash.patch \
           file://0010-desktop-shell-Don-t-re-position-views-when-output_li.patch \
           file://0011-add-hotplug-pending-timer-process.patch \
           file://0012-Add-interlaced-display-mode-support.patch \
           file://0013-optimze-the-display-mode-change-behavior-make-displa.patch \
           file://0014-Add-force_refresh-api-and-register-each-weston_outpu.patch \
           file://0015-Fix-weston-launcher-inputevent-fd-leak-issue.patch \
           file://0016-optimze-hotplug-pending-process.patch \
"
#           file://0001-make-error-portable.patch
#           file://0001-weston-launch-Provide-a-default-version-that-doesn-t.patch
SRC_URI[md5sum] = "e7b10710ef1eac82258f97bfd41fe534"
SRC_URI[sha256sum] = "bf2f6d5aae2e11cabb6bd69a76bcf9edb084f8c3e14ca769bea7234a513155b4"

UPSTREAM_CHECK_URI = "https://wayland.freedesktop.org/releases.html"

inherit autotools pkgconfig useradd features_check
# depends on virtual/egl
REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "libxkbcommon gdk-pixbuf pixman cairo glib-2.0 jpeg"
DEPENDS += "wayland wayland-protocols libinput virtual/egl pango wayland-native"

WESTON_MAJOR_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:1])}"

EXTRA_OECONF = "--enable-setuid-install \
                --disable-rdp-compositor \
                --enable-autotools \
                "

DEPENDS += "meson-display"
RDEPENDS:${PN} += "meson-display"
EXTRA_OECONF += "--enable-drm-helper --enable-output-dynamic-switch WESTON_MAX_OUTPUT_PIPLINE=1"


#EXTRA_OECONF += "--enable-drm-fix-ui-size"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'kms fbdev wayland egl', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 wayland', 'xwayland', '', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'pam systemd x11', d)} \
                   clients launch"
#
# Compositor choices
#
# Weston on KMS
PACKAGECONFIG[kms] = "--enable-drm-compositor,--disable-drm-compositor,drm udev virtual/mesa virtual/libgbm mtdev"
# Weston on Wayland (nested Weston)
PACKAGECONFIG[wayland] = "--enable-wayland-compositor,--disable-wayland-compositor,virtual/mesa"
# Weston on X11
PACKAGECONFIG[x11] = "--enable-x11-compositor,--disable-x11-compositor,virtual/libx11 libxcb libxcb libxcursor cairo"
# Headless Weston
PACKAGECONFIG[headless] = "--enable-headless-compositor,--disable-headless-compositor"
# Weston on framebuffer
PACKAGECONFIG[fbdev] = "--enable-fbdev-compositor,--disable-fbdev-compositor,udev mtdev"
# weston-launch
PACKAGECONFIG[launch] = "--enable-weston-launch,--disable-weston-launch,drm"
# VA-API desktop recorder
PACKAGECONFIG[vaapi] = "--enable-vaapi-recorder,--disable-vaapi-recorder,libva"
# Weston with EGL support
PACKAGECONFIG[egl] = "--enable-egl --enable-simple-egl-clients,--disable-egl --disable-simple-egl-clients,virtual/egl"
# Weston with cairo glesv2 support
PACKAGECONFIG[cairo-glesv2] = "--with-cairo-glesv2,--with-cairo=image,cairo"
# Weston with lcms support
PACKAGECONFIG[lcms] = "--enable-lcms,--disable-lcms,lcms"
# Weston with webp support
PACKAGECONFIG[webp] = "--with-webp,--without-webp,libwebp"
# Weston with systemd-login support
PACKAGECONFIG[systemd] = "--enable-systemd-login,--disable-systemd-login,systemd dbus"
# Weston with Xwayland support (requires X11 and Wayland)
PACKAGECONFIG[xwayland] = "--enable-xwayland,--disable-xwayland"
# colord CMS support
PACKAGECONFIG[colord] = "--enable-colord,--disable-colord,colord"
# Clients support
PACKAGECONFIG[clients] = "--enable-clients --enable-simple-clients --enable-demo-clients-install,--disable-clients --disable-simple-clients"
# Weston with PAM support
#PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam"

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
}

PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'xwayland', '${PN}-xwayland', '', d)} \
             libweston-${WESTON_MAJOR_VERSION} ${PN}-examples"

FILES:${PN} = "${bindir}/weston ${bindir}/weston-terminal ${bindir}/weston-info ${bindir}/weston-launch ${bindir}/wcap-decode ${libexecdir} ${libdir}/${BPN}/*.so ${datadir}"

FILES_libweston-${WESTON_MAJOR_VERSION} = "${libdir}/lib*${SOLIBS} ${libdir}/libweston-${WESTON_MAJOR_VERSION}/*.so"
SUMMARY_libweston-${WESTON_MAJOR_VERSION} = "Helper library for implementing 'wayland window managers'."

FILES:${PN}-examples = "${bindir}/*"

FILES:${PN}-xwayland = "${libdir}/libweston-${WESTON_MAJOR_VERSION}/xwayland.so"
RDEPENDS:${PN}-xwayland += "xserver-xorg-xwayland"

RDEPENDS:${PN} += "xkeyboard-config"
RRECOMMENDS:${PN} = "weston-conf liberation-fonts"
RRECOMMENDS:${PN}-dev += "wayland-protocols"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system weston-launch"
