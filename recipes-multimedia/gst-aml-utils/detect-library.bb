DESCRIPTION = "Amlogic face detect with NPU"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += " npu-common nndemo-library liblog"
RDEPENDS:${PN} += " npu-common nndemo-library liblog"

inherit autotools pkgconfig


SRCREV ?="${AUTOREV}"

do_configure[noexec] = "1"

EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D} PKG_CONFIG=${STAGING_BINDIR_NATIVE}/pkg-config"

do_compile(){
    oe_runmake -C ${S} ${EXTRA_OEMAKE} all
}

CONVERT_SHELL_DIR = "${S}/nn_input"
adla_driver_present = "${@bb.utils.contains('MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS', 'adla-driver', 'yes', 'no', d)}"

do_install:prepend() {
    install -d -m 0755 ${B}/jpeg_to_bin
    if [ "${adla_driver_present}" = "yes" ]; then
            python3 ${CONVERT_SHELL_DIR}/save_bin.py ${CONVERT_SHELL_DIR}/space_shuttle_416x416.jpg i8 ${B}/jpeg_to_bin
    else
            python3 ${CONVERT_SHELL_DIR}/save_bin.py ${CONVERT_SHELL_DIR}/space_shuttle_416x416.jpg u8 ${B}/jpeg_to_bin
    fi
}

do_install() {
    install -d -m 0755 ${D}${includedir}
    install -d -m 0755 ${D}${libdir}

    install -D -m 0644 ${S}/inc/*.h ${D}${includedir}/
    install -D -m 0644 ${B}/*.so ${D}${libdir}/

    #if [ "${HOST_ARCH}" = "aarch64" ]; then
    #    install -m 0644 -D ${S}/lib/lib64/*.so ${D}/usr/lib
    #fi
    mkdir -p ${D}/etc/nn_data
    mkdir -p ${D}/etc/nn_input/jpeg
    mkdir -p ${D}/etc/nn_input/bin
    cp -rf ${S}/nn_data ${D}/etc/
    cp -rf ${S}/nn_input/* ${D}/etc/nn_input/jpeg
    cp -rf ${B}/jpeg_to_bin/* ${D}/etc/nn_input/bin

    chmod -R 755 ${D}/etc/nn_data
    chmod -R 755 ${D}/etc/nn_input
}

FILES:${PN} = " ${libdir}/* /etc/* "
FILES:${PN}-dev = " ${includedir}/* \
                    /usr/lib/pkgconfig/* \
    "
INSANE_SKIP:${PN} += "file-rdeps"
