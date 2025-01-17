inherit kernel_meson
require recipes-kernel/linux/linux-yocto.inc
require recipes-kernel/linux/linux-yocto_amlogic.inc

do_copy_dtb() {
    if [ -d ${B}/common_drivers/arch/${ARCH}/boot/dts/amlogic ]; then
        ADDON_PATH="common_drivers/"
    fi
    cp -f ${B}/${ADDON_PATH}arch/${ARCH}/boot/dts/amlogic/*.dtb ${B}/arch/${ARCH}/boot/
    if [ $(echo "${KERNEL_DEVICETREE}" | wc -w ) -gt 1 ]; then
        dtbTool -o ${B}/arch/${ARCH}/boot/multi.dtb -p ${B}/scripts/dtc/ ${B}/${ADDON_PATH}arch/${ARCH}/boot/dts/amlogic/
    else
        install -m 0644 ${B}/${ADDON_PATH}arch/${ARCH}/boot/dts/amlogic/${KERNEL_DEVICETREE} ${B}/arch/${ARCH}/boot/multi.dtb
    fi
}

addtask copy_dtb before do_install after do_compile
#addtask install_headers after do_install before do_deploy

do_deploy:append () {
    DTB_PATH=`get_real_dtb_path_in_kernel multi.dtb`
    install -m 0644 ${DTB_PATH} ${DEPLOYDIR}/dtb.img

    echo "dtb.img will be gziped"

    mv ${DEPLOYDIR}/dtb.img ${DEPLOYDIR}/dtb.img.orig
    gzip -c ${DEPLOYDIR}/dtb.img.orig > ${DEPLOYDIR}/dtb.img
    rm -fv ${DEPLOYDIR}/dtb.img.orig
}

do_install:append () {
    oe_runmake -C ${B} CC="${KERNEL_CC}" headers_install INSTALL_HDR_PATH=${D}/usr/include/linux-meson/
}

sysroot_stage_all:append() {
    sysroot_stage_dir ${D}/usr/include/linux-meson ${SYSROOT_DESTDIR}/usr/include/linux-meson

}

do_create_lib32_symlink() {
    if [ -d ${STAGING_DIR}/lib32-${MACHINE} ]; then
      rm -rf ${STAGING_DIR}/lib32-${MACHINE}/usr/include/linux-meson
      ln -sf ${STAGING_INCDIR}/linux-meson ${STAGING_DIR}/lib32-${MACHINE}/usr/include/linux-meson
    fi
}

addtask create_lib32_symlink after do_populate_sysroot before do_package

FILES:${PN}-dev += " /usr/include/linux-meson/* "
