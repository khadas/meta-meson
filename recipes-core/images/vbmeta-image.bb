SUMMARY = "create vbmeta.img for AVB"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

DOLBY_PROP = " --prop dovi_hash:3cd93647bdd864b4ae1712d57a7de3153e3ee4a4dfcfae5af8b1b7d999b93c5a "

DEPENDS += "avb-native python3-native avbkey-native"
DM_VERITY_SUPPORT = "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'true', 'false', d)}"
CHAINED_PARTITION_SUPPORT = "${@bb.utils.contains('DISTRO_FEATURES', 'AVB_chained_partition', 'true', 'false', d)}"
AVB_DM_VERITY_SYSTEM_PARTITION_NAME = "system"
AVB_DM_VERITY_VENDOR_PARTITION_NAME = "vendor"
AVB_DM_VERITY_SYSTEM_PARTITION_PUBKEY = "system_rsa2048.avbpubkey"
AVB_DM_VERITY_VENDOR_PARTITION_PUBKEY = "vendor_rsa2048.avbpubkey"
AVB_RECOVERY_PARTITION_PUBKEY = "recovery_rsa2048.avbpubkey"

AVB_VBMETA_RSA_KEY = "vbmeta_rsa2048.pem"
AVB_VBMETA_ALGORITHM = "SHA256_RSA2048"
AVB_VBMETA_RSA_KEY = "${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', \
    bb.utils.contains('DISTRO_FEATURES', 'verimatrix', 'bl33-level-3-rsa-priv.pem', 'vbmeta_rsa2048.pem', d), \
    'vbmeta_rsa2048.pem', d)}"
AVB_VBMETA_RSA_KEY_PATH = "${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', \
    bb.utils.contains('DISTRO_FEATURES', 'verimatrix', '${DEPLOY_DIR_IMAGE}', '${STAGING_DIR_NATIVE}/${sysconfdir_native}', d), \
    '${STAGING_DIR_NATIVE}/${sysconfdir_native}', d)}"
AVB_VBMETA_ALGORITHM = "${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', \
    bb.utils.contains('DISTRO_FEATURES', 'verimatrix', 'SHA256_RSA4096', 'SHA256_RSA2048', d), \
    'SHA256_RSA2048', d)}"

SIGN_VBMETA = " --key ${AVB_VBMETA_RSA_KEY_PATH}/${AVB_VBMETA_RSA_KEY} --algorithm ${AVB_VBMETA_ALGORITHM} --padding_size 4096 "


ADD_KERNEL_AVB = " --include_descriptors_from_image ${DEPLOY_DIR_IMAGE}/boot.img "
ADD_SYSTEM_AVB_DM_VERITY = " --include_descriptors_from_image ${DEPLOY_DIR_IMAGE}/${DM_VERITY_IMAGE}-${MACHINE}.${DM_VERITY_IMAGE_TYPE} "
ADD_VENDOR_AVB_DM_VERITY = " --include_descriptors_from_image ${DEPLOY_DIR_IMAGE}/${VENDOR_DM_VERITY_IMAGE}-${MACHINE}.${DM_VERITY_IMAGE_TYPE} "

CHAIN_SYSTEM_AVB_DM_VERITY = " --chain_partition ${AVB_DM_VERITY_SYSTEM_PARTITION_NAME}:${DEVICE_PROPERTY_SYSTEM_ROLLBACK_LOCATION}:${STAGING_DIR_NATIVE}/${sysconfdir_native}/${AVB_DM_VERITY_SYSTEM_PARTITION_PUBKEY} "
CHAIN_VENDOR_AVB_DM_VERITY = " --chain_partition ${AVB_DM_VERITY_VENDOR_PARTITION_NAME}:${DEVICE_PROPERTY_VENDOR_ROLLBACK_LOCATION}:${STAGING_DIR_NATIVE}/${sysconfdir_native}/${AVB_DM_VERITY_VENDOR_PARTITION_PUBKEY} "

