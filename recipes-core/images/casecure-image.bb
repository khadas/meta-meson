IMAGE_FEATURES = ""
#PACKAGE_INSTALL = "irdeto-sdk"
export IMAGE_BASENAME = "casecure-image"
IMAGE_LINGUAS = ""
THIS_DIR = "${@os.path.dirname(d.getVar('FILE', True))}"

LICENSE = "MIT"
DEPENDS = "irdeto-sdk"
# don't actually generate an image, just the artifacts needed for one
IMAGE_FSTYPES = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', \
                bb.utils.contains('ROOTFS_TYPE', 'ubifs', 'ubi', '${ROOTFS_TYPE}', d), 'ext4', d)}"

inherit core-image

IMAGE_PREPROCESS_COMMAND += "remove_folder;casecure_img;"

remove_folder() {
    rm -rf ${IMAGE_ROOTFS}/*
}

casecure_img() {
    echo "Files in ${DEPLOY_DIR_IMAGE}:"
    ls ${DEPLOY_DIR_IMAGE}
    ls ${S}/
    if [ -e ${STAGING_DIR_HOST}/casecure/ ];then
        ls ${STAGING_DIR_HOST}/casecure/
        cp ${STAGING_DIR_HOST}/casecure/* ${IMAGE_ROOTFS}/
    fi
}