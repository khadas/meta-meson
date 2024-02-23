
do_partition_encryption() {
    TYPE=$1
    part_img=${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${TYPE}
    bbnote "before md5 = `md5sum ${part_img}`"
    key_file=${STAGING_DIR_NATIVE}/${sysconfdir_native}/${PARTITION_ENCRYPTION_KEY}
    if [ -f ${key_file} ]; then
        bbnote "enc_part -i ${part_img} -o ${part_img} -k `xxd -p ${key_file}`"
        # In place for limitation of disk space on Jenkins
        enc_part -i ${part_img} -o ${part_img} -k `xxd -p ${key_file}`
        bbnote "after md5 = `md5sum ${part_img}`"
        ln -s ${part_img} ${part_img}.enc
    else
        bbfatal "Cannot find partition keys for ${PN}"
    fi
}

CONVERSIONTYPES += "enc"
CONVERSION_CMD_enc = "do_partition_encryption ${type}"
CONVERSION_DEPENDS_enc = "xxd-native partition-enc-native partition-keys-native"

python __anonymous() {
    partition_enc_image_types = set(d.getVar('PARTITION_ENCRYPTION_IMAGE_TYPES').split())
    image_fstypes = set(d.getVar('IMAGE_FSTYPES').split())
    for t in partition_enc_image_types & image_fstypes:
        d.appendVar('IMAGE_FSTYPES', ' %s.enc' % t)
}
