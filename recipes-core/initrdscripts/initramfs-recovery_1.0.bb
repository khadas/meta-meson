SUMMARY = "recovery init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
DEPENDS = "virtual/kernel"
RDEPENDS:${PN} = "udev udev-extraconf"
SRC_URI = "file://init-recovery"

PR = "r0"

S = "${WORKDIR}"

SOC_BOARD = "default"
SOC_BOARD_aq2432 = "aq2432"

do_install() {
    install -m 0755 ${WORKDIR}/init-recovery ${D}/init
    install -d ${D}/dev
    mknod -m 622 ${D}/dev/console c 5 1

    if [ "${SOC_BOARD}" = "aq2432" ]; then
        sed -i '/udevadm\ trigger\ \-\-action\=add/a\\techo 80 > /proc/sys/vm/watermark_scale_factor' ${D}/init
        sed -i '/udevadm\ trigger\ \-\-action\=add/a\\n\techo 12288 > \/proc\/sys\/vm\/min_free_kbytes' ${D}/init
    fi
}

FILES:${PN} += " /init /dev "

# Due to kernel dependency
PACKAGE_ARCH = "${MACHINE_ARCH}"
