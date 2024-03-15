SUMMARY = "amlog wrapper of dbus"
LICENSE = "CLOSED"

DEPENDS+="systemd"

SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

do_compile(){
    cd ${S}
    oe_runmake BUILDDIR=${B}
}

do_install() {
    install -d ${D}${libdir} ${D}${includedir}
    cp --no-preserve=ownership -af ${S}/*.h ${D}${includedir}
    cp --no-preserve=ownership -af ${B}/*.so.0 ${B}/*.so ${D}${libdir}
}

