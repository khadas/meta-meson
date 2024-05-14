include westeros.inc

SUMMARY = "This receipe compiles the westeros compositor simple-buffer component"

LICENSE = "Apache-2.0"

# S = "${WORKDIR}/git/simplebuffer"

LICENSE_LOCATION = "${S}/../LICENSE"

DEPENDS = "wayland glib-2.0 wayland-native"

inherit autotools pkgconfig

do_compile:prepend() {
   export SCANNER_TOOL=${STAGING_BINDIR_NATIVE}/wayland-scanner
   oe_runmake -C ${S}/protocol
}
