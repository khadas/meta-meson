do_install:append () {
  if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
    cat << EOF >> ${D}${sysconfdir}/profile
# set pager
export PAGER=cat
export SYSTEMD_PAGER=cat
# Set locale
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
export LANGUAGE=en_US.UTF-8
EOF
  fi
}

do_install:append:sc2 () {
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
FILES:${PN}:append:sc2 = " /vendor/* /factory/* "
dirs755:append:sc2 = " /vendor /factory "

#/*-----------------------S4 STB--------------------------------------*/
do_install:append:s4 () {
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

    if ${@bb.utils.contains('DISTRO_FEATURES', 'vendor-partition', 'true', 'false', d)}; then
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/${vendor_dev}     /vendor                    auto       defaults              0  0
EOF
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'zapper', 'true', 'false', d)}; then
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/mtdblock6         /factory                   yaffs2     defaults              0  0
 /dev/mtdblock7         /tee                       yaffs2     defaults              0  0
EOF
    else
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/mtdblock5         /factory                   yaffs2     defaults              0  0
 /dev/mtdblock6         /tee                       yaffs2     defaults              0  0
EOF
    fi
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
FILES:${PN}:append:s4 = " /vendor/* /factory/* "
dirs755:append:s4 = " /vendor /factory "

#/*-----------------------S7 STB--------------------------------------*/
do_install:append:s7 () {
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

    if ${@bb.utils.contains('DISTRO_FEATURES', 'vendor-partition', 'true', 'false', d)}; then
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/${vendor_dev}     /vendor                    auto       defaults              0  0
EOF
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'zapper', 'true', 'false', d)}; then
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/mtdblock6         /factory                   yaffs2     defaults              0  0
 /dev/mtdblock7         /tee                       yaffs2     defaults              0  0
EOF
    else
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/mtdblock5         /factory                   yaffs2     defaults              0  0
 /dev/mtdblock6         /tee                       yaffs2     defaults              0  0
EOF
    fi
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
        sed -i '$a \  /dev/tee          /tee/         ext4        defcontext=system_u:object_r:usr_t,defaults,x-systemd.mount-timeout=10s,x-systemd.requires=ext4format@tee.service        0        0' ${D}${sysconfdir}/fstab
    else
        sed -i '$a \  /dev/tee          /tee/         ext4        defaults,x-systemd.automount,x-systemd.mount-timeout=10s,x-systemd.requires=ext4format@tee.service        0        0' ${D}${sysconfdir}/fstab
    fi
fi
}
FILES:${PN}:append:s7 = " /vendor/* /factory/* "
dirs755:append:s7 = " /vendor /factory "


#/*-----------------------S1A STB--------------------------------------*/
do_install:append:s1a () {
    mkdir -p ${D}/factory

if ${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'true', 'false', d)}; then
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/mtdblock4         /factory                   yaffs2     defaults              0  0
 /dev/mtdblock5         /tee                       yaffs2     defaults              0  0
EOF
fi

if ${@bb.utils.contains('DISTRO_FEATURES', 'irdeto-downloader', 'true', 'false', d)}; then
    mkdir -p ${D}/cadata
    mkdir -p ${D}/casecure
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/mtdblock10         /casecure                 squashfs     defaults              0  0
 /dev/mtdblock15         /cadata                   yaffs2       defaults              0  0
EOF
fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'OverlayFS', 'false', 'true', d)}; then
        cat >> ${D}${sysconfdir}/fstab <<EOF
            tmpfs                /var/cache        tmpfs      defaults,nosuid,nodev,noexec              0  0
EOF
        sed -i '/^ *\/dev\/root/ d' ${D}${sysconfdir}/fstab
    fi
}
FILES:${PN}:append:s1a = " /factory/* "
dirs755:append:s1a = " /factory "

#/*-----------------------T5D TV----------------------------------*/
do_install:append:t5d () {
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
FILES:${PN}:append:t5d = " /vendor/* /factory/* "
dirs755:append:t5d = " /vendor /factory "

#/*-----------------------T5W TV----------------------------------*/
do_install:append:t5w () {
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
FILES:${PN}:append:t5w = " /vendor/* /factory/* "
dirs755:append:t5w = " /vendor /factory "

#/*-----------------------T3 TV----------------------------------*/
do_install:append:t3 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
}
FILES:${PN}:append:t3 = " /vendor/* /factory/* "
dirs755:append:t3 = " /vendor /factory "

#/*-----------------------T7----------------------------------*/
do_install:append:t7 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
}
FILES:${PN}:append:t7 = " /vendor/* /factory/* "
dirs755:append:t7 = " /vendor /factory "

#/*-----------------------sm1----------------------------------*/
do_install:append:sm1 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
}
FILES:${PN}:append:sm1 = " /vendor/* /factory/* "
dirs755:append:sm1 = " /vendor /factory "
INSANE_SKIP:${PN} = "dev-so"
INSANE_SKIP:${PN} += "empty-dirs"
