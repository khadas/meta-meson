SUMMARY = "Amlogic Yocto Zapper Image"
LICENSE = "MIT"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
IMAGECLASS ?=  "core-image"

inherit ${IMAGECLASS}
IMAGE_FSTYPES = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', \
                bb.utils.contains('ROOTFS_TYPE', 'ubifs', 'ubi', '${ROOTFS_TYPE}', d), 'ext4', d)}"

require aml-package.inc

IMAGE_INSTALL += " \
    sqlite3 \
    libarchive \
    libunwind \
    libdaemon \
    libffi \
    libfastjson \
    glibc \
    expat \
    tinyalsa \
    alsa-utils \
    bash \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'sysvinit util-linux-agetty', '', d)} \
    fuse-exfat \
    exfat-utils \
    ntfs-3g \
    glib-2.0 \
    gnutls \
    jansson \
    libgcrypt \
    libgpg-error \
    libpcre \
    libxml2 \
    neon \
    popt \
    yajl \
    procps \
    libnl \
    dbus \
    faad2 \
    libopus \
    tzdata \
    tzcode \
    android-tools \
    procrank \
    zram \
    system-config \
    playscripts \
    tinyalsa-tools \
    glibc-utils \
    localedef \
    format-partitions \
    ${@bb.utils.contains('DISTRO_FEATURES', 'nand', '', 'e2fsprogs e2fsprogs-e2fsck e2fsprogs-mke2fs e2fsprogs-tune2fs', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'swupdate', 'cpio update-swfirmware aml-bootloader-message', '', d)} \
"

IMAGE_INSTALL += " \
    aml-hdcp \
    liblog \
    android-tools-logcat \
    modules-load \
    aml-ubootenv \
    aml-utils-simulate-key \
    aml-hdmicec \
    aml-audio-service \
    aml-audio-hal \
    aml-mp-sdk \
    dtvkit-release-prebuilt \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-iptv', 'aml-iptv-firmware ffmpeg-ctc', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-cas', 'drmplayer-bin ffmpeg-vendor', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-dtv', 'aml-dtvdemod aml-afd', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-userspace tee-supplicant optee-video-firmware aml-provision', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'dfb', 'directfb', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'arka', 'arka', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'nagra', 'nagra-sdk', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'irdeto', 'irdeto-sdk irdeto-cashal-rel', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'dolby-ms12', 'dolby-ms12', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'swupdate-dvb-ota', \
        bb.utils.contains('DISTRO_FEATURES', 'dtvkit-src', 'aml-dvb-ota-dtvkit', 'aml-dvb-ota-dtvkit-prebuilt', d), '', d)} \
"


IMAGE_INSTALL += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'arka-prebuilt-pkg', 'arka-prebuilt-pkg', '', d)} \
"

#    icu \
#    aml-libdvr \
#    aml-subtitleserver \
#    dolby-ms12 \
#

PACKAGE_INSTALL += "base-files base-passwd initramfs-meson-boot udev-extraconf "

PACKAGE_EXCLUDE = " kernel-devicetree"

MACHINE_IMAGE_NAME ?= "${PN}"
IMAGE_FEATURES:remove = " read-only-rootfs"
DEPENDS:append = " android-tools-native"

#reduce this value to reserve space for DM-verity/AVB meta-data at the end of partition(64M)
IMAGE_ROOTFS_SIZE = "983040"

#UBI
UBI_VOLNAME = "rootfs"
#4k
#MKUBIFS_ARGS = "-F -m 4096 -e 253952 -c 1120"
#UBINIZE_ARGS = "-m 4096 -p 256KiB -s 4096 -O 4096"
#2K
#MKUBIFS_ARGS = "-v -m 2048 -e 126976 -c 2212"
#UBINIZE_ARGS = "-m 2048 -p 128KiB -s 2048 -O 2048"

IMAGE_ROOTFS_EXTRA_SPACE = "0"
KERNEL_BOOTARGS = "root=/dev/system rootfstype=ext4"

do_rootfs[depends] += "android-tools-native:do_populate_sysroot"

