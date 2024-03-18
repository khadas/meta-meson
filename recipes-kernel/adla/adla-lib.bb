SUMMARY = "amlogic adla library"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "linux-meson"

#SRCREV ?="${AUTOREV}"

do_populate_lic[noexec] = "1"
do_configure[noexec] = "1"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

do_install() {
    install -d ${D}/usr/lib

    if [ "${HOST_ARCH}" = "aarch64" ]; then
        install -m 0644 -D ${S}/libraryso/yocto/lib64/*.so ${D}/usr/lib
    else
        install -m 0644 -D ${S}/libraryso/yocto/lib/*.so ${D}/usr/lib
    fi
}

FILES:${PN} += "/usr/lib/*"
