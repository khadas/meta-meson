DESCRIPTION = "TinyALSA is a small library to interface with ALSA in \
the Linux kernel. It is a lightweight alternative to libasound."
HOMEPAGE = "https://github.com/tinyalsa/tinyalsa"
SECTION = "libs/multimedia"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://NOTICE;md5=dbdefe400d894b510a9de14813181d0b"

S = "${WORKDIR}/git"
SRCREV = "df11091086b56e5fb71887f2fa320e1d2ffeff58"
SRC_URI = "git://github.com/tinyalsa/tinyalsa.git;protocol=https;branch=master;"
SRC_URI += " \
            file://0001-disable-doxygen-usage.patch \
            file://0002-interval.h-add-missing-header.patch \
            file://0003-add-pcm-ioctl.patch \
            file://0004-pcm-rw-return-fix.patch \
            file://0005-fix-example-FORTIFY_SOURCE-error.patch \
            file://0006-tinymix-enum-print.patch \
            file://0007-tinycap-function-error-fix.patch \
            "

do_configure() {
    :
}

do_compile() {
    oe_runmake CC='${CC}' LD='${CC}' AR='${AR}'
}

do_install() {
    oe_runmake install \
        PREFIX="${prefix}" DESTDIR="${D}" INCDIR="${includedir}/tinyalsa" \
        LIBDIR="${libdir}" BINDIR="${bindir}" MANDIR="${mandir}"
}

PACKAGES =+ "${PN}-tools"
FILES:${PN}-tools = "${bindir}/*"
