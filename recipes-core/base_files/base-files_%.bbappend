do_install_append_sc2 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
}
FILES_${PN}_append_sc2 = " /vendor/* /factory/* "
dirs755_append_sc2 = " /vendor /factory "

#/*-----------------------S4 STB--------------------------------------*/
do_install_append_s4 () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
}
FILES_${PN}_append_s4 = " /vendor/* /factory/* "
dirs755_append_s4 = " /vendor /factory "

#/*-----------------------T5D TV----------------------------------*/
do_install_append_t5d () {
    mkdir -p ${D}/vendor
    mkdir -p ${D}/factory
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
 /dev/factory           /factory                   auto       defaults              0  0
EOF
}
FILES_${PN}_append_t5d = " /vendor/* /factory/* "
dirs755_append_t5d = " /vendor /factory "

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

INSANE_SKIP_${PN} = "dev-so"
