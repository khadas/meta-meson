SUMMARY = "amlogic cve kernel drivers"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "linux-meson"

SRCREV ?="${AUTOREV}"
PV = "1.0"
PN = "cve"

do_populate_lic[noexec] = "1"
do_configure[noexec] = "1"

inherit module systemd

EXTRA_OEMAKE = "KERNEL_SRC=${STAGING_KERNEL_DIR} M=${S} EXTRA_CFLAGS+='-Wno-format-extra-args'"

do_compile() {
    oe_runmake -j1 -C ${S} ${EXTRA_OEMAKE}
}

do_install() {
    CVE_INSTALL_DIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/amlogic/cve
    install -d ${CVE_INSTALL_DIR}

    install -m 0644 -D ${S}/amlogic_cve.ko ${CVE_INSTALL_DIR}
    oe_runmake -C ${KERNEL_SRC} ${S} clean
}

FILES:${PN} = "/usr/bin"
FILES:${PN}-dev = "/usr/include/*"

KERNEL_MODULE_AUTOLOAD += "amlogic_cve"

