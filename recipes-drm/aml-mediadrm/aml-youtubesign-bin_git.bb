DESCRIPTION = "youtube sign bin"

LICENSE = "CLOSED"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"

PROVIDES = "youtubesign-bin"
RDEPENDS:${PN} += " optee-userspace"

SRCREV ?= "${AUTOREV}"
#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/prebuilt/libmediadrm;protocol=${AML_GIT_ROOT_PROTOCOL};branch=linux-buildroot"

S = "${WORKDIR}/git"

#inherit autotools pkgconfig
ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET:aarch64 = "aarch64.lp64."
TA_TARGET="noarch"

do_install:append() {
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}/usr/include
    install -d -m 0755 ${D}/lib/optee_armtz
    install -D -m 0755 ${S}/youtubesign-bin/prebuilt/${TA_TARGET}/ta/${TDK_VERSION}/*.ta ${D}/lib/optee_armtz/
    install -D -m 0644 ${S}/youtubesign-bin/prebuilt/${PLATFORM_TDK_VERSION}/${ARM_TARGET}/*.so ${D}${libdir}
    install -D -m 0644 ${S}/youtubesign-bin/prebuilt/noarch/include/* ${D}/usr/include/
}

FILES:${PN} += "${libdir}/*.so /lib/optee_armtz/* ${includedir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "ldflags dev-so dev-elf already-stripped"
INSANE_SKIP:${PN}-dev = "ldflags dev-so dev-elf already-stripped"
