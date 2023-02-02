DESCRIPTION = "nagra-sdk"
SECTION = "nagra-sdk"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

PV = "git${SRCPV}"
PR = "r0"

#Only enable it in OpenLinux
#SRC_URI:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'nagra', 'git://${AML_GIT_ROOT_OP}/nagra-sdk-nocs.git;protocol=${AML_GIT_ROOT_PROTOCOL};branch=projects/openlinux/v3.6','', d)}"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/nagra/nagra-sdk')}"

PN = 'nagra-sdk'
SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

ARM_TARGET = "usr/lib"
ARM_TARGET:aarch64 = "usr/lib64"

do_install() {
    install -d -m 0755 ${D}/lib/optee_armtz
    install -d -m 0755 ${D}/${libdir}
    install -D -m 0644 ${S}/lib/ca/${ARM_TARGET}/libnagra_dal.so ${D}/${libdir}

    install -D -m 0644 ${S}/lib/ta/${CHIPSET_NAME}/bc2f95bc-14b6-4445-a43c-a1796e7cac31.ta ${D}/lib/optee_armtz
    install -D -m 0644 ${S}/lib/ta/${CHIPSET_NAME}/efdfed0c-a6bd-44d3-9c64-de426fc5fb89.ta ${D}/lib/optee_armtz
}

FILES:${PN} = "${libdir}/* /usr/lib/* /lib/optee_armtz/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
