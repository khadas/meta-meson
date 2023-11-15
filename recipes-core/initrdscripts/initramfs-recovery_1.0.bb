SUMMARY = "recovery init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
DEPENDS = "virtual/kernel"
RDEPENDS:${PN} = "udev udev-extraconf"
SRC_URI = "file://init-recovery"

PR = "r0"

S = "${WORKDIR}"

do_install() {
    install -m 0755 ${WORKDIR}/init-recovery ${D}/init
    install -d ${D}/dev
    mknod -m 622 ${D}/dev/console c 5 1

    if ${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', 'true', 'false', d)}; then
        sed -i '/udevadm\ trigger\ \-\-action\=add/a\\techo 80 > /proc/sys/vm/watermark_scale_factor' ${D}/init
        sed -i '/udevadm\ trigger\ \-\-action\=add/a\\n\techo 12288 > \/proc\/sys\/vm\/min_free_kbytes' ${D}/init
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'kernel_515', 'true', 'false', d)}; then
        sed -i "s/#kernel_515_module_install/kernel_515_module_install/g" ${D}/init
        if ${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'false', 'true', d)}; then
            sed -i "s/#upstream_emmc_mount/upstream_emmc_mount/g" ${D}/init
        fi
    fi
}

do_install:append:t5w() {
    sed -i '/boot_root(/a\\tattach_unifykey' ${D}/init
}

FILES:${PN} += " /init /dev "

# Due to kernel dependency
PACKAGE_ARCH = "${MACHINE_ARCH}"
