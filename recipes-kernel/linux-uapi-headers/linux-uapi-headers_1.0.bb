SUMMARY = "install uapi headers before compile"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRCREV ?= "${AUTOREV}"

do_compile[noexec] = "1"
do_configure[depends] += "virtual/kernel:do_shared_workdir"

do_install() {
    install -m 0755 -d ${D}${includedir}/linux/amlogic

    # install amlogic customized headers
    aml_uapi_path="${STAGING_KERNEL_DIR}/common_drivers/include/uapi/amlogic"
    if [ ! -d ${aml_uapi_path} ]; then
        aml_uapi_path="${STAGING_KERNEL_DIR}/include/uapi/linux/amlogic"
    fi
    cp -af $aml_uapi_path ${D}${includedir}/linux/

    # install extra headers
    uapi_path="${STAGING_KERNEL_DIR}/include/uapi/linux/"

    for f in dma-heap.h ion.h; do
        if [ -f ${uapi_path}${f} ]; then
            if [ ! -e ${STAGING_INCDIR}/linux/${f} ]; then
                install -m 0644 ${uapi_path}${f} ${D}${includedir}/linux
            fi
        fi
    done
}

FILES:${PN}-dev = "${includedir}/linux/*"

