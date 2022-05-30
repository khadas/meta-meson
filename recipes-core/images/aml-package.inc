DEPENDS_append = " aml-img-packer-native"
DEPENDS_append_onepass = " core-image-minimal"
DEPENDS_append = " vendor-image"
DEPENDS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'swupdate', ' aml-swupdate-native', '', d)}"
DEPENDS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'scs', ' aml-linux-scs-native', '', d)}"

SOC_FAMILY = "TBD"
SOC_FAMILY_gxl = "gxl"
SOC_FAMILY_axg = "axg"
SOC_FAMILY_txlx = "txlx"
SOC_FAMILY_g12a = "g12a"
SOC_FAMILY_g12b = "g12b"
SOC_FAMILY_tm2 = "tm2"
SOC_FAMILY_sc2= "sc2"
SOC_FAMILY_t7 = "t7"
SOC_FAMILY_p1 = "p1"
SOC_FAMILY_s4 = "s4"
SOC_FAMILY_t5d = "t5d"
SOC_FAMILY_t5w = "t5w"
SOC_FAMILY_t3 = "t3"
SOC_FAMILY_a213y = "a213y"

PACK_CONF = "${@bb.utils.contains('DISTRO_FEATURES','absystem','aml_upgrade_package_ab.conf','aml_upgrade_package.conf', d)}"

padding_to_multipleOf4K_for_img2simg() {
    local add_size=0
    local file_size=$(stat -L -c "%s" $1)

    echo "$1 file_size $file_size"

    local remain=$(expr $file_size % 4096)
    if [ $remain -ne 0 ]; then
        add_size=$(expr 4096 - $remain)
        echo "$1 add_size $add_size"
        if [ $add_size -gt 0 ];then
            echo "$1 padding $add_size bytes for img2simg"
            truncate --size="+$add_size" $1
        fi
    fi
}

