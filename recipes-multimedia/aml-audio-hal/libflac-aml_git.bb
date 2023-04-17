DESCRIPTION = "Amlogic Version of libflac"
PR = "r0"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

inherit cmake pkgconfig
DEPENDS += "liblog aml-amaudioutils"

SRCREV = "${AUTOREV}"
do_package_qa[noexec] = "1"

S="${WORKDIR}/git"

FILES_${PN} = "${libdir}/* ${bindir}/*"
#No Install header files as it will conflict with upstream libflac
FILES_${PN}-dev = "${includedir}/* "

INSANE_SKIP_${PN}-dev = "dev-so"