#ROOTFS_POSTPROCESS_COMMAND += "disable_systemd_services; "
##disable_systemd_services() {
#        if [ -d ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/ ]; then
#                rm -f ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/appmanager.service;
#
#        fi
#
#        # Remove files added by openembedded-core/meta/recipes-connectivity/wpa-supplicant/wpa-supplicant_2.7.bb:
#        rm -f ${IMAGE_ROOTFS}${systemd_unitdir}/system/wpa_supplicant.service
#        rm -f ${IMAGE_ROOTFS}${systemd_unitdir}/system/wpa_supplicant-nl80211@.service
#        rm -f ${IMAGE_ROOTFS}${systemd_unitdir}/system/wpa_supplicant-wired@.service
#        rm -f ${IMAGE_ROOTFS}${systemd_unitdir}/system/wpa_supplicant@.service
#        rm -f ${IMAGE_ROOTFS}${sysconfdir}/network/if-pre-up.d/wpa-supplicant
#        rm -f ${IMAGE_ROOTFS}${sysconfdir}/network/if-post-down.d/wpa_supplicant
#        #rm -f ${IMAGE_ROOTFS}${sysconfdir}/dbus-1/system.d/dbus-wpa_supplicant.conf
#        #rm -f ${IMAGE_ROOTFS}${sysconfdir}/etc/default/volatiles/99_wpa_supplicant
#}

R = "${IMAGE_ROOTFS}"

PROJECT_BRANCH ?= "default"

python version_hook(){
      bb.build.exec_func('create_version_file', d)
}

python create_version_file() {

    version_file = os.path.join(d.getVar("R", True), 'version.txt')
    image_name = d.getVar("IMAGE_NAME", True)
    machine = d.getVar("MACHINE", True).upper()
    branch = d.getVar("PROJECT_BRANCH", True)
    yocto_version = d.getVar("DISTRO_CODENAME", True)
    release_version = d.getVar("RELEASE_VERSION", True) or '0.0.0.0'
    release_spin = d.getVar("RELEASE_SPIN", True) or '0'
    stamp = d.getVar("DATETIME", True)
    t = time.strptime(stamp, '%Y%m%d%H%M%S')
    build_time = time.strftime('"%Y-%m-%d %H:%M:%S"', t)
    gen_time = time.strftime('Generated on %a %b %d  %H:%M:%S UTC %Y', t)
    with open(version_file, 'w') as fw:
        fw.write('imagename:{0}\n'.format(image_name))
        fw.write('BRANCH={0}\n'.format(branch))
        fw.write('YOCTO_VERSION={0}\n'.format(yocto_version))
        fw.write('VERSION={0}\n'.format(release_version))
        fw.write('SPIN={0}\n'.format(release_spin))
        fw.write('BUILD_TIME={0}\n'.format(build_time))
        fw.write('{0}\n'.format(gen_time))
    build_config = os.path.join(d.getVar("TOPDIR", True), 'build-images.txt')
    taskdata = d.getVar("BB_TASKDEPDATA", True)
    key = sorted(taskdata)[0]
    target = taskdata[key][0]
    line = '{0} - {1}\n'.format(target, image_name)
    with open(build_config, 'a') as fw:
        fw.write(line)
}

create_version_file[vardepsexclude] += "DATETIME"
create_version_file[vardepsexclude] += "BB_TASKDEPDATA"

ROOTFS_POSTPROCESS_COMMAND += "version_hook; "
ROOTFS_POSTPROCESS_COMMAND += "create_extra_folder; "

create_extra_folder(){
    if [ ! -d ${IMAGE_ROOTFS}/tee ];then
        mkdir -p ${IMAGE_ROOTFS}/tee
    fi

    if [ ! -d ${IMAGE_ROOTFS}/data ];then
        mkdir -p ${IMAGE_ROOTFS}/data
    fi
}

process_for_read_only_rootfs(){
    if [ ! -f ${IMAGE_ROOTFS}/usr/bin/hdcp_tx22 ];then
        touch ${IMAGE_ROOTFS}/usr/bin/hdcp_tx22
        chmod +x ${IMAGE_ROOTFS}/usr/bin/hdcp_tx22
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "${@bb.utils.contains('DISTRO_FEATURES', 'OverlayFS', '', 'process_for_read_only_rootfs; ', d)}"

inherit avb-dm-verity
# The following is needed only if chained
AVB_DMVERITY_SIGNINING_KEY = "system_rsa2048.pem"
AVB_DMVERITY_SIGNINING_ALGORITHM = "SHA256_RSA2048"
AVB_DMVERITY_PARTITION_SIZE = "${DEVICE_PROPERTY_SYSTEM_PARTITION_SIZE}"
AVB_DMVERITY_ROLLBACK_INDEX = "${DEVICE_PROPERTY_SYSTEM_ROLLBACK_INDEX}"
AVB_DM_VERITY_PARTITION_NAME = "system"
