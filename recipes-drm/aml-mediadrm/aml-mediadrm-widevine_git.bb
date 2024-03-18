DESCRIPTION = "widevine DRM"

LICENSE = "CLOSED"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"

PROVIDES = "widevine"

#SRC_URI = "git://${AML_GIT_ROOT_WV}/vendor/widevine;protocol=${AML_GIT_PROTOCOL};branch=amlogic-linux;destsuffix=git/widevine-bin"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libmediadrm')}"

#SRCREV ?= "${AUTOREV}"
PV = "git${SRCPV}"

S = "${WORKDIR}/git"
DEPENDS = "optee-userspace aml-secmem aml-mediahal-sdk"
inherit autotools pkgconfig

ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET:aarch64 = "aarch64.lp64."
TA_TARGET="noarch"

def get_widevine_version(datastore):
    if datastore.getVar("WIDEVINE_VERSION", True) == "16":
        return "prebuilt-v16"
    else:
        return "prebuilt-v15"

WIDEVINE_VER = "${@get_widevine_version(d)}"
do_install() {

    install -d -m 0755 ${D}/lib/optee_armtz
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}/usr/bin
    install -d -m 0755 ${D}/usr/include
    install -d -m 0755 ${D}${libdir}/pkgconfig

    install -D -m 0755 ${S}/widevine-bin/${WIDEVINE_VER}/${TA_TARGET}/ta/${TDK_VERSION}/*.ta ${D}/lib/optee_armtz/
    install -D -m 0644 ${S}/widevine-bin/${WIDEVINE_VER}/${TA_TARGET}/include/*.h ${D}${includedir}/
    install -D -m 0644 ${S}/widevine-bin/${WIDEVINE_VER}/${TA_TARGET}/pkgconfig/widevine.pc ${D}${libdir}/pkgconfig

    install -D -m 0644 ${S}/widevine-bin/${WIDEVINE_VER}/${PLATFORM_TDK_VERSION}/${ARM_TARGET}/libwidevine_ce_cdm_shared.so ${D}${libdir}
    install -D -m 0644 ${S}/widevine-bin/${WIDEVINE_VER}/${PLATFORM_TDK_VERSION}/${ARM_TARGET}/liboemcrypto.so ${D}${libdir}
    install -D -m 0755 ${S}/widevine-bin/${WIDEVINE_VER}/${PLATFORM_TDK_VERSION}/${ARM_TARGET}/widevine_ce_cdm_unittest ${D}/usr/bin
}
FILES:${PN} += "${libdir}/*.so /lib/optee_armtz/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "ldflags dev-so dev-elf already-stripped installed-vs-shipped"
INSANE_SKIP:${PN}-dev = "ldflags dev-so dev-elf already-stripped"
