SUMMARY = "aml audio utils"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS = "liblog boost"

#SRC_URI = "git://${AML_GIT_ROOT}/linux/multimedia/amaudioutils;protocol=${AML_GIT_PROTOCOL};branch=master"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/aml_amaudioutils')}"

#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

do_configure[noexec] = "1"
inherit autotools pkgconfig
S="${WORKDIR}/git"

export AML_AMAUDIOUTILS_BUILD_DIR = "${B}"
export AML_AMAUDIOUTILS_STAGING_DIR = "${D}"
export AML_AMAUDIOUTILS_TARGET_DIR = "${D}"
export AML_AMAUDIOUTILS_BR2_ARCH = "${TARGET_ARCH}"
export TARGET_DIR = "${D}"
EXTRA_FLAGS:aarch64="TOOLCHAIN_NEON_SUPPORT=n"

EXTRA_OEMAKE="${EXTRA_FLAGS} STAGING_DIR=${D} \
                  TARGET_DIR=${D} \
                  AML_BUILD_DIR=${B}"

do_compile() {
    cd ${B}
    oe_runmake -C ${S} all
}

do_install() {
	install -d ${D}${libdir}
	install -d ${D}/usr/include/audio_utils
	install -d ${D}/usr/include/IpcBuffer
	install -m 644 -D ${B}/libamaudioutils.so ${D}${libdir}
	install -m 644 -D ${B}/libcutils.so ${D}${libdir}
	install -m 644 ${S}/include/audio_utils/*.h ${D}/usr/include/audio_utils
	install -m 644 ${S}/include/IpcBuffer/*.h ${D}/usr/include/IpcBuffer/
}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
