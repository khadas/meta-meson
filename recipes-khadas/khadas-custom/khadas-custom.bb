SUMMARY = "khadas-custom"
SECTION = "khadas-custom"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302" 

inherit systemd
 
S = "${WORKDIR}"
 
SRC_URI = "\
	file://overlays/vim4/ext_board.dtbo \
	file://overlays/vim4/fb-1024×600.dtbo \
	file://overlays/vim4/fix-ts050-mipi-console.dtbo \
	file://overlays/vim4/i2cm_a.dtbo \
	file://overlays/vim4/i2cm_f.dtbo \
	file://overlays/vim4/i2s.dtbo \
	file://overlays/vim4/onewire.dtbo \
	file://overlays/vim4/pwm_f.dtbo \
	file://overlays/vim4/spdifout.dtbo \
	file://overlays/vim4/spi0.dtbo \
	file://overlays/vim4/uart_e.dtbo \
	file://overlays/vim4/vim4n-imx415.dtbo \
	file://overlays/vim4/vim4n-os08a10.dtbo \
	file://overlays/vim4/kvim4.dtb.overlay.env \
	file://fan_setup.sh \
	file://uEnv.txt \
	file://fan.sh \
	file://overlays/vim3/4k2k_fb.dtbo \
	file://overlays/vim3/disable-i2c3.dtbo \
	file://overlays/vim3/disable-ts050.dtbo \
	file://overlays/vim3/i2s.dtbo \
	file://overlays/vim3/m2x-eth.dtbo \
	file://overlays/vim3/onewire.dtbo \
	file://overlays/vim3/os08a10.dtbo \
	file://overlays/vim3/panfrost.dtbo \
	file://overlays/vim3/pwm_f.dtbo \
	file://overlays/vim3/spdifout.dtbo \
	file://overlays/vim3/spi1.dtbo \
	file://overlays/vim3/uart3.dtbo \
	file://overlays/vim3/watchdog.dtbo \
	file://overlays/vim3/kvim3.dtb.overlay.env \
	file://overlays/vim1s/4k2k_fb.dtbo \
	file://overlays/vim1s/i2cm_e.dtbo \
	file://overlays/vim1s/i2s.dtbo \
	file://overlays/vim1s/onewire.dtbo \
	file://overlays/vim1s/panfrost.dtbo \
	file://overlays/vim1s/pwm_f.dtbo \
	file://overlays/vim1s/spdifout.dtbo \
	file://overlays/vim1s/spi0.dtbo \
	file://overlays/vim1s/uart_c.dtbo \
	file://overlays/vim1s/kvim1s.dtb.overlay.env \
	file://khadas-custom.sh \
	file://khadas-custom.service \
	"

do_install() {

    install -d ${D}/boot/

    if [ "${BOARD_NAME}" = "vim4" ]; then
        DTB_PATH="kvim4.dtb.overlays"
    elif [ "${BOARD_NAME}" = "vim3" ]; then
        DTB_PATH="kvim3.dtb.overlays"
	elif [ "${BOARD_NAME}" = "vim1s" ]; then
        DTB_PATH="kvim1s.dtb.overlays"
    else
        bbwarn "This is not khadas board,skipping overlays installation."
        return
    fi

    mkdir -p ${D}/boot/dtb/amlogic/${DTB_PATH}/

    for overlay in ${S}/overlays/${BOARD_NAME}/*.dtbo; do
        install -m 0755 ${overlay} ${D}/boot/dtb/amlogic/${DTB_PATH}/
    done

    install -m 0755 ${S}/overlays/${BOARD_NAME}/k${BOARD_NAME}.dtb.overlay.env ${D}/boot/dtb/amlogic/

    install -d ${D}/boot/	
    install -m 0755 ${S}/uEnv.txt ${D}/boot/

    install -d ${D}${bindir}
	install -m 0755 ${S}/fan.sh ${D}${bindir}
    install -m 0755 ${S}/fan_setup.sh ${D}${bindir}

	if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
        install -D -m 0644 ${WORKDIR}/khadas-custom.service ${D}${systemd_unitdir}/system/khadas-custom.service
    fi
	install -m 0755 ${S}/khadas-custom.sh ${D}${bindir}

}

SYSTEMD_SERVICE:${PN} = " khadas-custom.service "

FILES:${PN} += " /boot/* "


