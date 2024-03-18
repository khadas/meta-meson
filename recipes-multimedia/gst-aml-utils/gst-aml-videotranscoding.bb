SUMMARY = "amlogic gstreamer media player"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += " gstreamer1.0 gstreamer1.0-plugins-base "

RDEPENDS:${PN} = " "


#SRCREV ?="${AUTOREV}"
PV = "${SRCPV}"

#S = "${WORKDIR}/git/"
inherit autotools pkgconfig

do_configure[noexec] = "1"

EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D} PKG_CONFIG=${STAGING_BINDIR_NATIVE}/pkg-config"

do_compile(){
    oe_runmake -C ${S} ${EXTRA_OEMAKE} all
}

do_install() {
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}${includedir}

    install -D -m 0755 ${S}/inc/*.h ${D}${includedir}/
    install -D -m 0644 ${B}/*.so ${D}${libdir}/

}


FILES:${PN} = " ${libdir}/* "
FILES:${PN}-dev = " ${includedir}/* \
                    /usr/lib/pkgconfig/* \
    "
INSANE_SKIP:${PN} += "file-rdeps"