SUMMARY = "Meson init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
DEPENDS = "virtual/kernel"
#RDEPENDS:${PN} = "udev udev-extraconf"
SRC_URI = "file://init-meson.sh"

PR = "r0"

S = "${WORKDIR}"

do_install() {
    install -m 0755 ${WORKDIR}/init-meson.sh ${D}/init
    install -d ${D}/dev
    mknod -m 622 ${D}/dev/console c 5 1

    if ${@bb.utils.contains('DISTRO_FEATURES', 'zapper', 'true', 'false', d)}; then
        sed -i '/mkdir -p \/var\/run/a\\techo 100 > /proc/sys/vm/watermark_scale_factor' ${D}/init
        sed -i '/mkdir -p \/var\/run/a\\n\techo 3072 > \/proc\/sys\/vm\/min_free_kbytes' ${D}/init
    elif ${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', 'true', 'false', d)}; then
        sed -i '/mkdir -p \/var\/run/a\\techo 80 > /proc/sys/vm/watermark_scale_factor' ${D}/init
        sed -i '/mkdir -p \/var\/run/a\\n\techo 12288 > \/proc\/sys\/vm\/min_free_kbytes' ${D}/init
    fi

    #read_args need about 200ms on nand platform, so disable it on nand
    if ${@bb.utils.contains("DISTRO_FEATURES", "nand", "true", "false", d)}; then
        if ${@bb.utils.contains("DISTRO_FEATURES", "use-mdev", "true", "false", d)}; then
            sed -i '/\/var\/run/a\\n\tmdev -s' ${D}/init
        fi
        sed -i -e 's/root_fstype=\"ext4\"/root_fstype=\"${ROOTFS_TYPE}\"/' ${D}/init
        sed -i '/read_args(/a\\treturn 0' ${D}/init
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'kernel_515', 'true', 'false', d)}; then
        sed -i "s/#load_ramdisk_ko/load_ramdisk_ko/g" ${D}/init
        if ${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'false', 'true', d)}; then
            sed -i "s/#upstream_emmc_mount/upstream_emmc_mount/g" ${D}/init
        fi
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'kernel_66', 'true', 'false', d)}; then
        sed -i "s/#load_ramdisk_ko/load_ramdisk_ko/g" ${D}/init
        if ${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'false', 'true', d)}; then
            sed -i "s/#upstream_emmc_mount/upstream_emmc_mount/g" ${D}/init
        fi
    fi
}

FILES:${PN} += " /init /dev "

# Due to kernel dependency
PACKAGE_ARCH = "${MACHINE_ARCH}"
