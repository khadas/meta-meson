DESCRIPTION = "Verimatrix Release Binaries"
SECTION = "vmx-release-binaries"
LICENSE = "CLOSE"
PV = "git${SRCPV}"
PR = "r0"

SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/vmx/vmx_release_binaries')}"

RDEPENDS:${PN} += "aml-libdvr aml-mediahal-sdk aml-audio-service liblog vmx-sdk-rel optee-userspace"

SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

VMX_REL_PATH = "TBD"
VMX_REL_PATH:sc2 = "s905x4_linux"
VMX_REL_PATH:s4 = "s905y4_linux"
VMX_REL_PATH:aq2432 = "s805c3_linux"

do_install() {
    mkdir -p ${D}/lib/optee_armtz
    install -d -m 0644 ${D}/usr/lib
    install -d -m 0644 ${D}/usr/bin
    install -d -m 0644 ${D}/usr/include
    install -D -m 0644 ${S}/${VMX_REL_PATH}/lib/* ${D}/usr/lib
    install -D -m 0644 ${S}/${VMX_REL_PATH}/ta/* ${D}/lib/optee_armtz
    install -D -m 0755 ${S}/${VMX_REL_PATH}/vmx-indiv/* ${D}/usr/bin
    install -D -m 0644 ${S}/${VMX_REL_PATH}/include/* ${D}/usr/include
}

FILES:${PN} = "${libdir}/* /usr/lib/* /usr/bin/* /lib/optee_armtz/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
