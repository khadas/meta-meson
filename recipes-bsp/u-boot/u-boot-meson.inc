SUMMARY = "Universal Boot Loader for embedded devices(amlogic)"
HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader"

inherit uboot-config deploy

EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}"'

# Allow setting an additional version string that will be picked up by the
# u-boot build system and appended to the u-boot version.  If the .scmversion
# file already exists it will not be overwritten.
UBOOT_LOCALVERSION ?= ""
SOURCE_DATE_EPOCH = ""

# Some versions of u-boot use .bin and others use .img.  By default use .bin
# but enable individual recipes to change this value.
UBOOT_SUFFIX ?= "bin"
UBOOT_SUFFIX:s7 ?= "bin.signed"
UBOOT_SUFFIX:s7d ?= "bin.signed"
UBOOT_SUFFIX:sc2 ?= "bin.signed"
UBOOT_SUFFIX:t7 ?= "bin.signed"
UBOOT_SUFFIX:s4 ?= "bin.signed"
UBOOT_SUFFIX:s5 ?= "bin.signed"
UBOOT_SUFFIX:t3 ?= "bin.signed"
UBOOT_SUFFIX:t3x ?= "bin.signed"
UBOOT_SUFFIX:t5m ?= "bin.signed"
UBOOT_SUFFIX:s1a ?= "bin.signed"
UBOOT_IMAGE ?= "u-boot-${MACHINE}-${PV}-${PR}.${UBOOT_SUFFIX}"
UBOOT_BINARY ?= "u-boot.${UBOOT_SUFFIX}"
UBOOT_SYMLINK ?= "u-boot-${MACHINE}.${UBOOT_SUFFIX}"
UBOOT_MAKE_TARGET ?= "all"

# Some versions of u-boot build an SPL (Second Program Loader) image that
# should be packaged along with the u-boot binary as well as placed in the
# deploy directory.  For those versions they can set the following variables
# to allow packaging the SPL.
SPL_BINARY ?= ""
SPL_IMAGE ?= "${SPL_BINARY}-${MACHINE}-${PV}-${PR}"
SPL_SYMLINK ?= "${SPL_BINARY}-${MACHINE}"

do_compile () {
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'ld-is-gold', '', d)}" = "ld-is-gold" ] ; then
		sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' config.mk
	fi

	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	if [ ! -e ${B}/.scmversion -a ! -e ${S}/.scmversion ]
	then
		echo ${UBOOT_LOCALVERSION} > ${B}/.scmversion
		echo ${UBOOT_LOCALVERSION} > ${S}/.scmversion
	fi

	cd ${S}
	oe_runmake distclean
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake
}

do_install () {
    install -d ${D}/boot
    install ${S}/fip/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
    ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}

    if [ -e ${WORKDIR}/fw_env.config ] ; then
        install -d ${D}${sysconfdir}
        install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    fi

    if [ "x${SPL_BINARY}" != "x" ]
    then
        install ${S}/${SPL_BINARY} ${D}/boot/${SPL_IMAGE}
        ln -sf ${SPL_IMAGE} ${D}/boot/${SPL_BINARY}
    fi
}

FILES:${PN} = "/boot ${sysconfdir}"
FILESPATH =. "${FILE_DIRNAME}/u-boot-git/${MACHINE}:"

do_deploy () {
    install -d ${DEPLOYDIR}
    install ${S}/fip/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}

    cd ${DEPLOYDIR}
    rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
    ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
    ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}

    if [ "x${SPL_BINARY}" != "x" ]
    then
        install ${S}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}
        rm -f ${DEPLOYDIR}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_SYMLINK}
        ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_BINARY}
        ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_SYMLINK}
    fi

    echo "UBOOT_SUFFIX is ${UBOOT_SUFFIX}"
    case ${UBOOT_SUFFIX} in
    "bin.signed")
        install ${S}/fip/u-boot.bin.signed ${DEPLOYDIR}/
        test -f ${S}/fip/u-boot.bin.device.signed && install ${S}/fip/u-boot.bin.device.signed ${DEPLOYDIR}/
        install ${S}/fip/u-boot.bin.usb.signed ${DEPLOYDIR}/
        install ${S}/fip/u-boot.bin.sd.bin.signed ${DEPLOYDIR}/
        UBOOT_TYPE="${UBOOT_MACHINE}"
        install ${S}/fip/${UBOOT_TYPE%_config}-u-boot.aml.zip ${DEPLOYDIR}/ || true

        if ${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'true', 'false', d)}; then
            install ${S}/fip/_tmp/bb1st.sto.bin.signed ${DEPLOYDIR}/
            install ${S}/fip/_tmp/blob-bl2e.sto.bin.signed ${DEPLOYDIR}/
            install ${S}/fip/_tmp/blob-bl2x.bin.signed ${DEPLOYDIR}/
            install ${S}/fip/_tmp/ddr-fip.bin ${DEPLOYDIR}/
            install ${S}/fip/_tmp/device-fip.bin.signed ${DEPLOYDIR}/
        fi
    ;;
    "bin")
        install ${S}/fip/u-boot.bin ${DEPLOYDIR}/
        install ${S}/fip/u-boot.bin.usb.bl2 ${DEPLOYDIR}/
        install ${S}/fip/u-boot.bin.usb.tpl ${DEPLOYDIR}/
        install ${S}/fip/u-boot.bin.sd.bin ${DEPLOYDIR}/
		if ${@bb.utils.contains('DISTRO_FEATURES', 'uboot-abmode', 'true', 'false', d)}; then
            install ${S}/fip/fip-bl33.bin ${DEPLOYDIR}/
        fi
    ;;
    esac
}

addtask deploy before do_build after do_compile

