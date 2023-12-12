SUMMARY = "aml libcve library and sample application"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "libion"
LIBION_SRC_DIR = "${COREBASE}/../aml-comp/hardware/aml-5.4/amlogic/libion"

SRCREV ?="${AUTOREV}"

do_configure[noexec] = "1"

EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D}  EXTRA_CFLAGS+='-I${STAGING_INCDIR} -Wno-unused-variable -Wno-format-extra-args -Wno-unused-result' EXTRA_LDFLAGS+='-L${STAGING_LIBDIR}'"

# copy ion related headers from libion source directory that this package needs
do_prepare_headers () {
    install -d ${STAGING_INCDIR}/ion
    install -d ${STAGING_INCDIR}/linux

    install -m 0644 -D ${LIBION_SRC_DIR}/ion_4.12.h ${STAGING_INCDIR}/linux
    install -m 0644 -D ${LIBION_SRC_DIR}/kernel-headers/linux/ion.h ${STAGING_INCDIR}/linux
    install -m 0644 -D ${LIBION_SRC_DIR}/include/ion/ion.h ${STAGING_INCDIR}/ion
}

do_compile () {
    oe_runmake -C ${S} ${EXTRA_OEMAKE}
}

do_install () {
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -m 0644 -D ${B}/libcve.so ${D}${libdir}
    install -m 0755 -D ${B}/sample-cve ${D}${bindir}
}

do_clean () {
    oe_runmake -C ${S} clean
}

FILES_${PN} = " ${libdir}/* ${bindir}/*"
INSANE_SKIP_${PN} = "dev-so"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

addtask do_prepare_headers before do_compile after do_prepare_recipe_sysroot
