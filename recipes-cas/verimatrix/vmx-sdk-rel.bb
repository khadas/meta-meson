DESCRIPTION = "Verimatrix SDK Release"
SECTION = "vmx-sdk-rel"
LICENSE = "CLOSE"
PV = "git${SRCPV}"
PR = "r0"

#Only enable it in OpenLinux
VMX_SDK_BRANCH:s4 = "m9y4-rel-linux"
VMX_SDK_BRANCH:sc2 = "m9x4-rel-linux"
VMX_SDK_BRANCH:aq2432 = "m9c3-rel-linux"

SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/vmx/sdk-rel')}"

#PN = 'verimatrix'
#SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#VMX SDK Version depends on SoC used
VMX_SDK_VERSION:s4 = "SDK_M9Y4_1_0_0"
VMX_SDK_VERSION:sc2 = "SDK_M9X4_1_0_0"
VMX_SDK_VERSION:aq2432 = "SDK_M9C3_1_0_0"

do_install() {
    install -d -m 0755 ${D}/usr/lib
    install -d -m 0755 ${D}/usr/include
    install -D -m 0644 ${S}/${VMX_SDK_VERSION}/include/* ${D}/usr/include
    install -D -m 0644 ${S}/${VMX_SDK_VERSION}/libs/libvmxca_client.so ${D}/usr/lib
    install -D -m 0644 ${S}/${VMX_SDK_VERSION}/libs/libvmxca_iptvclient.so ${D}/usr/lib
    install -D -m 0644 ${S}/${VMX_SDK_VERSION}/libs/libvmxca_webclient.so ${D}/usr/lib
}

FILES:${PN} = "${libdir}/* /usr/lib/* /lib/teetz/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
