SUMMARY = "aml subtitleserver"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"
SRC_URI +="file://subtitleserver.service"

#do_configure[noexec] = "1"
inherit autotools cmake pkgconfig systemd
DEPENDS = " libbinder liblog aml-zvbi cairo aml-mediahal-sdk"
DEPENDS += " ${@bb.utils.contains('DISTRO_FEATURES', 'zapper', 'directfb', ' virtual/libgles2', d)}"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_subtitleserver.git;protocol=${AML_GIT_PROTOCOL};branch=master"
SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"
SRC_URI +="file://subtitleserver.service"
S="${WORKDIR}/git"

EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', ' -DUSE_DFB=ON', ' -DUSE_WAYLAND=ON', d)}"
EXTRA_OECMAKE += " \
    -DMEDIASYNC_FOR_SUBTITLE=ON \
"

TARGET_CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', ' -I${STAGING_INCDIR}/directfb', ' ', d)}"
TARGET_CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', ' -DMEDIASYNC_FOR_SUBTITLE  -DUSE_DFB', ' -DMEDIASYNC_FOR_SUBTITLE -DUSE_WAYLAND', d)}"

#do_compile() {
#    cd ${S}
#    oe_runmake  all
#}
#do_install() {
#   install -d ${D}${libdir}
#   install -d ${D}${bindir}
#   install -d ${D}${includedir}
#    cd ${S}
#    oe_runmake  install
#    install -m 0644 ${S}/client/include/*.h ${D}${includedir}
#    if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
#        install -D -m 0644 ${WORKDIR}/subtitleserver.service ${D}${systemd_unitdir}/system/subtitleserver.service
#    fi
#}

do_install_append() {
    if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
        install -D -m 0644 ${WORKDIR}/subtitleserver.service ${D}${systemd_unitdir}/system/subtitleserver.service
    fi
}

SYSTEMD_SERVICE_${PN} = "subtitleserver.service"

FILES_${PN} = "${libdir}/* ${bindir}/*"
FILES_${PN}-dev = "${includedir}/* "
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP_${PN}-dev = "dev-so ldflags dev-elf"