CHAIN_RECOVERY = "${@bb.utils.contains('DISTRO_FEATURES', 'AVB_recovery_partition', 'true', 'false', d)}"

CHAIN_RECOVERY_CMD = " --chain_partition recovery:${DEVICE_PROPERTY_RECOVERY_ROLLBACK_LOCATION}:${STAGING_DIR_NATIVE}/${sysconfdir_native}/${AVB_RECOVERY_PARTITION_PUBKEY}"

SIGN_RECOVERY = "${@bb.utils.contains('DISTRO_FEATURES', 'recovery', bb.utils.contains('DISTRO_FEATURES', 'AVB_recovery_partition','true' , 'false' ,d), '', d)}"
AVB_RECOVERY_SIGNINING_KEY = "recovery_rsa2048.pem"
SIGN_RECOVERY_CMD = "avbtool.py add_hash_footer --image ${DEPLOY_DIR_IMAGE}/recovery.img --partition_size ${DEVICE_PROPERTY_RECOVERY_PARTITION_SIZE}  --partition_name "recovery" --algorithm SHA256_RSA2048 --key ${STAGING_DIR_NATIVE}/${sysconfdir_native}/${AVB_RECOVERY_SIGNINING_KEY} --rollback_index ${DEVICE_PROPERTY_RECOVERY_ROLLBACK_INDEX}"

DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'recovery', ' recovery-image', '', d)}"

VBMETA_ROLLBACK_INDEX = " --rollback_index ${DEVICE_PROPERTY_VBMETA_ROLLBACK_INDEX}"

do_compile() {
    install -d ${DEPLOY_DIR_IMAGE}
    #if boot.img already has hash_footer, avbtool won't add again, so don't need erase hash_footer first
    avbtool.py add_hash_footer --image ${DEPLOY_DIR_IMAGE}/boot.img --partition_size ${DEVICE_PROPERTY_BOOT_PARTITION_SIZE} --partition_name boot

    if [ "${SIGN_RECOVERY}" = "true" ]; then
        ${SIGN_RECOVERY_CMD}
    fi

    if [ "${CHAIN_RECOVERY}" = "true" ]; then
        RECOVERY_CMD="${CHAIN_RECOVERY_CMD}"
    else
        RECOVERY_CMD=""
    fi

    if [ "${DM_VERITY_SUPPORT}" = "true" ]; then
        if [ "${CHAINED_PARTITION_SUPPORT}" = "true" ]; then
            avbtool.py make_vbmeta_image --output ${DEPLOY_DIR_IMAGE}/vbmeta.img ${SIGN_VBMETA} ${DOLBY_PROP} ${ADD_KERNEL_AVB} ${RECOVERY_CMD} ${CHAIN_SYSTEM_AVB_DM_VERITY} ${CHAIN_VENDOR_AVB_DM_VERITY} ${VBMETA_ROLLBACK_INDEX}
        else
            avbtool.py make_vbmeta_image --output ${DEPLOY_DIR_IMAGE}/vbmeta.img ${SIGN_VBMETA} ${DOLBY_PROP} ${ADD_KERNEL_AVB} ${RECOVERY_CMD} ${ADD_SYSTEM_AVB_DM_VERITY} ${ADD_VENDOR_AVB_DM_VERITY} ${VBMETA_ROLLBACK_INDEX}
        fi
    else
            avbtool.py make_vbmeta_image --output ${DEPLOY_DIR_IMAGE}/vbmeta.img ${SIGN_VBMETA} ${DOLBY_PROP} ${ADD_KERNEL_AVB} ${RECOVERY_CMD} ${VBMETA_ROLLBACK_INDEX}
    fi
}

do_compile[depends] = "core-image-minimal:do_image_complete"
do_compile[depends] += "${@bb.utils.contains('DISTRO_FEATURES', 'recovery', bb.utils.contains('MULTTILIBS', 'multilib:lib32', ' lib32-recovery-image:do_image_complete', ' recovery-image:do_image_complete', d), '', d)}"

deltask do_package
deltask do_packagedata
deltask do_package_write_ipk