do_aml_pack() {
       if ${@bb.utils.contains('DISTRO_FEATURES','onepass','false','true',d)}; then
            dir64b=`echo ${DEPLOY_DIR_IMAGE} | sed -r 's/_32b//'`
              #if [ -e $dir64b ];then
              #    cp $dir64b/boot.img ${DEPLOY_DIR_IMAGE}
              #   cp $dir64b/dtb.img ${DEPLOY_DIR_IMAGE}/
              #    cp $dir64b/u-boot* ${DEPLOY_DIR_IMAGE}
              #fi
        fi

    if [ -n "$(find ${DEPLOY_DIR_IMAGE}/dtb.img -size +200k)" ]; then
        echo "dtb.img > 200k will be gziped"
        mv ${DEPLOY_DIR_IMAGE}/dtb.img ${DEPLOY_DIR_IMAGE}/dtb.img.orig
        gzip -c ${DEPLOY_DIR_IMAGE}/dtb.img.orig > ${DEPLOY_DIR_IMAGE}/dtb.img
    fi

    cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-img-packer/aml_* ${DEPLOY_DIR_IMAGE}/
    cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-img-packer/img2simg ${DEPLOY_DIR_IMAGE}/
    cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-img-packer/ext2simg ${DEPLOY_DIR_IMAGE}/
    cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-img-packer/res_packer ${DEPLOY_DIR_IMAGE}/
    cp -rf ${STAGING_DIR_NATIVE}${bindir}/aml-img-packer/${SOC_FAMILY}/* ${DEPLOY_DIR_IMAGE}/
    #Handle vbmeta partition in case AVB feature is not enabled.
    if ${@bb.utils.contains('DISTRO_FEATURES', 'AVB', 'false', 'true', d)}; then
        sed -i '/vbmeta/ d' ${DEPLOY_DIR_IMAGE}/${PACK_CONF}
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'recovery', 'false', 'true', d)}; then
        sed -i '/recovery/ d' ${DEPLOY_DIR_IMAGE}/${PACK_CONF}
    else
        if ${@bb.utils.contains('DISTRO_FEATURES', 'absystem', 'true', 'false', d)}; then
            sed -i -e 's/sub_type=\"recovery\"/sub_type=\"recovery_a\"/' ${DEPLOY_DIR_IMAGE}/${PACK_CONF}
        fi
    fi

    ${DEPLOY_DIR_IMAGE}/res_packer -r ${DEPLOY_DIR_IMAGE}/logo_img_files ${DEPLOY_DIR_IMAGE}/logo.img

if ${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'true', 'false', d)}; then
    cp ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ubi ${DEPLOY_DIR_IMAGE}/rootfs.ubi
    cp ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ubi ${DEPLOY_DIR_IMAGE}/vendor.ubi
    ${DEPLOY_DIR_IMAGE}/aml_image_v2_packer_new -r ${DEPLOY_DIR_IMAGE}/aml_upgrade_package_nand.conf ${DEPLOY_DIR_IMAGE} ${DEPLOY_DIR_IMAGE}/aml_upgrade_package.img
else
    padding_to_multipleOf4K_for_img2simg ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ext4
    ${DEPLOY_DIR_IMAGE}/img2simg ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ext4 ${DEPLOY_DIR_IMAGE}/rootfs.ext4.img2simg
    ${DEPLOY_DIR_IMAGE}/ext2simg ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ext4 ${DEPLOY_DIR_IMAGE}/system.img

    if [ -f ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ext4 ];then
        padding_to_multipleOf4K_for_img2simg ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ext4
        ${DEPLOY_DIR_IMAGE}/img2simg ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ext4 ${DEPLOY_DIR_IMAGE}/vendor.ext4.img2simg
    fi

    if [ "${SOC_FAMILY}" = "p1" ];then
        cp ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ext4 ${DEPLOY_DIR_IMAGE}/rootfs.ext4.img2simg
        cp ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ext4 ${DEPLOY_DIR_IMAGE}/vendor.ext4.img2simg
    fi

    ${DEPLOY_DIR_IMAGE}/aml_image_v2_packer_new -r ${DEPLOY_DIR_IMAGE}/${PACK_CONF} ${DEPLOY_DIR_IMAGE} ${DEPLOY_DIR_IMAGE}/aml_upgrade_package.img

    #sign image
    if ${@bb.utils.contains('DISTRO_FEATURES', 'scs', 'true', 'false', d)}; then
        cp -rf ${STAGING_DIR_NATIVE}${bindir}/aml-linux-scs/device-keys ${DEPLOY_DIR_IMAGE}/
        cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-linux-scs/fw_arb.cfg ${DEPLOY_DIR_IMAGE}/

        if [ "${AML_SCS_SIGN_TOOL}" = "" ];then
            AML_SCS_SIGN=${STAGING_DIR_NATIVE}${bindir}/aml-linux-scs/amlogic_scs_sign_whole_pkg.bash
        else
            AML_SCS_SIGN=${AML_SCS_SIGN_TOOL}
        fi

        UBOOT_TYPE="${UBOOT_MACHINE}"
        ${AML_SCS_SIGN} --soc ${CHIPSET_NAME} \
            --aml_key ${DEPLOY_DIR_IMAGE}/device-keys \
            --fw_arb_cfg ${DEPLOY_DIR_IMAGE}/fw_arb.cfg \
            --aml_img ${DEPLOY_DIR_IMAGE}/aml_upgrade_package.img \
            --bootloader_zip ${DEPLOY_DIR_IMAGE}/${UBOOT_TYPE%_config}-u-boot.aml.zip \
            --output ${DEPLOY_DIR_IMAGE}/aml_upgrade_package_enc.img
    fi

    #create software.swu
    if ${@bb.utils.contains('DISTRO_FEATURES', 'swupdate', 'true', 'false', d)}; then
        cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/swupdate-priv.pem ${DEPLOY_DIR_IMAGE}/
        cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/update.sh ${DEPLOY_DIR_IMAGE}/
        cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/sw_package_create.sh ${DEPLOY_DIR_IMAGE}/
        cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-description-emmc ${DEPLOY_DIR_IMAGE}/sw-description
        cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-package-filelist-emmc ${DEPLOY_DIR_IMAGE}/sw-package-filelist

        sed 's@boardname@${MACHINE_ARCH}@' -i ${DEPLOY_DIR_IMAGE}/sw-description
        sed 's@_lib32_@_@' -i ${DEPLOY_DIR_IMAGE}/sw-description

        ${DEPLOY_DIR_IMAGE}/sw_package_create.sh ${DEPLOY_DIR_IMAGE}
    fi
fi
}

do_aml_pack[depends] += "core-image-minimal:do_image_complete "
do_aml_pack[depends] += "vendor-image:do_image_complete "
do_aml_pack[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'recovery', bb.utils.contains('MULTTILIBS', 'multilib:lib32', ' lib32-recovery-image:do_image_complete', ' recovery-image:do_image_complete', d), '', d)}"
do_aml_pack[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'AVB', ' vbmeta-image:do_compile', '', d)}"
addtask aml_pack after do_image_complete before do_build
do_aml_pack[nostamp] = "1"
