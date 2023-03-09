DESCRIPTION = "Amlogic H264/H265 encode plugin"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad gstreamer1.0-libav"
DEPENDS += "libion"
DEPENDS += "libmultienc"

inherit autotools pkgconfig

SRCREV ?="${AUTOREV}"

do_configure[noexec] = "1"

EXTRA_OEMAKE = "TARGET_DIR=${D} STAGING_DIR=${D} DESTDIR=${D} PKG_CONFIG=${STAGING_BINDIR_NATIVE}/pkg-config"

do_compile(){
    oe_runmake -C ${S} ${EXTRA_OEMAKE} all
}

do_install() {
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}${libdir}/gstreamer-1.0/
    install -D -m 0755 ${S}/libgstamlvenc.so ${D}${libdir}/gstreamer-1.0/
}

FILES_${PN} = " ${libdir}/* "
FILES_${PN}-dev = " ${includedir}/* \
                    /usr/lib/pkgconfig/* \
    "
