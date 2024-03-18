inherit module

SUMMARY = "Amlogic NPU driver"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

DEPENDS += "linux-meson"

#SRCREV ?="${AUTOREV}"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
do_compile() {
    cd ${S}
    ./aml_buildroot.sh ${ARCH} ${STAGING_KERNEL_DIR} ${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}
}

do_install() {
    NPU_KO_INSTALL_DIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/amlogic/npu
    NPU_SO_INSTALL_DIR=${D}/usr/lib

    install -d ${NPU_KO_INSTALL_DIR}
    install -d ${NPU_SO_INSTALL_DIR}

    if [ ${ARCH}="arm64" ];then
        install -m 0755 ${S}/build/sdk/drivers/galcore.ko ${NPU_KO_INSTALL_DIR}
        install -m 0755 ${S}/sharelib/lib64/* ${NPU_SO_INSTALL_DIR}
    else
        install -m 0755 ${S}/build/sdk/drivers/galcore.ko ${NPU_KO_INSTALL_DIR}
        install -m 0755 ${S}/sharelib/lib32/* ${NPU_SO_INSTALL_DIR}
    fi
}

PACKAGES = "${PN}"
FILES:${PN} += "/lib/modules/${KERNEL_VERSION}/kernel/amlogic/npu/* /usr/lib/*"

KERNEL_MODULE_AUTOLOAD += "galcore"

INSANE_SKIP:${PN} = "dev-so ldflags dev-elf already-stripped"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
