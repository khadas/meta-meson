DESCRIPTION = "Meson mode policy"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRCREV ?= "${AUTOREV}"

DEPENDS = "linux-uapi-headers zlib libbsd"

EXTRA_OECMAKE += "-DLIB_DIR=${libdir} -DBIN_DIR=${bindir} -DINCLUDE_DIR=${includedir}"

inherit cmake

FILES:${PN}-dev = ""
INSANE_SKIP:${PN} = "installed-vs-shipped"
