SUMMARY = "user add"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

inherit useradd

SRC_URI = "file://file1"

S = "${WORKDIR}"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system -d / -M --shell /bin/nologin --user-group session;"
USERADD_PARAM:${PN} += "--system -d / -M --shell /bin/nologin --groups video,audio,input,disk,session --user-group system"

do_install () {
    install -d -m 755 ${D}${datadir}/
    install -p -m 644 file1 ${D}${datadir}/
}

FILES_${PN} = "${datadir}/*"
