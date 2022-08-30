IMAGE_FEATURES = ""
PACKAGE_INSTALL = "vendor-files ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'refpolicy', '', d)}"
export IMAGE_BASENAME = "vendor-image"
IMAGE_LINGUAS = ""
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'policycoreutils-native', '', d)}"

LICENSE = "MIT"

# don't actually generate an image, just the artifacts needed for one
IMAGE_FSTYPES = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', \
                bb.utils.contains('ROOTFS_TYPE', 'ubifs', 'ubi', '${ROOTFS_TYPE}', d), 'ext4', d)}"

#UBI
UBI_VOLNAME = "vendor"
MKUBIFS_ARGS = "-F -m 4096 -e 253952 -c 60"
UBINIZE_ARGS = "-m 4096 -p 256KiB -s 4096 -O 4096"

inherit core-image

IMAGE_ROOTFS_SIZE = "327680"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
IMAGE_PREPROCESS_COMMAND += "remove_folder;create_dolbyms12_link;selinux_set_labels;"

remove_folder() {
    rm -rf ${IMAGE_ROOTFS}/var
    rm -rf ${IMAGE_ROOTFS}/run
    rm ${IMAGE_ROOTFS}/etc/timestamp
    rm ${IMAGE_ROOTFS}/etc/version
    rm ${IMAGE_ROOTFS}/etc/ld.so.cache
}

#during system booting up, when decrypting dolby M12 library, audioserver.service need to create soft symbol link dynamically.
#because vendor partition will be changed to read-only, so we create this link here, and audioserver.service only need to create
#the link targart /tmp/ds/0x4d_0x5331_0x32.so
create_dolbyms12_link() {
    mkdir -p ${IMAGE_ROOTFS}/lib
    ln -sf /tmp/ds/0x4d_0x5331_0x32.so ${IMAGE_ROOTFS}/lib/libdolbyms12.so 
}

selinux_set_labels () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'true', 'false', d)}; then
        POL_TYPE=$(sed -n -e "s&^SELINUXTYPE[[:space:]]*=[[:space:]]*\([0-9A-Za-z_]\+\)&\1&p" ${IMAGE_ROOTFS}/${sysconfdir}/selinux/config)
        if ! setfiles -m -r ${IMAGE_ROOTFS} ${IMAGE_ROOTFS}/${sysconfdir}/selinux/${POL_TYPE}/contexts/files/file_contexts ${IMAGE_ROOTFS}
        then
            echo WARNING: Unable to set filesystem context, setfiles / restorecon must be run on the live image.
            touch ${IMAGE_ROOTFS}/.autorelabel
            exit 0
        fi

        rm -rf ${IMAGE_ROOTFS}/usr
        rm -rf ${IMAGE_ROOTFS}/etc/selinux
    fi
}

# For dm-verity
IMAGE_CLASSES += "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'image_types aml-dm-verity-img', '', d)}"
DM_VERITY_IMAGE = "vendor-image"
DM_VERITY_IMAGE_TYPE = "ext4"
STAGING_VERITY_DIR = "${DEPLOY_DIR_IMAGE}"

inherit avb-dm-verity
# The following is needed only if chained
AVB_DMVERITY_SIGNINING_KEY = "vendor_rsa2048.pem"
AVB_DMVERITY_SIGNINING_ALGORITHM = "SHA256_RSA2048"
AVB_DMVERITY_PARTITON_SIZE = "${DEVICE_PROPERTY_VENDOR_PARTITION_SIZE}"
AVB_DM_VERITY_PARTITON_NAME = "vendor"
