do_install_append () {
  if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
    sed -i '/\/usr\/bin\/resize/ s/^/#/' ${D}${sysconfdir}/profile
    echo "export PAGER=/bin/cat" >> ${D}${sysconfdir}/profile
    echo "export SYSTEMD_PAGER=/bin/cat" >> ${D}${sysconfdir}/profile
  fi
}

do_install_append_sc2 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory
    # if dm-verity is enabled, mount /dev/mapper/vendor(/dev/dm-1) as ro
    if ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'true', 'false', d)}; then
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/dm-1            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
    else
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
    fi
}
FILES_${PN}_append_sc2 = " /vendor/* /factory/* "
dirs755_append_sc2 = " /vendor /factory "

#/*-----------------------S4 STB--------------------------------------*/
do_install_append_s4 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory

    # if dm-verity is enabled, mount /dev/mapper/vendor(/dev/dm-1) as ro
    if ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'true', 'false', d)}; then
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/dm-1            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
    else
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
    fi
}
FILES_${PN}_append_s4 = " /vendor/* /factory/* "
dirs755_append_s4 = " /vendor /factory "

#/*-----------------------T5D TV----------------------------------*/
do_install_append_t5d () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory

    if ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'true', 'false', d)}; then
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/dm-1            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
    else
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
    fi
}
FILES_${PN}_append_t5d = " /vendor/* /factory/* "
dirs755_append_t5d = " /vendor /factory "

#/*-----------------------T5W TV----------------------------------*/
do_install_append_t5w () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory

    if ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'true', 'false', d)}; then
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/dm-1            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
    else
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
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

#/*-----------------------p1 pxp--------------------------------------*/
do_install_append_p1 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
}
FILES_${PN}_append_p1 = " /vendor/* /factory/* "
dirs755_append_p1 = " /vendor /factory "

#/*-----------------------tm2--------------------------------------*/
do_install_append_tm2 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
}
FILES_${PN}_append_tm2 = " /vendor/* /factory/* "
dirs755_append_tm2 = " /vendor /factory "

INSANE_SKIP_${PN} = "dev-so"
