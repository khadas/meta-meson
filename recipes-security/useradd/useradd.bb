SUMMARY = "user add"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

inherit useradd

do_install[noexec] = "1"
ALLOW_EMPTY:${PN} = "1"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system -d / -M --shell /bin/nologin --user-group session;"
USERADD_PARAM:${PN} += "--system -d / -M --shell /bin/nologin --groups video,audio,input,disk,session --user-group system"
