SUMMARY = "Amlogic Yocto Image"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

IMAGECLASS ?= " ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux-image', 'core-image', d)} "
inherit ${IMAGECLASS}

IMAGE_FSTYPES = "${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'ubi', 'ext4', d)}"

require aml-package.inc

LICENSE = "MIT"

IMAGE_FEATURES += "splash "
IMAGE_FEATURES += "tools-debug "

IMAGE_INSTALL += " \
    packagegroup-amlogic-baserootfs \
    gdb \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'packagegroup-core-selinux auditd', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'test_tools', 'test-tools', '', d)} \
    "

# unicode locale support
IMAGE_INSTALL_append = " glibc-utils localedef"

MACHINE_IMAGE_NAME ?= "${PN}"
IMAGE_FEATURES_remove = " read-only-rootfs"

VIRTUAL-RUNTIME_dev_manager = "busybox"

DEPENDS_append = " android-tools-native"

PACKAGE_INSTALL += "base-files netbase ${VIRTUAL-RUNTIME_base-utils} ${VIRTUAL-RUNTIME_dev_manager} base-passwd ${ROOTFS_BOOTSTRAP_INSTALL} initramfs-meson-boot e2fsprogs "

# Linguas defined in local.conf
#IMAGE_LINGUAS = ""

#reduce this value to reserve space for DM-verity/AVB meta-data at the end of partition(64M)
#unit is 1K, ${IMAGE_ROOTFS_SIZE} + DM-verity-meta-data-size=system-flash-partition-size, 1.5G rootfs, the DM-meta-data-size is about 13M, so reserve 32M is enough
#IMAGE_ROOTFS_SIZE = (0x60000000 - 32*1024*1024)/1024
IMAGE_ROOTFS_SIZE = "1540096"

#UBI
# -e 253952: logical eraseblock size of the UBI volume
# -c 1240: specifies maximum file-system size in logical eraseblocks
# the resulting FS may be put on volumes up to about 300MiB (253952 multiplied by 1240)
UBI_VOLNAME = "system"
MKUBIFS_ARGS = "-F -m 4096 -e 253952 -c 1240"
UBINIZE_ARGS = "-m 4096 -p 256KiB -s 4096 -O 4096"

IMAGE_ROOTFS_EXTRA_SPACE = "0"
KERNEL_BOOTARGS = "root=/dev/system rootfstype=ext4"

do_rootfs[depends] += "android-tools-native:do_populate_sysroot"
#PREFERRED_VERSION_ell = "0.22"

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
    kernel_version=$(ls ${IMAGE_ROOTFS}/lib/modules/)

    if [ ! -f ${IMAGE_ROOTFS}/etc/wifi/rtk_station_mode ];then
        touch ${IMAGE_ROOTFS}/etc/wifi/rtk_station_mode
    fi

    if [ ! -f ${IMAGE_ROOTFS}/processing_graph_dot.txt ];then
        touch ${IMAGE_ROOTFS}/processing_graph_dot.txt
    fi


    if [ ! -f ${IMAGE_ROOTFS}/usr/bin/hdcp_tx22 ];then
        touch ${IMAGE_ROOTFS}/usr/bin/hdcp_tx22
        chmod +x ${IMAGE_ROOTFS}/usr/bin/hdcp_tx22
    fi

    if [ ! -f ${IMAGE_ROOTFS}/lib/modules/$kernel_version/kernel/media/dovi.ko ];then
        touch ${IMAGE_ROOTFS}/lib/modules/$kernel_version/kernel/media/dovi.ko
    fi

    if [ -f ${IMAGE_ROOTFS}/${systemd_unitdir}/system/data-lib-modules-kernelVersionAmlogic-kernel-media-dovi.ko.service ];then
        sed -i "s/kernelVersionAmlogic/$kernel_version/g" ${IMAGE_ROOTFS}/${systemd_unitdir}/system/data-lib-modules-kernelVersionAmlogic-kernel-media-dovi.ko.service
    fi

    if [ -f ${IMAGE_ROOTFS}/.autorelabel ];then
        rm -f ${IMAGE_ROOTFS}/.autorelabel
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "${@bb.utils.contains('READONLY', 'y', 'process_for_read_only_rootfs; ', '', d)}"
