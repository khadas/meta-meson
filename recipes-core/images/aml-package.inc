DEPENDS:append = " aml-img-packer-native"
DEPENDS:append:onepass = " core-image-minimal"
DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'vendor-partition', ' vendor-image', '', d)}"
DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'swupdate', ' aml-swupdate-native', '', d)}"
DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'scs', ' aml-linux-scs-native', '', d)}"
DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', ' aml-linux-sb-native', '', d)}"

SOC_FAMILY = "TBD"
SOC_FAMILY:sc2= "sc2"
SOC_FAMILY:t7 = "t7"
SOC_FAMILY:s4 = "s4"
SOC_FAMILY:t5d = "t5d"
SOC_FAMILY:t5w = "t5w"
SOC_FAMILY:t3 = "t3"
SOC_FAMILY:s1a = "s1a"

PACK_CONF = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'aml_upgrade_package_nand.conf', bb.utils.contains('DISTRO_FEATURES', 'absystem', 'aml_upgrade_package_ab.conf', 'aml_upgrade_package.conf', d), d)}"

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

    cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-img-packer/aml_* ${DEPLOY_DIR_IMAGE}/
    cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-img-packer/img2simg ${DEPLOY_DIR_IMAGE}/
    cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-img-packer/ext2simg ${DEPLOY_DIR_IMAGE}/
    cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-img-packer/res_packer ${DEPLOY_DIR_IMAGE}/
    cp -rf ${STAGING_DIR_NATIVE}${bindir}/aml-img-packer/${SOC_FAMILY}/* ${DEPLOY_DIR_IMAGE}/

    #if system have generate secureboot uboot, just use it.
    test -f ${DEPLOY_DIR_IMAGE}/u-boot.bin.device.signed && sed 's/"u-boot.bin.signed"/"u-boot.bin.device.signed"/' -i ${DEPLOY_DIR_IMAGE}/${PACK_CONF}

    if [ `grep -c "u-boot.bin.device.signed" ${DEPLOY_DIR_IMAGE}/${PACK_CONF}` -ne '0' ];then
        echo "++ aml_image_v2 package with u-boot.bin.device.signed"
        bbwarn "@@ aml_image_v2 package with u-boot.bin.device.signed"
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'AVB', 'true', 'false', d)}; then
        if ${@bb.utils.contains('DISTRO_FEATURES','absystem','true','false', d)};then
            sed -i '$a file="vbmeta.img"           main_type="PARTITION"      sub_type="vbmeta_a"' ${DEPLOY_DIR_IMAGE}/${PACK_CONF}
        else
            sed -i '$a file="vbmeta.img"           main_type="PARTITION"      sub_type="vbmeta"' ${DEPLOY_DIR_IMAGE}/${PACK_CONF}
        fi
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'recovery', 'false', 'true', d)}; then
        sed -i '/recovery/ d' ${DEPLOY_DIR_IMAGE}/${PACK_CONF}
    else
        if ${@bb.utils.contains('DISTRO_FEATURES', 'absystem', 'true', 'false', d)}; then
            sed -i -e 's/sub_type=\"recovery\"/sub_type=\"recovery_a\"/' ${DEPLOY_DIR_IMAGE}/${PACK_CONF}
        fi
        if ${@bb.utils.contains('DISTRO_FEATURES', 'uboot-abmode', 'true', 'false', d)}; then
            sed -i -e 's/sub_type=\"fip\"/sub_type=\"fip_a\"/' ${DEPLOY_DIR_IMAGE}/${PACK_CONF}
        fi
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'vendor-partition', 'false', 'true', d)}; then
        sed -i '/vendor/ d' ${DEPLOY_DIR_IMAGE}/${PACK_CONF}
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'gpt-partition', 'true', 'false', d)}; then
        sed -i '/usb_flow/a\file="gpt.img"           main_type="bin"        sub_type="gpt"        file_type="normal"' ${DEPLOY_DIR_IMAGE}/${PACK_CONF}
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'dtb-bind-kernel', 'true', 'false', d)}; then
        sed -i '/_aml_dtb/ d' ${DEPLOY_DIR_IMAGE}/${PACK_CONF}
    fi

    ${DEPLOY_DIR_IMAGE}/res_packer -r ${DEPLOY_DIR_IMAGE}/logo_img_files ${DEPLOY_DIR_IMAGE}/logo.img

    if ${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'true', 'false', d)}; then
        if [ "${ROOTFS_TYPE}" = "ubifs" ]; then
            cp ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ubi ${DEPLOY_DIR_IMAGE}/rootfs.img
            if [ -f ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ubi ];then
                cp ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ubi ${DEPLOY_DIR_IMAGE}/vendor.img
            fi
        else
            cp ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.${ROOTFS_TYPE} ${DEPLOY_DIR_IMAGE}/rootfs.img
            if [ -f ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.${ROOTFS_TYPE} ];then
                cp ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.${ROOTFS_TYPE} ${DEPLOY_DIR_IMAGE}/vendor.img
            fi
        fi
    else
        padding_to_multipleOf4K_for_img2simg ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ext4
        ${DEPLOY_DIR_IMAGE}/img2simg ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ext4 ${DEPLOY_DIR_IMAGE}/rootfs.ext4.img2simg
        ${DEPLOY_DIR_IMAGE}/ext2simg ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ext4 ${DEPLOY_DIR_IMAGE}/system.img
        gzip -9c ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ext4 > ${DEPLOY_DIR_IMAGE}/rootfs.ext4.gz

        if [ -f ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ext4 ];then
            padding_to_multipleOf4K_for_img2simg ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ext4
            ${DEPLOY_DIR_IMAGE}/img2simg ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ext4 ${DEPLOY_DIR_IMAGE}/vendor.ext4.img2simg
            gzip -9c ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ext4 > ${DEPLOY_DIR_IMAGE}/vendor.ext4.gz
        fi

        if [ "${SOC_FAMILY}" = "p1" ];then
            cp ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ext4 ${DEPLOY_DIR_IMAGE}/rootfs.ext4.img2simg
            cp ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.ext4 ${DEPLOY_DIR_IMAGE}/vendor.ext4.img2simg
        fi
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
        local chipset_name=$(echo ${CHIPSET_NAME} | tr 'A-Z' 'a-z')
        ${AML_SCS_SIGN} --soc ${chipset_name} \
            --aml_key ${DEPLOY_DIR_IMAGE}/device-keys \
            --fw_arb_cfg ${DEPLOY_DIR_IMAGE}/fw_arb.cfg \
            --aml_img ${DEPLOY_DIR_IMAGE}/aml_upgrade_package.img \
            --bootloader_zip ${DEPLOY_DIR_IMAGE}/${UBOOT_TYPE%_config}-u-boot.aml.zip \
            --output ${DEPLOY_DIR_IMAGE}/aml_upgrade_package_enc.img
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', 'true', 'false', d)}; then
        cp -rf ${STAGING_DIR_NATIVE}${bindir}/aml-linux-sb/keys ${DEPLOY_DIR_IMAGE}/

        if [ "${AML_SB_SIGN_TOOL}" = "" ]; then
            AML_SB_SIGN=${STAGING_DIR_NATIVE}${bindir}/aml-linux-sb/amlogic_secureboot_sign_whole_pkg.bash
        else
            AML_SB_SIGN=${AML_SB_SIGN_TOOL}
        fi

        ${AML_SB_SIGN} --soc ${SOC_FAMILY} \
            --aml_key ${DEPLOY_DIR_IMAGE}/keys \
            --aml_img ${DEPLOY_DIR_IMAGE}/aml_upgrade_package.img \
            --output ${DEPLOY_DIR_IMAGE}/aml_upgrade_package_enc.img
    fi

    #create software.swu
    if ${@bb.utils.contains('DISTRO_FEATURES', 'swupdate', 'true', 'false', d)}; then
        cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/swupdate-priv.pem ${DEPLOY_DIR_IMAGE}/
        cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/update.sh ${DEPLOY_DIR_IMAGE}/
        cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/sw_package_create.sh ${DEPLOY_DIR_IMAGE}/
        if ${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'true', 'false', d)}; then
            cp ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.${ROOTFS_TYPE} ${DEPLOY_DIR_IMAGE}/rootfs.${ROOTFS_TYPE}
            if [ -f ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.${ROOTFS_TYPE} ];then
                cp ${DEPLOY_DIR_IMAGE}/vendor-image-${MACHINE}.${ROOTFS_TYPE} ${DEPLOY_DIR_IMAGE}/vendor.${ROOTFS_TYPE}
            fi
            cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-description-nand-${ROOTFS_TYPE} ${DEPLOY_DIR_IMAGE}/sw-description
            cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-package-filelist-nand-${ROOTFS_TYPE} ${DEPLOY_DIR_IMAGE}/sw-package-filelist
            if ${@bb.utils.contains('DISTRO_FEATURES', 'AVB', 'true', 'false', d)}; then
                cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-description-nand-${ROOTFS_TYPE}-avb ${DEPLOY_DIR_IMAGE}/sw-description
                cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-package-filelist-nand-${ROOTFS_TYPE}-avb ${DEPLOY_DIR_IMAGE}/sw-package-filelist
            fi

            # For Zapper 2K
            if ${@bb.utils.contains('DISTRO_FEATURES', 'zapper-2k', 'true', 'false', d)}; then
                cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-description-nand-${ROOTFS_TYPE}-zapper-2k ${DEPLOY_DIR_IMAGE}/sw-description
                cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-package-filelist-nand-${ROOTFS_TYPE}-zapper-2k ${DEPLOY_DIR_IMAGE}/sw-package-filelist
            fi

            if ${@bb.utils.contains('DISTRO_FEATURES', 'vendor-partition', 'false', 'true', d)}; then
                sed -i 's/vendor.${ROOTFS_TYPE}//' ${DEPLOY_DIR_IMAGE}/sw-package-filelist
            fi
        else
            cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-description-emmc ${DEPLOY_DIR_IMAGE}/sw-description
            cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-package-filelist-emmc ${DEPLOY_DIR_IMAGE}/sw-package-filelist
            if ${@bb.utils.contains('DISTRO_FEATURES', 'AVB', 'true', 'false', d)}; then
                cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-description-emmc-avb ${DEPLOY_DIR_IMAGE}/sw-description
                cp -f ${STAGING_DIR_NATIVE}${bindir}/aml-swupdate/${SOC_FAMILY}/sw-package-filelist-emmc-avb ${DEPLOY_DIR_IMAGE}/sw-package-filelist
            fi

            if ${@bb.utils.contains('DISTRO_FEATURES', 'vendor-partition', 'false', 'true', d)}; then
                sed -i 's/vendor.ext4.gz//' ${DEPLOY_DIR_IMAGE}/sw-package-filelist
            fi
        fi

        if ${@bb.utils.contains('DISTRO_FEATURES', 'vendor-partition', 'false', 'true', d)}; then
            sed -i -e '/vendor/{n;d}' -e '$!N;/\n.*vendor/!P;D' ${DEPLOY_DIR_IMAGE}/sw-description
            sed -i -e '/vendor/{n;d}' -e '$!N;/\n.*vendor/!P;D' ${DEPLOY_DIR_IMAGE}/sw-description
            sed -i '/vendor/d' ${DEPLOY_DIR_IMAGE}/sw-description
        fi

        sed 's@boardname@${MACHINE_ARCH}@' -i ${DEPLOY_DIR_IMAGE}/sw-description
        sed 's@_lib32_@_@' -i ${DEPLOY_DIR_IMAGE}/sw-description

        ${DEPLOY_DIR_IMAGE}/sw_package_create.sh ${DEPLOY_DIR_IMAGE}
    fi
}

do_aml_pack[depends] += "core-image-minimal:do_image_complete "
do_aml_pack[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'vendor-partition', ' vendor-image:do_image_complete', '', d)}"
do_aml_pack[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'recovery', bb.utils.contains('MULTTILIBS', 'multilib:lib32', ' lib32-recovery-image:do_image_complete', ' recovery-image:do_image_complete', d), '', d)}"
do_aml_pack[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'AVB', ' vbmeta-image:do_compile', '', d)}"
do_aml_pack[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'gpt-partition', ' aml-gpt-make:do_install', '', d)}"
addtask aml_pack after do_image_complete before do_build
do_aml_pack[nostamp] = "1"
