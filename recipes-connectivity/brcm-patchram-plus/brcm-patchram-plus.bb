DESCRIPTION = "Broadcom Bluetooth patch utility (based on Intel Edison tree)"
SECTION = "connectivity"

SRC_URI = "git://github.com/01org/edison-firmware.git;branch=master;protocol=git;rev=8585a10b3527666b2d35b3dcacffede3ec00cb53"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://brcm_patchram_plus.c;md5=a1a36fbad61f424f16abdabd86d51052"

S = "${WORKDIR}/git/broadcom_cws/bluetooth/firmware"

do_compile() {
        ${CC} ${CFLAGS} ${LDFLAGS} -o brcm_patchram_plus brcm_patchram_plus.c
}

do_install() {
        install -v -d  ${D}/usr/sbin/
        install -m 0755 brcm_patchram_plus ${D}/usr/sbin/
}
