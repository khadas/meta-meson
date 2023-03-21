FILESEXTRAPATHS_prepend := "${THISDIR}/files/:"
SRC_URI_append = " file://70-keyboard.hwdb"
SRC_URI_append = " file://network/20-ethernet.network file://network/21-wlan.network"
SRC_URI_append = " file://0027-fix-udisk-can-not-unmount-properly.patch"

PACKAGECONFIG_remove = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', 'hostnamed networkd nss-resolve resolved randomseed timesyncd', '', d)}"

do_install_append() {
sed -i -e 's/ExecStart=/ExecStart=-/' ${D}/lib/systemd/system/systemd-modules-load.service
cat >> ${D}/lib/systemd/system/systemd-modules-load.service <<EOF
ExecStartPost=-/bin/sh -c '/etc/modules-load.sh'
EOF
install -m 0644 ${WORKDIR}/70-keyboard.hwdb ${D}/lib/udev/hwdb.d/70-keyboard.hwdb
install -d ${D}/etc/systemd/network
install -m 0644 ${WORKDIR}/network/20-ethernet.network ${D}/etc/systemd/network
install -m 0644 ${WORKDIR}/network/21-wlan.network ${D}/etc/systemd/network
sed -i '/HandlePowerKey/c\HandlePowerKey=ignore' ${D}/etc/systemd/logind.conf
sed -i '/pam_selinux/s/^/#&/g' ${D}/etc/pam.d/systemd-user
#below settings to avoid journalctl logs drop
sed -i '$a\RateLimitInterval=0' ${D}/etc/systemd/journald.conf
sed -i '$a\RateLimitBurst=0' ${D}/etc/systemd/journald.conf
}

do_install_append_aq2432() {
  sed -i '$a RuntimeMaxUse=64K' ${D}/etc/systemd/journald.conf
  sed -i '$a RuntimeMaxFileSize=64K' ${D}/etc/systemd/journald.conf
}
