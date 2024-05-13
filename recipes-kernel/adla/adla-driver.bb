inherit module systemd

SUMMARY = "amlogic adla kernel drivers"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "linux-meson"

#SRCREV ?="${AUTOREV}"

do_populate_lic[noexec] = "1"
do_configure[noexec] = "1"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

EXT_DRIVER_DIR = "common_drivers/drivers"
KOUT_DIR = "${EXT_DRIVER_DIR}/adla_driver/"

EXTRA_OEMAKE = " KOUT_DIR=${KOUT_DIR} KERNEL_SRC=${STAGING_KERNEL_DIR} KDIR=${STAGING_KERNEL_DIR} ARCH=${ARCH} CROSS_COMPILE=${CROSS_COMPILE} Building_Yocto=1"

inherit module

do_compile:prepend() {
    cd ${STAGING_KERNEL_DIR}/${EXT_DRIVER_DIR}

    # in common driver folder, create a link to this driver
    ln -sf "${S}" .
}

do_compile() {
    cd ${S}
    oe_runmake -j1 -C ${S} ${EXTRA_OEMAKE}
}

do_compile:append() {
    # in common driver folder, remove the temp link
    rm  ${STAGING_KERNEL_DIR}/${EXT_DRIVER_DIR}/adla_driver
}

do_install() {
    NPU_INSTALL_DIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/amlogic/adla

    install -d ${NPU_INSTALL_DIR}

    install -m 0644 -D ${KBUILD_OUTPUT}/${KOUT_DIR}/adla/kmd/adla_core.ko ${NPU_INSTALL_DIR}
}


do_clean() {
    cd ${S}
    oe_runmake -C ${S} ${EXTRA_OEMAKE} clean

}

DEPENDS += ""


KERNEL_MODULE_AUTOLOAD += "adla_core"
