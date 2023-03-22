do_install_append () {
  if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
    cat << EOF >> ${D}${sysconfdir}/profile
# workaround to clear resize output
read -t 0.1 -n 10000 discard
echo -e "\033[1K"
# set pager
export PAGER=/bin/cat
export SYSTEMD_PAGER=/bin/cat
# Set locale
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
export LANGUAGE=en_US.UTF-8
EOF
  fi
}

do_install_append_sc2 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory

    vendor_dev="vendor"
    # if dm-verity is enabled, mount /dev/mapper/vendor(/dev/dm-1) as ro
    if ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'true', 'false', d)}; then
        vendor_dev="dm-1"
    elif ${@bb.utils.contains('DISTRO_FEATURES', 'absystem', 'true', 'false', d)}; then
        vendor_dev="vendor_a"
    fi

    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/${vendor_dev}     /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF

    if ${@bb.utils.contains('DISTRO_FEATURES', 'OverlayFS', 'false', 'true', d)}; then
        cat >> ${D}${sysconfdir}/fstab <<EOF
            tmpfs                /var/cache        tmpfs      defaults,nosuid,nodev,noexec              0  0
EOF
        sed -i '/^ *\/dev\/root/ d' ${D}${sysconfdir}/fstab
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'true', 'false', d)}; then
        sed -i '$a \  /dev/tee          /tee/         ext4        defcontext=system_u:object_r:usr_t,defaults,x-systemd.automount,x-systemd.mount-timeout=10s,x-systemd.requires=ext4format@tee.service        0        0' ${D}${sysconfdir}/fstab
    else
        sed -i '$a \  /dev/tee          /tee/         ext4        defaults,x-systemd.automount,x-systemd.mount-timeout=10s,x-systemd.requires=ext4format@tee.service        0        0' ${D}${sysconfdir}/fstab
    fi
}
FILES_${PN}_append_sc2 = " /vendor/* /factory/* "
dirs755_append_sc2 = " /vendor /factory "

#/*-----------------------S4 STB--------------------------------------*/
do_install_append_s4 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory

if ${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'true', 'false', d)}; then
    vendor_dev="ubi1_0"
    if [ "${ROOTFS_TYPE}" = "squashfs" ]; then
        vendor_dev="mtdblock12"
    fi
    # if dm-verity is enabled, mount /dev/mapper/vendor(/dev/dm-1) as ro
    if ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'true', 'false', d)}; then
        vendor_dev="dm-1"
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'zapper', 'false', 'true', d)}; then
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/${vendor_dev}     /vendor                    auto       defaults              0  0
EOF
    fi

    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/mtdblock5         /factory                   yaffs2     defaults              0  0
 /dev/mtdblock6         /tee                       yaffs2     defaults              0  0
EOF
else
    vendor_dev="vendor"
    # if dm-verity is enabled, mount /dev/mapper/vendor(/dev/dm-1) as ro
    if ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'true', 'false', d)}; then
        vendor_dev="dm-1"
    elif ${@bb.utils.contains('DISTRO_FEATURES', 'absystem', 'true', 'false', d)}; then
        vendor_dev="vendor_a"
    fi

    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/${vendor_dev}     /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
fi


    if ${@bb.utils.contains('DISTRO_FEATURES', 'OverlayFS', 'false', 'true', d)}; then
        cat >> ${D}${sysconfdir}/fstab <<EOF
            tmpfs                /var/cache        tmpfs      defaults,nosuid,nodev,noexec              0  0
EOF
        sed -i '/^ *\/dev\/root/ d' ${D}${sysconfdir}/fstab
    fi

if ${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'false', 'true', d)}; then
    if ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'true', 'false', d)}; then
        sed -i '$a \  /dev/tee          /tee/         ext4        defcontext=system_u:object_r:usr_t,defaults,x-systemd.automount,x-systemd.mount-timeout=10s,x-systemd.requires=ext4format@tee.service        0        0' ${D}${sysconfdir}/fstab
    else
        sed -i '$a \  /dev/tee          /tee/         ext4        defaults,x-systemd.automount,x-systemd.mount-timeout=10s,x-systemd.requires=ext4format@tee.service        0        0' ${D}${sysconfdir}/fstab
    fi
