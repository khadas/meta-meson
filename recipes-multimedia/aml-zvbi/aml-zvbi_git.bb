SUMMARY = "aml customization of zvbi library"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://README;md5=91789e3b1cce0c7cd3f26db7a9f9bfac"

inherit autotools pkgconfig
DEPENDS = "libpng liblog"
do_configure[noexec] = "1"

#SRC_URI = "git://${AML_GIT_ROOT}/platform/external/libzvbi.git;protocol=${AML_GIT_PROTOCOL};branch=ics-amlogic;name=libzvbi"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/zvbi')}"

#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S="${WORKDIR}/git"
ARCH_IS_64:aarch64 = "y"
ARCH_IS_64:armv7a = "n"
EXTRA_OEMAKE=" OUT_DIR=${B} ARCH_IS_64=${ARCH_IS_64}"
S="${WORKDIR}/git"

do_compile() {
    cd ${S}
    oe_runmake all
}
do_install() {
   install -d ${D}${libdir}
   install -d ${D}${includedir}
    install -m 0644 ${B}/libzvbi.so ${D}${libdir}
    install -m 0644 ${S}/src/libzvbi.h ${D}${includedir}
    install -m 0644 ${S}/src/dtvcc.h ${D}${includedir}
}

FILES:${PN} = "${libdir}/*"
FILES:${PN}-dev = "${includedir}/*"
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
