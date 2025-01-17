DESCRIPTION = "VMX Library Install Example"
SECTION = "VMX Library"
LICENSE = "CLOSED"

# The current directory is expected to the location where ace yocto package is untarred
FILESEXTRAPATHS:prepend := "${THISDIR}:"
PV = "1.0"
PR = "r0"
S = "${WORKDIR}"

VMX_LIBPATH = "NotSupport"
VMX_LIBPATH:sc2 = "s905x4"
VMX_LIBPATH:s4 = "s905y4"

do_install() {
    install -d -m 0755 ${D}/usr/lib
    install -D -m 0644 ${S}/${VMX_LIBPATH}/lib/libViewRightWebClientDEV.so ${D}/usr/lib
    install -D -m 0644 ${S}/${VMX_LIBPATH}/lib/libvriptvclientDEV.so ${D}/usr/lib

    install -d -m 0755 ${D}/lib/optee_armtz
    install -D -m 0644 ${S}/${VMX_LIBPATH}/ta/bc2f95bc-14b6-4445-a43c-a1796e7cac31.ta ${D}/lib/optee_armtz
    install -D -m 0644 ${S}/${VMX_LIBPATH}/ta/7a60371e-5af8-426c-be15-113d794238d4.ta ${D}/lib/optee_armtz
    install -D -m 0644 ${S}/${VMX_LIBPATH}/ta/acbe4b66-6e40-46dc-a2fe-a576922cf170.ta ${D}/lib/optee_armtz
    install -D -m 0644 ${S}/${VMX_LIBPATH}/ta/cb4066f7-e18f-4fb9-b6b1-9511bd319ebc.ta ${D}/lib/optee_armtz
}

FILES:${PN} = "${libdir}/* /usr/lib/* /lib/optee_armtz/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
