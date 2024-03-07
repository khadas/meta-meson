SUMMARY = "aml key provision"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/provision;protocol=${AML_GIT_PROTOCOL};branch=projects/buildroot/tdk-v2.4"
SRC_URI:append = " file://aml_key_inject.service"
SRC_URI:append = " file://aml_key_inject.init"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/provision')}"

inherit cmake pkgconfig systemd update-rc.d

INITSCRIPT_NAME = "aml_key_inject"
INITSCRIPT_PARAMS = "start 30 2 3 4 5 . stop 80 0 6 1 ."


do_configure[noexec] = "1"
do_compile[noexec] = "1"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git"
DEPENDS += "optee-userspace"
ARM_TARGET:aarch64 = "64"

inherit autotools pkgconfig systemd

do_install() {
    # install headers
    install -d -m 0755 ${D}/lib/optee_armtz
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}/usr/bin

    install -D -m 0755 ${S}/ca/${PLATFORM_TDK_VERSION}/bin${ARM_TARGET}/tee_provision ${D}/usr/bin/
    install -D -m 0755 ${S}/ca/${PLATFORM_TDK_VERSION}/bin${ARM_TARGET}/tee_key_inject ${D}/usr/bin/
    install -D -m 0755 ${S}/ca/${PLATFORM_TDK_VERSION}/lib${ARM_TARGET}/libprovision.so ${D}${libdir}
    install -D -m 0755 ${S}/ta/${TDK_VERSION}/*.ta ${D}/lib/optee_armtz/

    install -D -m 0644 ${WORKDIR}/aml_key_inject.service ${D}${systemd_unitdir}/system/aml_key_inject.service

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/aml_key_inject.init ${D}${sysconfdir}/init.d/aml_key_inject
}

SYSTEMD_SERVICE:${PN} = "aml_key_inject.service "
FILES:${PN} += "/lib/optee_armtz/* /usr/bin/* ${sysconfdir}"
FILES:${PN} += "${libdir}/*"
FILES:${PN}-dev = " "
INSANE_SKIP:${PN} = "ldflags dev-so dev-elf"
INSANE_SKIP:${PN}-dev = "ldflags dev-so dev-elf"
