FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'disable-syslog', ' file://nosyslogd.cfg ', ' file://syslog-extra.cfg ', d)}"
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'use-mdev', ' file://mdev.cfg  file://mdev ', ' ', d)}"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','use-mdev','true','false', d)}; then
	cat << EOF >> ${D}${sysconfdir}/mdev.conf

#dvb device rules
dvb/adapter0/ca([0-9]) 0:0 660 >dvb0.ca%1
dvb/adapter0/demux([0-9]) 0:0 660 >dvb0.demux%1
dvb/adapter0/dvr([0-9]) 0:0 660 >dvb0.dvr%1
dvb/adapter0/frontend([0-9]) 0:0 660 >dvb0.frontend%1
EOF

    install -m 0755  ${WORKDIR}/mdev  ${D}${sysconfdir}/init.d/
    fi
}

