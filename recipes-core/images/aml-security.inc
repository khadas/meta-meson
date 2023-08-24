
# The following 2 variables are used to enable/disable
# dm-verity and partition-enc to an image explicitly.

# Usually, when image recipe includes aml-security.inc, dm-verity and partition-enc will
# be applied to image according to DISTRO_FEATURES.

# However, sometimes we want to have finer, more precise control over the
# switches of dm-verity and partition-enc.
# In these cases, we can make use of these two variables.
# For example, we may want to enable partition-enc to recovery.img but not dm-verity, so we can
# set ENABLE_DM_VERITY to 'false' before including aml-security.inc in recovery-image.bb.

ENABLE_DM_VERITY ??= "true"
ENABLE_PARTITION_ENCRYPTION ??= "true"

# IMAGE_FSTYPES will be expanded by dm-verity or partition encryption classes
# DM VERITY
DM_VERITY_IMAGE ??= "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', '${PN}', '', d)}"
DM_VERITY_IMAGE_TYPE ??= "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', '${ROOTFS_TYPE}', '', d)}"

# AVB & dm-verity
IMAGE_CLASSES += "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', \
                     bb.utils.contains('DISTRO_FEATURES', 'AVB', 'avb-dm-verity', 'aml-dm-verity-img', d), '', d) \
                     if ENABLE_DM_VERITY == 'true' else ''}"

# Partition encryption
PARTITION_ENCRYPTION_KEY ?= "${@bb.utils.contains('DISTRO_FEATURES', 'partition-enc', 'default.bin', '', d)}"

PARTITION_ENCRYPTION_IMAGE_TYPES = "${@bb.utils.contains('DISTRO_FEATURES', 'partition-enc', \
                                       bb.utils.contains('DISTRO_FEATURES', 'dm-verity', \
                                       '${ROOTFS_TYPE}.verity' if ENABLE_DM_VERITY == 'true' else '${ROOTFS_TYPE}', '${ROOTFS_TYPE}', d), \
                                       '', d)}"

PARTITION_SPECIFIED_AS_ENCRYPTED = "${@bb.utils.contains('ENCRYPTED_PARTITIONS', '${PARTITION_NAME}', 'true', 'false', d)}"

# add aml-partition-encrypt for pre-encryption,
# if (ENABLE_PARTITION_ENCRYPTION && PARTITION_SPECIFIED_AS_ENCRYPTED && partition-enc)
IMAGE_CLASSES += "${@bb.utils.contains('DISTRO_FEATURES', 'partition-enc', \
                    'aml-partition-encrypt' if PARTITION_SPECIFIED_AS_ENCRYPTED == 'true' else '', '', d) \
                     if ENABLE_PARTITION_ENCRYPTION == 'true' else ''}"

# The classes put into IMAGE_CLASSES will be inherited by "inherit image", so we need to put all necessary
# inheritance before "inherit image"
