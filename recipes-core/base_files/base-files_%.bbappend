do_install_append_sc2 () {
    mkdir -p ${D}/vendor
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
EOF
}
FILES_${PN}_append_sc2 = " /vendor/* "
dirs755_append_sc2 = " /vendor "

#/*-----------------------S4 STB--------------------------------------*/
do_install_append_s4 () {
    mkdir -p ${D}/vendor
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
EOF
}
FILES_${PN}_append_s4 = " /vendor/* "
dirs755_append_s4 = " /vendor "

#/*-----------------------T5D TV----------------------------------*/
do_install_append_t5d () {
    mkdir -p ${D}/vendor
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
EOF
}
FILES_${PN}_append_t5d = " /vendor/* "
dirs755_append_t5d = " /vendor "

#/*-----------------------p1 pxp--------------------------------------*/
do_install_append_p1 () {
    mkdir -p ${D}/vendor
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
EOF
}
FILES_${PN}_append_p1 = " /vendor/* "
dirs755_append_p1 = " /vendor "

INSANE_SKIP_${PN} = "dev-so"
