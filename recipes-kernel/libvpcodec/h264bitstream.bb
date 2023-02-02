SUMMARY = "h264bitstream"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

inherit autotools pkgconfig

S = "${WORKDIR}/git"
SRCREV = "34f3c58afa3c47b6cf0a49308a68cbf89c5e0bff"
SRC_URI = "git://github.com/aizvorski/h264bitstream"

FILES:${PN} = " ${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/*"
INSANE_SKIP:${PN} = "dev-so"
INSANE_SKIP:${PN}-dev = "dev-elf dev-so"
