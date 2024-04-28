DESCRIPTION = "liblog for android tools"
PR = "r0"
LICENSE = "Apache-2.0"

FILESEXTRAPATHS:prepend := "${THISDIR}/android-tools-logcat:"

LIC_FILES_CHKSUM = "file://${THISDIR}/android-tools-logcat/LICENSE-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

#SRCREV = "${AUTOREV}"

PV = "${SRCPV}"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/aml_commonlib;protocol=${AML_GIT_PROTOCOL};branch=master;"
#SRC_URI += "file://liblog.patch"
SRC_URI += "file://LICENSE-2.0"
SRC_URI += "file://logcat.conf"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/aml_commonlib')}"

S = "${WORKDIR}/git/liblog"

do_configure[noexec] = "1"
EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D}"

#do_package_qa[noexec] = "1"

EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-logcat', 'USE_FAKE_DEVICE=y', '', d)}"

do_compile(){
    ${MAKE} ${EXTRA_OEMAKE} -C ${S}
}

do_install(){
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    cp -ra ${B}/liblog*.so* ${D}${libdir}
    cp -ra ${S}/include/* ${D}${includedir}
    if ${@bb.utils.contains('DISTRO_FEATURES', 'disable-logcat', "true", "false", d)}; then
        install -d ${D}${sysconfdir}
        install -m 755 ${WORKDIR}/logcat.conf ${D}${sysconfdir}
    fi
}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-logcat', '${sysconfdir}/*', '', d)}"
FILES:${PN}-dev = "${includedir}/* "

INSANE_SKIP:${PN}-dev = "dev-so"
INSANE_SKIP:${PN} = "dev-so ldflags"
