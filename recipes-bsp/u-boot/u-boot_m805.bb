require u-boot-meson.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

EXTRA_OEMAKE = ''
PACKAGE_ARCH = "${MACHINE_ARCH}"
PATCHTOOL="git"

#For common patch
do_configure[noexec] = "1"

PR = "r1"
PV = "m805+git${SRCPV}"
PATH_append = ":/opt/gnutools/arc-4.8-amlogic-20130904-r2/bin/:/opt/CodeSourcery/Sourcery_G++_Lite/bin/"

do_compile () {
    cd ${S}
    export BUILD_FOLDER=${S}/build/
    unset SOURCE_DATE_EPOCH
    UBOOT_TYPE="${UBOOT_MACHINE}"
    LDFLAGS= make ${UBOOT_TYPE}_config && LDFLAGS= make
}

do_install () {
    install -d ${D}/boot
    install ${S}/build/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
    ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}
}

do_deploy () {
    install -d ${DEPLOYDIR}
    install ${S}/build/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}

    cd ${DEPLOYDIR}
    rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
    ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
    ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}

    install ${S}/build/u-boot.bin       ${DEPLOYDIR}/
    install ${S}/build/u-boot-usb.bin   ${DEPLOYDIR}/
    install ${S}/build/u-boot-comp.bin  ${DEPLOYDIR}/
    install ${S}/build/ddr_init.bin     ${DEPLOYDIR}/
}

