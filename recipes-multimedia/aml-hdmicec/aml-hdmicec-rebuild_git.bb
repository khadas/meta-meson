SUMMARY = "Hdmi Control Service"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

DEPENDS = " libbinder liblog aml-audio-service"
RDEPENDS_${PN} = " liblog libbinder aml-audio-service"
do_configure[noexec] = "1"
EXTERNALSRC="${@'${MESON_ROOT_PATH}/aml-comp/vendor/amlogic/hdmicec'}"
AML_HDMICEC_DIR="${@'${MESON_ROOT_PATH}/aml-comp/vendor/amlogic/aml_hdmicec'}"
S="${WORKDIR}/git"
ARM_TARGET = "32"
ARM_TARGET_aarch64 = "64"

EXTRA_OEMAKE="STAGING_DIR=${STAGING_DIR_TARGET} \
                TARGET_DIR=${D} \
             "

do_install() {
    install -m 0644 ${S}/libcec/include/hdmi_cec_intf.h ${AML_HDMICEC_DIR}/include
    install -m 0755 ${S}/libcec.so ${AML_HDMICEC_DIR}/${ARM_TARGET}
}

do_compile() {
    cd ${S}
    oe_runmake  all
}

FILES_${PN} = "${libdir}/* ${bindir}/*"
FILES_${PN}-dev = "${includedir}/* "
INSANE_SKIP_${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP_${PN}-dev = "dev-so ldflags dev-elf"
