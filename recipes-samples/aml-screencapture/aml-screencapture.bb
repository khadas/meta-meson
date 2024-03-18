SUMMARY  = "Amlogic screencapture source reference"
LICENSE = "CLOSED"

#SRCREV ?="${AUTOREV}"
PV = "${@bb.parse.vars_from_file(d.getVar('FILE'),d)[1] or '1.0'}"
PN = "${@bb.parse.vars_from_file(d.getVar('FILE'),d)[0] or 'aml-screencapture'}"

EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D} EXTRA_CFLAGS+='-Wno-unused-result -Wno-unused-variable -Wno-unused-function -Os'"

do_configure[noexec] = "1"
do_package:qa[noexec] = "1"

do_compile() {
    oe_runmake -C ${S} ${EXTRA_OEMAKE} all
}

do_install() {
    install -d ${D}${bindir}

    install -D -m 0755 ${B}/screencapture ${D}${bindir}/screencapture

}

FILES:${PN} = "${bindir}/* ${libdir}/*"
FILES:${PN}-dev = "${includedir}/*"
