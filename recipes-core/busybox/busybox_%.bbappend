FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'disable-syslog', ' file://nosyslogd.cfg ', ' file://syslog-extra.cfg ', d)}"

