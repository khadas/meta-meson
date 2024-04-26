SUMMARY = "aml subtitleserver"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#do_configure[noexec] = "1"
inherit autotools cmake pkgconfig systemd update-rc.d

INITSCRIPT_NAME = "subtitleserver"
INITSCRIPT_PARAMS = " \
 ${@bb.utils.contains('DISTRO_FEATURES', 'disable-subtitle', \
    'disable', 'start 40 2 3 4 5 . stop 80 0 6 1 .', d)}\
"

DEPENDS = " libbinder liblog aml-zvbi libtinyxml2 cairo libpng aml-mediahal-sdk"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_subtitleserver.git;protocol=${AML_GIT_PROTOCOL};branch=master"
#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"
SRC_URI +="file://subtitleserver.service"
SRC_URI +="file://subtitleserver.init"

S="${WORKDIR}/git"

EXTRA_OECMAKE += " \
    -DMEDIASYNC_FOR_SUBTITLE=ON \
"

RDEPENDS:${PN} = " liblog libbinder aml-zvbi  libtinyxml2 cairo libpng aml-mediahal-sdk"

EXTRA_OEMAKE="STAGING_DIR=${STAGING_DIR_TARGET} \
              TARGET_DIR=${D} \
             "

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'aml-SUB-FB', '', \
               bb.utils.contains('DISTRO_FEATURES', 'aml-SUB-DFB', 'directfb', 'virtual/libgles2', d), d)}"


EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'aml-SUB-FB', '-DUSE_FB=ON', \
               bb.utils.contains('DISTRO_FEATURES', 'aml-SUB-DFB', '-DUSE_DFB=ON', '-DUSE_WAYLAND=ON', d), d)}"


TARGET_CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'aml-SUB-FB', '-DMEDIASYNC_FOR_SUBTITLE -DUSE_FB', \
               bb.utils.contains('DISTRO_FEATURES', 'aml-SUB-DFB', '-DMEDIASYNC_FOR_SUBTITLE -DUSE_DFB -I${STAGING_INCDIR}/directfb', '-DMEDIASYNC_FOR_SUBTITLE -DUSE_WAYLAND', d), d)}"


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

do_install:append() {
    if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
        install -D -m 0644 ${WORKDIR}/subtitleserver.service ${D}${systemd_unitdir}/system/subtitleserver.service
    fi

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/subtitleserver.init ${D}${sysconfdir}/init.d/subtitleserver
}

SYSTEMD_SERVICE:${PN} = "subtitleserver.service"

FILES:${PN} = "${libdir}/* ${bindir}/* ${sysconfdir}"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
