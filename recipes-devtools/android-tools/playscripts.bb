SUMMARY  = "scripts for play"
DESCRIPTION = "Some scripts for configure audio decoders and alsa conf."
LICENSE  = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI  = "\
  file://S89usbgadget \
"
S = "${WORKDIR}"


do_install() {
    install -m 0755 ${WORKDIR}/S89usbgadget ${D}${sysconfdir}/rc5.d/
}
