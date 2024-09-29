SUMMARY = "khadas-custom"
SECTION = "khadas-custom"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302" 
 
S = "${WORKDIR}"
 
SRC_URI = "\
        file://ext_board.dtbo \
        file://fb-1024×600.dtbo \
        file://fix-ts050-mipi-console.dtbo \
        file://i2cm_a.dtbo \
        file://i2cm_f.dtbo \
        file://i2s.dtbo \
        file://onewire.dtbo \
        file://pwm_f.dtbo \
        file://spdifout.dtbo \
        file://spi0.dtbo \
        file://uart_e.dtbo \
        file://vim4n-imx415.dtbo \
        file://vim4n-os08a10.dtbo \
        file://kvim4.dtb.overlay.env \
        file://uEnv.txt \
        file://fan.sh \
        file://fan_setup.sh \
		"

do_install() {

	install -d ${D}/boot/
	mkdir -p ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/ext_board.dtbo                 ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/fb-1024×600.dtbo               ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/fix-ts050-mipi-console.dtbo    ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/i2cm_a.dtbo                    ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/i2cm_f.dtbo                    ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/i2s.dtbo                       ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/onewire.dtbo                   ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/pwm_f.dtbo                     ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/spdifout.dtbo                  ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/spi0.dtbo                      ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/uart_e.dtbo                    ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/vim4n-imx415.dtbo              ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/vim4n-os08a10.dtbo             ${D}/boot/dtb/amlogic/kvim4.dtb.overlays/
    install -m 0755 ${S}/kvim4.dtb.overlay.env          ${D}/boot/dtb/amlogic

    install -d ${D}/boot/	
    install -m 0755 ${S}/uEnv.txt ${D}/boot/

    install -d ${D}${bindir}
	install -m 0755 ${S}/fan.sh ${D}${bindir}
    install -m 0755 ${S}/fan_setup.sh ${D}${bindir}

}

FILES:${PN} += " /boot/* "


