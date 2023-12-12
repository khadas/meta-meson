inherit module systemd

SUMMARY = "amlogic adla kernel drivers"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "linux-meson"

SRCREV ?="${AUTOREV}"

do_populate_lic[noexec] = "1"
do_configure[noexec] = "1"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

EXTRA_OEMAKE = "KDIR=${STAGING_KERNEL_DIR} ARCH=${ARCH} CROSS_COMPILE=${CROSS_COMPILE} Building_Yocto=1"

inherit module

do_compile() {
    cd ${S}
    oe_runmake -j1 -C ${S} ${EXTRA_OEMAKE}

}

do_install() {
    NPU_INSTALL_DIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/amlogic/adla

    install -d ${NPU_INSTALL_DIR}

    install -m 0644 -D ${S}/adla/kmd/adla_core.ko ${NPU_INSTALL_DIR}
}

DEPENDS += ""


KERNEL_MODULE_AUTOLOAD += "adla_core"
