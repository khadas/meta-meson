SUMMARY = "amlogic npu libnnsdk"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "linux-meson"

#SRCREV ?="${AUTOREV}"

do_populate_lic[noexec] = "1"
do_configure[noexec] = "1"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

do_install() {
    install -d ${D}/usr/include
    install -d ${D}/usr/include/utils
    install -d ${D}/usr/include/interface
    install -d ${D}/usr/include/custom/ops
    install -d ${D}/usr/include/internal
    install -d ${D}/usr/include/ops
    install -d ${D}/usr/include/post
    install -d ${D}/usr/include/quantization
    install -m 0644 -D ${S}/nnsdk/include/* ${D}/usr/include
    find ${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/applib/ovxinc/include/ -type f -name 'vsi_nn*.h' -exec install -m 0644 {} ${D}/usr/include/ \;
    find ${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/applib/ovxinc/include/utils/ -type f -exec install -m 0644 {} ${D}/usr/include/utils/ \;
    find ${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/applib/ovxinc/include/custom/ -type f -exec bash -c 'install -m 0644 {} ${D}/usr/include/custom/$(echo {} | sed "s|^${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/applib/ovxinc/include/custom/||")' \;
    find ${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/applib/ovxinc/include/internal/ -type f -exec install -m 0644 {} ${D}/usr/include/internal/ \;
    find ${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/applib/ovxinc/include/interface/ -type f -exec install -m 0644 {} ${D}/usr/include/interface/ \;
    find ${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/applib/ovxinc/include/ops/ -type f -exec install -m 0644 {} ${D}/usr/include/ops/ \;
    find ${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/applib/ovxinc/include/post/ -type f -exec install -m 0644 {} ${D}/usr/include/post/ \;
    find ${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/applib/ovxinc/include/quantization/ -type f -exec install -m 0644 {} ${D}/usr/include/quantization/ \;
    find ${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/sdk/inc/VX/ -type d -exec bash -c 'mkdir -p ${D}/usr/include/VX/$(echo {} | sed "s|^${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/sdk/inc/VX/||")' \;
    find ${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/sdk/inc/VX/ -type f -exec bash -c 'install -m 0644 {} ${D}/usr/include/VX/$(echo {} | sed "s|^${S}/../../../hardware/aml-5.4/amlogic/npu/nanoq/sdk/inc/VX/||")' \;

    install -d ${D}/usr/lib
    if [ "${HOST_ARCH}" = "aarch64" ]; then
        install -m 0644 -D ${S}/nnsdk/linux/yocto/lib64/*.so ${D}/usr/lib
    fi
}

FILES:${PN} += "/usr/lib/*"
