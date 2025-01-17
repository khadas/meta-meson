inherit module

SUMMARY = "Arm G31(dvalin) graphic driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://t83x/kernel/license.txt;md5=13e14ae1bd7ad5bff731bba4a31bb510"
include gpu.inc

#SRCREV = "${AUTOREV}"
VER = "r44p0"
PV = "${VER}git${SRCPV}"

PROVIDES += "virtual/gpu"
RPROVIDES:${PN} += "gpu"
GPU_ARCH = "bifrost"
GPU_DRV_SRC = "${S}/${GPU_ARCH}/${VER}/kernel/drivers/gpu/arm/midgard"
GPU_LOW_MEM ?= "${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', '1', '0', d)}"

do_configure[noexec] = "1"

do_install() {
    GPUDIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/arm/gpu
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    mkdir -p ${GPUDIR}
    install -m 0666 ${GPU_DRV_SRC}/mali_kbase.ko ${GPUDIR}/mali.ko
}

FILES:${PN} = "mali.ko"
# Header file provided by a separate package
#DEPENDS += "dvalin-dmaexport"
#RDEPENDS:${PN} += "dvalin-dmaexport"

S = "${WORKDIR}/git"
EXTRA_OEMAKE='-C ${STAGING_KERNEL_DIR} M=${GPU_DRV_SRC} \
              EXTRA_CFLAGS="-DCONFIG_MALI_MIDGARD_DVFS -DCONFIG_MALI_GATOR_SUPPORT=y -DCONFIG_MALI_REAL_HW \
              -I${S}/${GPU_ARCH}/${VER}/kernel/include -DCONFIG_MALI_LOW_MEM=${GPU_LOW_MEM}" \
              CONFIG_MALI_MIDGARD=m CONFIG_MALI_MIDGARD_DVFS=y CONFIG_MALI_GATOR_SUPPORT=y CONFIG_MALI_PLATFORM_NAME="devicetree" CONFIG_MALI_REAL_HW=y modules'

