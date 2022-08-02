inherit aml-dm-verity-img

#AVB with DM-verity
AVB_DM_VERITY = '${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', bb.utils.contains('DISTRO_FEATURES', 'AVB', 'true', 'false' ,d), 'false', d)}'
CHAINED_PARTITION_SUPPORT = "${@bb.utils.contains('DISTRO_FEATURES', 'AVB_chained_partition', 'true', 'false', d)}"
DEPENDS += '${@'python3-native openssl-native avbtool-dm-verity-native avbkey-native' if AVB_DM_VERITY == 'true'  else ''}'

def is_numeric_base(s, base):
    try:
        value = int(s, base)
        return True
    except ValueError:
        return False

python create_avb_dm_verity_image () {
    deploy_dir_image = d.getVar('DEPLOY_DIR_IMAGE', True)
    dm_verity_image = d.getVar('DM_VERITY_IMAGE', True)
    dm_verity_image_type = d.getVar('DM_VERITY_IMAGE_TYPE', True)
    name = d.getVar('AVB_DM_VERITY_PARTITON_NAME', True)
    chained = d.getVar('CHAINED_PARTITION_SUPPORT', True)
    partition_size = d.getVar('AVB_DMVERITY_PARTITON_SIZE', True)
    sysconfdir = d.getVar('sysconfdir_native', True)
    native_dir = d.getVar('STAGING_DIR_NATIVE', True)
    key = d.getVar('AVB_DMVERITY_SIGNINING_KEY', True)
    algorithm = d.getVar('AVB_DMVERITY_SIGNINING_ALGORITHM', True)
    filename = deploy_dir_image + '/' + dm_verity_image + '.' + dm_verity_image_type + '.verity.env'
    verity_array = dict()
    try:
        with open(filename, 'r') as verity_env:
            bb.note('parsing ' + filename)
            for item in verity_env.readlines():
                if '#' not in item:
                    data = item.rstrip('\n').partition('=')
                    verity_array[data[0]] = data[2]
    except FileNotFoundError:
        bb.error('Cannot find ' + filename)

    for data in verity_array:
        bb.note(data + ' = ' + verity_array[data])

    image_deploy_dir = d.getVar('IMGDEPLOYDIR', True)
    machine = d.getVar('MACHINE', True)
# pad to 4096 bytes to match img2simg
    cmd = 'avbtool-dm-verity.py add_footer_without_hashtree' + ' --image ' + image_deploy_dir + '/' + dm_verity_image + '-' + machine + '.ext4' + ' --partition_name ' + name + ' --root_digest ' + verity_array['ROOT_HASH'] + ' --salt ' + verity_array['SALT'] + ' --hash_algorithm sha256 ' + ' --data_block_size ' + verity_array['DATA_BLOCK_SIZE'] + ' --hash_block_size ' + verity_array['HASH_BLOCK_SIZE'] + ' --data_size ' + verity_array['DATA_SIZE'] + ' --padding_size 4096'
    if chained == 'true':
        cmd += ' --key ' + native_dir + '/' + sysconfdir + '/' + key + ' --algorithm ' + algorithm
    bb.note(cmd)
    bb.process.run(cmd)
    if isinstance(partition_size , str) and is_numeric_base(partition_size, 10) or is_numeric_base(partition_size, 16):
        cmd = 'avbtool-dm-verity.py resize_image' + ' --image ' + image_deploy_dir + '/' + dm_verity_image + '-' + machine + '.ext4' + ' --partition_size ' + partition_size
        bb.note(cmd)
        bb.process.run(cmd)
}

IMAGE_POSTPROCESS_COMMAND += '${@'create_avb_dm_verity_image' if AVB_DM_VERITY == 'true'  else ''}'
