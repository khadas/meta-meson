FILESEXTRAPATHS:prepend := "${THISDIR}/files/:"

SRCREV = "ab544262da956499abbb8b9efa565c962b4c8e3b"
SRC_URI += " \
            file://0001-For-lack-of-pandoc-don-t-make-fscryptctl.1.patch \
            file://0002-Add-add-key-by-keyid.patch \
           "

do_install:prepend() {
    export PREFIX=""
    export BINDIR=""
}
