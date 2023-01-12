SUMMARY = "Amlogic Yocto BSP Image"
LICENSE = "MIT"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
IMAGECLASS ?=  "core-image"

inherit ${IMAGECLASS}
IMAGE_FSTYPES = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', \
                bb.utils.contains('ROOTFS_TYPE', 'ubifs', 'ubi', '${ROOTFS_TYPE}', d), 'ext4', d)}"

require aml-package.inc

IMAGE_INSTALL += " \
    toybox \
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
    systemd \
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs \
    e2fsprogs-tune2fs \
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
"

IMAGE_INSTALL += " \
    aml-hdcp \
    liblog \
    android-tools-logcat \
    modules-load \
    aml-ubootenv \
    aml-utils-simulate-key \
    vulkan-loader \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-cas', 'drmplayer-bin ffmpeg-vendor', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-dtv', 'aml-dtvdemod', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'playready', 'playready', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-userspace tee-supplicant optee-video-firmware aml-provision', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'widevine', 'aml-mediadrm-widevine', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'gstreamer1', \
        'gstreamer1.0-plugins-good \
        gstreamer1.0-plugins-bad \
        gstreamer1.0-plugins-ugly \
        gst-plugin-aml-asink \
        gst-plugin-video-sink \
        gst-plugin-aml-v4l2dec \
        gst-aml-drm-plugins \
        gstreamer1.0-libav \
        gst-player \
        ', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'weston', \
        'wayland \
        weston-init \
        meson-display \
        fbscripts \
        ', '', d)} \
    pulseaudio \
    aml-pqserver \
    aml-audio-service aml-audio-service-testapps \
    aml-audio-hal \
    openssh \
    sshfs-fuse \
"

IMAGE_INSTALL += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'dfb', 'directfb directfb-examples', '', d)} \
"

#    icu \
#    aml-libdvr \
#    aml-mp-sdk \
#    aml-subtitleserver \
#    dolby-ms12 \
#

PACKAGE_INSTALL += "base-files base-passwd initramfs-meson-boot udev-extraconf e2fsprogs "

#For Nagra CAS
IMAGE_INSTALL += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'nagra', 'nagra-sdk', '', d)} \
    "

#Add ubifs tools
RDEPENDS_packagegroup-amlogic-baserootfs += "${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'mtd-utils-ubifs', '',d)}"

MACHINE_IMAGE_NAME ?= "${PN}"
IMAGE_FEATURES_remove = " read-only-rootfs"
DEPENDS_append = " android-tools-native"

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
