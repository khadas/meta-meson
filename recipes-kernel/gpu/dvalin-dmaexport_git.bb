inherit module

SUMMARY = "Arm G31(dvalin) graphic driver"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://t83x/kernel/license.txt;md5=13e14ae1bd7ad5bff731bba4a31bb510"
include gpu.inc

SRCREV = "${AUTOREV}"
VER = "r25p0"
PV = "${VER}git${SRCPV}"

GPU_ARCH = "bifrost"
GPU_DMA_SRC = "${S}/${GPU_ARCH}/${VER}/kernel/drivers/base/dma_buf_test_exporter"

do_configure[noexec] = "1"

do_install() {
    GPUDIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/arm/gpu
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    mkdir -p ${GPUDIR}
    install -m 0666 ${GPU_DMA_SRC}/dma-buf-test-exporter.ko ${GPUDIR}/dma-buf-test-exporter.ko
}

FILES_${PN} = "dma-buf-test-exporter.ko"
# Header file provided by a separate package
DEPENDS += ""
KERNEL_MODULE_AUTOLOAD += "dma-buf-test-exporter"

S = "${WORKDIR}/git"
EXTRA_OEMAKE='-C ${GPU_DMA_SRC} KDIR=${STAGING_KERNEL_DIR} \
              EXTRA_CFLAGS="" \
             '
