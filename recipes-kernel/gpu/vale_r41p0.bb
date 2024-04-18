inherit module

SUMMARY = "Arm g310(vale) graphic driver"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://t83x/kernel/license.txt;md5=13e14ae1bd7ad5bff731bba4a31bb510"
include gpu.inc

#SRCREV = "${AUTOREV}"
VER = "r41p0"

PROVIDES += "virtual/gpu"
RPROVIDES:${PN} += "gpu"
GPU_ARCH = "valhall"
GPU_DRV_SRC = "${S}/${GPU_ARCH}/${VER}/kernel/drivers"
GPU_LOW_MEM ?= "0"

do_configure[noexec] = "1"

do_install() {
    GPUDIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/arm/gpu
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    mkdir -p ${GPUDIR}
    install -m 0666 ${GPU_DRV_SRC}/gpu/arm/midgard/mali_kbase.ko ${GPUDIR}/mali.ko
    install -m 0666 ${GPU_DRV_SRC}/base/arm/protected_memory_allocator/protected_memory_allocator.ko ${GPUDIR}/protected_memory_allocator.ko
}

FILES:${PN} = "mali.ko"
FILES:${PN} += "protected_memory_allocator.ko"
# Header file provided by a separate package
DEPENDS += ""

S = "${WORKDIR}/git"
EXTRA_OEMAKE='-C ${STAGING_KERNEL_DIR} M=${GPU_DRV_SRC} \
              EXTRA_CFLAGS="-DCONFIG_MALI_DEVFREQ -DCONFIG_MALI_GATOR_SUPPORT -DCONFIG_MALI_CSF_SUPPORT \
              -I${S}/${GPU_ARCH}/${VER}/kernel/include -DCONFIG_MALI_LOW_MEM=${GPU_LOW_MEM}" \
              CONFIG_MALI_MIDGARD=m CONFIG_MALI_DEVFREQ=y CONFIG_MALI_GATOR_SUPPORT=y CONFIG_MALI_REAL_HW=y \
              CONFIG_MALI_CSF_SUPPORT=y CONFIG_MALI_PROTECTED_MEMORY_ALLOCATOR=y CONFIG_MALI_PLATFORM_NAME="devicetree" modules'

