do_install_append_sc2-5.4 () {
    mkdir -p ${D}/vendor
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
EOF
}
FILES_${PN}_append_sc2-5.4 = " /vendor/* "
dirs755_append_sc2-5.4 = " /vendor "

#/*-----------------------S4 STB--------------------------------------*/
do_install_append_s4 () {
    mkdir -p ${D}/vendor
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
EOF
}
FILES_${PN}_append_s4 = " /vendor/* "
dirs755_append_s4 = " /vendor "

#/*-----------------------T5D-K5.4 TV----------------------------------*/
do_install_append_t5d-5.4 () {
    mkdir -p ${D}/vendor
    cat >> ${D}${sysconfdir}/fstab <<EOF
 /dev/vendor            /vendor                    auto       defaults              0  0
EOF
}
FILES_${PN}_append_t5d-5.4 = " /vendor/* "
dirs755_append_t5d-5.4 = " /vendor "

INSANE_SKIP_${PN} = "dev-so"
