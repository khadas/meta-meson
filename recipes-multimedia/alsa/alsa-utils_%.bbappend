do_install:append() {
    # alsa-restore will effect amlogic audio configuration, disable it
    rm -rf ${D}${systemd_unitdir}/system/alsa-restore.service
    rm -rf ${D}${systemd_unitdir}/system/sound.target.wants/alsa-restore.service
}