fi
}
FILES_${PN}_append_s4 = " /vendor/* /factory/* "
dirs755_append_s4 = " /vendor /factory "

#/*-----------------------T5D TV----------------------------------*/
do_install_append_t5d () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory

    vendor_dev="vendor"
    # if dm-verity is enabled, mount /dev/mapper/vendor(/dev/dm-1) as ro
    if ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'true', 'false', d)}; then
        vendor_dev="dm-1"
    elif ${@bb.utils.contains('DISTRO_FEATURES', 'absystem', 'true', 'false', d)}; then
        vendor_dev="vendor_a"
    fi

    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/${vendor_dev}     /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF

    if ${@bb.utils.contains('DISTRO_FEATURES', 'OverlayFS', 'false', 'true', d)}; then
        cat >> ${D}${sysconfdir}/fstab <<EOF
            tmpfs                /var/cache        tmpfs      defaults,nosuid,nodev,noexec              0  0
EOF
        sed -i '/^ *\/dev\/root/ d' ${D}${sysconfdir}/fstab
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'true', 'false', d)}; then
        sed -i '$a \  /dev/tee          /tee/         ext4        defcontext=system_u:object_r:usr_t,defaults,x-systemd.automount,x-systemd.mount-timeout=10s,x-systemd.requires=ext4format@tee.service        0        0' ${D}${sysconfdir}/fstab
    else
        sed -i '$a \  /dev/tee          /tee/         ext4        defaults,x-systemd.automount,x-systemd.mount-timeout=10s,x-systemd.requires=ext4format@tee.service        0        0' ${D}${sysconfdir}/fstab
    fi
}
FILES_${PN}_append_t5d = " /vendor/* /factory/* "
dirs755_append_t5d = " /vendor /factory "

#/*-----------------------T5W TV----------------------------------*/
do_install_append_t5w () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory

    vendor_dev="vendor"
    # if dm-verity is enabled, mount /dev/mapper/vendor(/dev/dm-1) as ro
    if ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'true', 'false', d)}; then
        vendor_dev="dm-1"
    elif ${@bb.utils.contains('DISTRO_FEATURES', 'absystem', 'true', 'false', d)}; then
        vendor_dev="vendor_a"
    fi

    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/${vendor_dev}     /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF

    if ${@bb.utils.contains('DISTRO_FEATURES', 'OverlayFS', 'false', 'true', d)}; then
        cat >> ${D}${sysconfdir}/fstab <<EOF
            tmpfs                /var/cache        tmpfs      defaults,nosuid,nodev,noexec              0  0
EOF
        sed -i '/^ *\/dev\/root/ d' ${D}${sysconfdir}/fstab
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'true', 'false', d)}; then
        sed -i '$a \  /dev/tee          /tee/         ext4        defcontext=system_u:object_r:usr_t,defaults,x-systemd.automount,x-systemd.mount-timeout=10s,x-systemd.requires=ext4format@tee.service        0        0' ${D}${sysconfdir}/fstab
    else
        sed -i '$a \  /dev/tee          /tee/         ext4        defaults,x-systemd.automount,x-systemd.mount-timeout=10s,x-systemd.requires=ext4format@tee.service        0        0' ${D}${sysconfdir}/fstab
    fi
}
FILES_${PN}_append_t5w = " /vendor/* /factory/* "
dirs755_append_t5w = " /vendor /factory "

#/*-----------------------T3 TV----------------------------------*/
do_install_append_t3 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
}
FILES_${PN}_append_t3 = " /vendor/* /factory/* "
dirs755_append_t3 = " /vendor /factory "

#/*-----------------------T7----------------------------------*/
do_install_append_t7 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
}
FILES_${PN}_append_t7 = " /vendor/* /factory/* "
dirs755_append_t7 = " /vendor /factory "

INSANE_SKIP_${PN} = "dev-so"
