DESCRIPTION = "Verimatrix Release Binaries"
SECTION = "vmx-release-binaries"
LICENSE = "CLOSE"
PV = "git${SRCPV}"
PR = "r0"

SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/vmx/vmx_release_binaries')}"

RDEPENDS_${PN} += "aml-libdvr aml-mediahal-sdk aml-audio-service liblog vmx-sdk-rel optee-userspace"

SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

VMX_REL_PATH = "TBD"
VMX_REL_PATH_sc2 = "s905x4_linux"
VMX_REL_PATH_s4 = "s905y4_linux"

do_install() {
    mkdir -p ${D}/lib/teetz
    install -d -m 0644 ${D}/usr/lib
    install -d -m 0644 ${D}/usr/bin
    install -d -m 0644 ${D}/usr/include
    install -D -m 0644 ${S}/${VMX_REL_PATH}/lib/* ${D}/usr/lib
    install -D -m 0644 ${S}/${VMX_REL_PATH}/ta/* ${D}/lib/teetz
    install -D -m 0755 ${S}/${VMX_REL_PATH}/vmx-indiv/* ${D}/usr/bin
    install -D -m 0644 ${S}/${VMX_REL_PATH}/include/* ${D}/usr/include
}

FILES_${PN} = "${libdir}/* /usr/lib/* /usr/bin/* /lib/teetz/*"
FILES_${PN}-dev = "${includedir}/* "
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"
