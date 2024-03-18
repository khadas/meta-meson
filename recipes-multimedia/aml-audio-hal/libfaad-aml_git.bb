DESCRIPTION = "Amlogic Version of libfaad2"
PR = "r0"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

inherit cmake pkgconfig
DEPENDS += "liblog aml-amaudioutils"

#SRCREV = "${AUTOREV}"
do_package_qa[noexec] = "1"

S="${WORKDIR}/git"

FILES:${PN} = "${libdir}/* ${bindir}/*"
#No Install header files as it will conflict with upstream libfaad2
FILES:${PN}-dev = "${includedir}/* "

INSANE_SKIP:${PN}-dev = "dev-so"
