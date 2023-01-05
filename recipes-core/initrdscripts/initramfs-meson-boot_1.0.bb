SUMMARY = "Meson init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
DEPENDS = "virtual/kernel"
RDEPENDS_${PN} = "udev udev-extraconf"
SRC_URI = "file://init-meson.sh"

PR = "r0"

S = "${WORKDIR}"

SOC_BOARD = "default"
SOC_BOARD_aq2432 = "aq2432"

do_install() {
        install -m 0755 ${WORKDIR}/init-meson.sh ${D}/init
        install -d ${D}/dev
        mknod -m 622 ${D}/dev/console c 5 1

        if [ "${SOC_BOARD}" = "aq2432" ]; then
            sed -i '/mkdir -p \/var\/run/a\\techo 80 > /proc/sys/vm/watermark_scale_factor' ${D}/init
            sed -i '/mkdir -p \/var\/run/a\\n\techo 12288 > \/proc\/sys\/vm\/min_free_kbytes' ${D}/init
        fi
}

FILES_${PN} += " /init /dev "

# Due to kernel dependency
PACKAGE_ARCH = "${MACHINE_ARCH}"
