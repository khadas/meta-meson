SUMMARY = "amlogic libgdc framework"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRCREV ?="${AUTOREV}"

do_configure[noexec] = "1"

DEPENDS = "libion"
RDEPENDS:${PN} += "libion"

do_compile() {
    if [ "${TARGET_ARCH}" = "aarch64" ]; then
        export ARM_TARGET=64
	else
	    export ARM_TARGET=32
    fi
    cd ${S}/
    oe_runmake all
    oe_runmake dewarp_test "AML_DEW_BIT=${ARM_TARGET}"
}

do_install() {
    if [ "${TARGET_ARCH}" = "aarch64" ]; then
        export ARM_TARGET=64
	else
	    export ARM_TARGET=32
    fi
    install -d ${D}${includedir}
    install -d ${D}${libdir}
    install -d ${D}${bindir}

    install -m 0755 -D ${S}/gdc_test ${D}${bindir}
    install -m 0755 -D ${S}/dewarp_test ${D}${bindir}
    install -m 0644 -D ${S}/libgdc.so ${D}${libdir}
    install -m 0644 -D ${S}/dewarp/lib/${ARM_TARGET}/libdewarp.so ${D}${libdir}

    install -m 0644 ${S}/include/gdc/gdc_api.h ${D}${includedir}
    install -m 0644 ${S}/dewarp/dewarp_api.h ${D}${includedir}
}

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

FILES:${PN}-dev = "${includedir} ${libdir}/pkgconfig"
FILES:${PN} += "${libdir}/libgdc*"
FILES:${PN} += "${libdir}/libdewarp*"
