
SUMMARY = "This receipe compiles the westeros compositor DRM SOC imnplementation : westeros-gl, westeros-egl"

LICENSE = "Apache-2.0"

LICENSE_LOCATION ?= "${S}/../LICENSE"

LIC_FILES_CHKSUM = "file://${LICENSE_LOCATION};md5=8fb65319802b0c15fc9e0835350ffa02"

PV = "1.0"

#S = "${WORKDIR}/git/drm"

include westeros_rev.inc
# SRC_URI = "${WESTEROS_URI}"
#SRCREV = "${WESTEROS_SRCREV}"
# SRC_URI:append = " file://0001-support-pip.patch;patchdir=../"
# SRC_URI:append = " file://0008-support-mono-time-rendering.patch;patchdir=../"
# SRC_URI:append = " file://0009-advanced-frame-proper-release.patch;patchdir=../"
# SRC_URI:append = " file://0010-westeros-soc-drm-patch-for-video_config.patch;patchdir=../"
# SRC_URI:append = " file://0011-add-store-scaling-and-mode-flow.patch;patchdir=../"
# SRC_URI:append = " file://0012-modify-auto-mode-flow.patch;patchdir=../"
# SRC_URI:append = " file://0013-modify-error-width-for-480i-and-576i.patch;patchdir=../"
# SRC_URI:append = " file://0014-Distinguish-between-hpd-and-mode-event.patch;patchdir=../"

SRCREV_FORMAT = "westeros-soc"

DEPENDS = "wayland  glib-2.0 libdrm virtual/egl aml-avsync aml-ubootenv"
DEPENDS += "linux-uapi-headers meson-mode-policy"
PROVIDES = "virtual/westeros-soc"
RDEPENDS:${PN} = " aml-avsync aml-ubootenv"

RPROVIDES:${PN} = "virtual/westeros-soc"

PACKAGECONFIG[gbm] = "--enable-gbm-modifiers"
PACKAGECONFIG += "gbm"

CFLAGS:append = " \
                    -DUSE_AMLOGIC_MESON \
                    -DUSE_AMLOGIC_MESON_MSYNC \
                    -I${STAGING_INCDIR}/libdrm \
                    ${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', ' -DBUILD_AML_TV', ' -DBUILD_AML_STB', d)} \
                    "

LDFLAGS:append  = " -lamlavsync -lubootenv -lz"
SECURITY_CFLAGS:remove = "-fpie"
SECURITY_CFLAGS:remove = "-pie"

DEBIAN_NOAUTONAME:${PN} = "1"
DEBIAN_NOAUTONAME:${PN}-dbg = "1"
DEBIAN_NOAUTONAME:${PN}-dev = "1"
DEBIAN_NOAUTONAME:${PN}-staticdev = "1"

inherit autotools pkgconfig
# coverity
