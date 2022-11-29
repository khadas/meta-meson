SUMMARY = "amlogic wfd hdcp"
LICENSE = "CLOSED"
DEPENDS = "optee-userspace bzip2 libxml2 aml-secmem aml-mediahal-sdk"
RDEPENDS_${PN} = "libbz2"

FILESEXTRAPATHS_preppend := "${THISDIR}/files/:"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libmediadrm/libwfd_hdcp-bin')}"

#SRCREV="bb62070629f62c580b32cdfe2cfaa3928611d6f3"
#use head version, ?= conditonal operator can be control revision in external rdk-next.conf like configuration file
SRCREV ?= "${AUTOREV}"

S = "${WORKDIR}/git"

PATCHTOOL="git"

ARM_TARGET="arm.aapcs-linux.hard"
ARM_TARGET_aarch64 ="aarch64.lp64."

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"
#do_compile () {
#}
do_install () {
	mkdir -p ${D}/usr/bin
	mkdir -p ${D}${libdir}
	mkdir -p ${D}/lib/optee_armtz
	mkdir -p ${D}/usr/include/wfd_hdcp

    install -m 0644 ${S}/prebuilt/noarch/ta/${TDK_VERSION}/*.ta ${D}/lib/optee_armtz
    install -m 0644 ${S}/prebuilt/${ARM_TARGET}/libwfd_hdcp.so ${D}${libdir}
    install -m 0755 ${S}/prebuilt/${ARM_TARGET}/hdcp_rx_test ${D}/usr/bin
    install -m 0755 ${S}/prebuilt/${ARM_TARGET}/hdcp_tx_test ${D}/usr/bin
    cp -rf ${S}/prebuilt/noarch/include/* ${D}/usr/include/wfd_hdcp/
}

FILES_${PN} += "/lib/optee_armtz/*"
FILES_${PN} += "${libdir}/*"

FILES_SOLIBSDEV = ""

INSANE_SKIP_${PN} = "ldflags dev-so"
