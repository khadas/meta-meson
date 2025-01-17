SUMMARY = "create vbmeta.img for AVB"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

DOLBY_PROP = " --prop dovi_hash:3cd93647bdd864b4ae1712d57a7de3153e3ee4a4dfcfae5af8b1b7d999b93c5a "
DOLBY_PROP:s7d = " --prop dovi_hash:5c65b506c3a83b789b4dc7448a3ce7e3445519073e4fb55d73c90070a8431cdd "

DEPENDS += "avb-native python3-native avbkey-native"
DM_VERITY_SUPPORT = "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'true', 'false', d)}"
CHAINED_PARTITION_SUPPORT = "${@bb.utils.contains('DISTRO_FEATURES', 'AVB_chained_partition', 'true', 'false', d)}"
AVB_DM_VERITY_SYSTEM_PARTITION_NAME = "system"
AVB_DM_VERITY_VENDOR_PARTITION_NAME = "vendor"
AVB_DM_VERITY_SYSTEM_PARTITION_PUBKEY = "system_rsa2048.avbpubkey"
AVB_DM_VERITY_VENDOR_PARTITION_PUBKEY = "vendor_rsa2048.avbpubkey"

AVB_VBMETA_RSA_KEY = "${@bb.utils.contains('DISTRO_FEATURES', 'scs', 'bl33-level-3-rsa-priv.pem', 'vbmeta_rsa2048.pem', d)}"


AVB_VBMETA_RSA_KEY_PATH = "${@bb.utils.contains('DISTRO_FEATURES', 'scs', \
    '${DEPLOY_DIR_IMAGE}', '${STAGING_DIR_NATIVE}/${sysconfdir_native}', d)}"


AVB_VBMETA_ALGORITHM = "${@bb.utils.contains('DISTRO_FEATURES', 'scs', \
    'SHA256_RSA4096', 'SHA256_RSA2048', d)}"


SIGN_VBMETA = " --key ${AVB_VBMETA_RSA_KEY_PATH}/${AVB_VBMETA_RSA_KEY} --algorithm ${AVB_VBMETA_ALGORITHM} --padding_size 4096 "


ADD_KERNEL_AVB = " --include_descriptors_from_image ${DEPLOY_DIR_IMAGE}/boot.img "
ADD_SYSTEM_AVB_DM_VERITY = " --include_descriptors_from_image ${DEPLOY_DIR_IMAGE}/${SYSTEM_DM_VERITY_IMAGE}-${MACHINE}.${DM_VERITY_IMAGE_TYPE} "
DEPENDS:append = ' ${SYSTEM_DM_VERITY_IMAGE}'

ADD_VENDOR_AVB_DM_VERITY = " --include_descriptors_from_image ${DEPLOY_DIR_IMAGE}/${VENDOR_DM_VERITY_IMAGE}-${MACHINE}.${DM_VERITY_IMAGE_TYPE} "
DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'vendor-partition', ' ${VENDOR_DM_VERITY_IMAGE}', '', d)}"

CHAIN_SYSTEM_AVB_DM_VERITY = " --chain_partition ${AVB_DM_VERITY_SYSTEM_PARTITION_NAME}:${DEVICE_PROPERTY_SYSTEM_ROLLBACK_LOCATION}:${STAGING_DIR_NATIVE}/${sysconfdir_native}/${AVB_DM_VERITY_SYSTEM_PARTITION_PUBKEY} "
CHAIN_VENDOR_AVB_DM_VERITY = " --chain_partition ${AVB_DM_VERITY_VENDOR_PARTITION_NAME}:${DEVICE_PROPERTY_VENDOR_ROLLBACK_LOCATION}:${STAGING_DIR_NATIVE}/${sysconfdir_native}/${AVB_DM_VERITY_VENDOR_PARTITION_PUBKEY} "

INCLUDE_VENDOR = "${@bb.utils.contains('DISTRO_FEATURES', 'vendor-partition', 'true', 'false', d)}"

VBMETA_ROLLBACK_INDEX = " --rollback_index ${DEVICE_PROPERTY_VBMETA_ROLLBACK_INDEX}"

do_compile() {
    install -d ${DEPLOY_DIR_IMAGE}
    #if boot.img already has hash_footer, avbtool won't add again, so don't need erase hash_footer first
    avbtool.py add_hash_footer --image ${DEPLOY_DIR_IMAGE}/boot.img --partition_size ${DEVICE_PROPERTY_BOOT_PARTITION_SIZE} --partition_name boot


    if [ "${DM_VERITY_SUPPORT}" = "true" ]; then
        if [ "${CHAINED_PARTITION_SUPPORT}" = "true" ]; then
            if [ "${INCLUDE_VENDOR}" = "true" ]; then
                avbtool.py make_vbmeta_image --output ${DEPLOY_DIR_IMAGE}/vbmeta.img ${SIGN_VBMETA} ${DOLBY_PROP} ${ADD_KERNEL_AVB} ${CHAIN_SYSTEM_AVB_DM_VERITY} ${CHAIN_VENDOR_AVB_DM_VERITY} ${VBMETA_ROLLBACK_INDEX}
            else
                avbtool.py make_vbmeta_image --output ${DEPLOY_DIR_IMAGE}/vbmeta.img ${SIGN_VBMETA} ${DOLBY_PROP} ${ADD_KERNEL_AVB} ${CHAIN_SYSTEM_AVB_DM_VERITY} ${VBMETA_ROLLBACK_INDEX}
            fi
        else
            if [ "${INCLUDE_VENDOR}" = "true" ]; then
                avbtool.py make_vbmeta_image --output ${DEPLOY_DIR_IMAGE}/vbmeta.img ${SIGN_VBMETA} ${DOLBY_PROP} ${ADD_KERNEL_AVB} ${ADD_SYSTEM_AVB_DM_VERITY} ${ADD_VENDOR_AVB_DM_VERITY} ${VBMETA_ROLLBACK_INDEX}
            else
                avbtool.py make_vbmeta_image --output ${DEPLOY_DIR_IMAGE}/vbmeta.img ${SIGN_VBMETA} ${DOLBY_PROP} ${ADD_KERNEL_AVB} ${ADD_SYSTEM_AVB_DM_VERITY} ${VBMETA_ROLLBACK_INDEX}
            fi
        fi
    else
            avbtool.py make_vbmeta_image --output ${DEPLOY_DIR_IMAGE}/vbmeta.img ${SIGN_VBMETA} ${DOLBY_PROP} ${ADD_KERNEL_AVB} ${VBMETA_ROLLBACK_INDEX}
    fi
}

do_compile[depends] = "core-image-minimal:do_image_complete"

deltask do_package
deltask do_packagedata
deltask do_package_write_ipk
