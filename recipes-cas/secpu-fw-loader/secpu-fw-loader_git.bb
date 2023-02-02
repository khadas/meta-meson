SUMMARY = "CA/TA of secpu fw loader"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/secpu_fw_loader;protocol=${AML_GIT_PROTOCOL};branch=amlogic-dev"
#SRC_URI = "git://${COREBASE}/../aml-comp/vendor/amlogic/secpu_fw_loader;protocal=file;rev=refs/remotes/amlogic/amlogic-dev"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/secpu_fw_loader')}"

SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"
DEPENDS += "optee-userspace"

inherit autotools pkgconfig

do_compile() {
   ${MAKE} -C ${S}/lib clean O=../out/lib
   ${MAKE} -C ${S}/ca clean O=../out/ca

   ${MAKE} -C ${S}/lib all O=../out/lib
   ${MAKE} -C ${S}/ca all O=../out/ca
}

do_install() {
    # install headers
    install -d -m 0755 ${D}/lib/optee_armtz
    install -d -m 0755 ${D}${libdir}
    install -d -m 0755 ${D}${bindir}

    # Tryng to replace "v3.8" with "v3".
    # If it fails to do replacement, the outout will still be original string.
    TDK_VERSION_NEW=`echo ${TDK_VERSION} | sed 's/v3.8/v3/'`

    install -D -m 0755 ${S}/out/ca/tee_secpu_fw_load ${D}${bindir}
    install -D -m 0755 ${S}/out/lib/*.so ${D}${libdir}
    install -D -m 0755 ${S}/ta/${TDK_VERSION_NEW}/*.ta ${D}/lib/optee_armtz/
}

FILES:${PN} += "/lib/optee_armtz/* ${bindir}/*"
FILES:${PN} += "${libdir}/*"
FILES:${PN}-dev = " "
INSANE_SKIP:${PN} = "ldflags dev-so dev-elf"
INSANE_SKIP:${PN}-dev = "ldflags dev-so dev-elf"
