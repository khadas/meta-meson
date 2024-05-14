include westeros.inc

SUMMARY = "This receipe compiles the westeros compositor simple-shell component"

LICENSE = "Apache-2.0"

# S = "${WORKDIR}/git/simpleshell"

DEPENDS = "wayland glib-2.0 wayland-native"

LICENSE_LOCATION = "${S}/../LICENSE"

inherit autotools pkgconfig

do_compile:prepend() {
   export SCANNER_TOOL=${STAGING_BINDIR_NATIVE}/wayland-scanner
   oe_runmake -C ${S}/protocol
}
