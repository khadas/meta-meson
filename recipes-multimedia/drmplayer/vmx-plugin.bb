DESCRIPTION = "aml drmp verimatrix plugin library"
PN = 'vmx-plugin'
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libmediadrm/verimatrix')}"

#S = "${WORKDIR}/git"
SRCREV ?= "${AUTOREV}"

do_compile[noexec] = "1"

DEPENDS += " aml-mediahal-sdk optee-userspace aml-secmem liblog "

EXTRA_OEMAKE=" STAGING_DIR=${STAGING_DIR_TARGET} \
                 TARGET_DIR=${D} \
                 "
#VMX_PLUGIN_PATH = "NoSupport"
VMX_PLUGIN_PATH:sc2 = "m9x4"
VMX_PLUGIN_PATH:s4 = "m9y4"

do_install() {
    mkdir -p ${D}/${datadir}/mediadrm
    install -d -m 0755 ${D}/${datadir}/mediadrm
    install -d -m 0755 ${D}/usr/lib
    install -D -m 0644 ${S}/${VMX_PLUGIN_PATH}/iptv/libdec_ca_vmx_iptv.so ${D}/usr/lib
    install -D -m 0644 ${S}/${VMX_PLUGIN_PATH}/web/libdec_ca_vmx_web.so ${D}/usr/lib
}

INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"

FILES:${PN} += " ${bindir}/* ${libdir}/*.so ${datadir}/mediadrm"
FILES:${PN}-dev = "${includedir}/* "

