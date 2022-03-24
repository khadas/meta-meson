SUMMARY = "aml audio service"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libaudioeffect')}"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

do_configure[noexec] = "1"
inherit autotools pkgconfig
S="${WORKDIR}/git"
DEPENDS += "aml-audio-service liblog aml-audio-hal"
RDEPENDS_${PN} += "aml-audio-service liblog aml-audio-hal"

export TARGET_DIR = "${D}"
export HOST_DIR = "${STAGING_DIR_NATIVE}/usr/"

#To remove --as-needed compile option which is causing issue with linking
#ASNEEDED = ""
#PARALLEL_MAKE = ""
do_compile() {
    oe_runmake  -C ${S} all
}
do_install() {
        install -d ${D}${libdir}
        install -d ${D}${libdir}/soundfx/
        install -m 0755 -D ${S}/libeffectfactory.so -t ${D}${libdir}
        install -m 0755 -D ${S}/libbalance.so -t ${D}${libdir}/soundfx/
        install -m 0755 -D ${S}/libtreblebasswrapper.so -t ${D}${libdir}/soundfx/
        install -m 0755 -D ${S}/libhpeqwrapper.so -t ${D}${libdir}/soundfx/
        install -m 0755 -D ${S}/libavl.so -t ${D}${libdir}/soundfx/
        install -m 0755 -D ${S}/libvirtualsurround.so -t ${D}${libdir}/soundfx/
        install -m 0755 -D ${S}/libvirtualx.so -t ${D}${libdir}/soundfx/
}

FILES_${PN} = "${libdir}/*"
FILES_${PN}-dev = "${includedir}/* "
