SUMMARY = "Volatile bind mount setup and configuration for read-only-rootfs"
DESCRIPTION = "${SUMMARY}"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.MIT;md5=5750f3aa4ea2b00c2bf21b2b2a7b714d"
S = "${WORKDIR}"

SRC_URI = "\
    file://mount-copybind \
    file://COPYING.MIT \
    file://volatile-binds.service.in \
    file://var-lib.mount \
"

inherit allarch systemd features_check

REQUIRED_DISTRO_FEATURES = "systemd"

VOLATILE_BINDS ?= "\
    /var/volatile/lib /var/lib\n\
"
VOLATILE_BINDS[type] = "list"
VOLATILE_BINDS[separator] = "\n"

VOLATILE_BINDS = "/data/var/lib /var/lib\n"
VOLATILE_BINDS .= "/data/etc/bluetooth /etc/bluetooth\n"
VOLATILE_BINDS .= "/data/etc/hosts /etc/hosts\n"
VOLATILE_BINDS .= "/data/etc/dropbear /etc/dropbear\n"
VOLATILE_BINDS .= "/data/etc/wifi /etc/wifi\n"
VOLATILE_BINDS .= "/data/etc/ld.so.cache /etc/ld.so.cache\n"
VOLATILE_BINDS .= "/data/etc/systemd/system/sysinit.target.wants /etc/systemd/system/sysinit.target.wants\n"
VOLATILE_BINDS .= "/data/vendor/etc/tvconfig/pq /vendor/etc/tvconfig/pq\n"

EXTRA_BINDS = "/data/usr/bin/hdcp_tx22 /usr/bin/hdcp_tx22\\n"
EXTRA_BINDS .= "/data/lib/firmware/hdcp/firmware.le /lib/firmware/hdcp/firmware.le\\n"
EXTRA_BINDS .= "/data/usr/lib/libdolbyms12.so /usr/lib/libdolbyms12.so\\n"
EXTRA_BINDS .= "/data/usr/lib/libHwAudio_dtshd.so /usr/lib/libHwAudio_dtshd.so\\n"
EXTRA_BINDS .= "/data/usr/lib/libHwAudio_dcvdec.so /usr/lib/libHwAudio_dcvdec.so\\n"

VOLATILE_BINDS .= "${@bb.utils.contains('RELEASE_MODE', 'PROD', '', '${EXTRA_BINDS}', d)}"

def volatile_systemd_services(d):
    services = []
    for line in oe.data.typed_value("VOLATILE_BINDS", d):
        if not line:
            continue
        what, where = line.split(None, 1)
        services.append("%s.service" % what[1:].replace("/", "-"))
    return " ".join(services)

SYSTEMD_SERVICE:${PN} = "${@volatile_systemd_services(d)}"

FILES:${PN} += "${systemd_unitdir}/system/*.service"

do_compile () {
    while read spec mountpoint; do
        if [ -z "$spec" ]; then
            continue
        fi

        servicefile="${spec#/}"
        servicefile="$(echo "$servicefile" | tr / -).service"
        sed -e "s#@what@#$spec#g; s#@where@#$mountpoint#g" \
            -e "s#@whatparent@#${spec%/*}#g; s#@whereparent@#${mountpoint%/*}#g" \
            volatile-binds.service.in >$servicefile
    done <<END
${@d.getVar('VOLATILE_BINDS', True).replace("\\n", "\n")}
END

    if [ -e var-volatile-lib.service ]; then
        # As the seed is stored under /var/lib, ensure that this service runs
        # after the volatile /var/lib is mounted.
        sed -i -e "/^Before=/s/\$/ systemd-random-seed.service/" \
               -e "/^WantedBy=/s/\$/ systemd-random-seed.service/" \
               var-volatile-lib.service
    fi

    if [ -e tmp-snmpd.conf.service ]; then
        sed -i -e '/^ConditionPathIsReadWrite=!/d' tmp-snmpd.conf.service
    fi
}
do_compile[dirs] = "${WORKDIR}"

do_install () {
    install -d ${D}${base_sbindir}
    install -m 0755 mount-copybind ${D}${base_sbindir}/

    install -d ${D}${systemd_unitdir}/system
    for service in ${SYSTEMD_SERVICE:${PN}}; do
        install -m 0644 $service ${D}${systemd_unitdir}/system/
    done
    install -m 0644 ${WORKDIR}/var-lib.mount ${D}${systemd_unitdir}/system/
}

do_install:append() {
    if [ -f ${D}${systemd_unitdir}/system/data-var-lib.service ];then
        sed -i '/Before=/ s/$/ systemd-rfkill\.service/g' ${D}${systemd_unitdir}/system/data-var-lib.service
    fi
    rm -f ${D}${systemd_unitdir}/system/var-lib.mount

    if [ -f ${D}${systemd_unitdir}/system/data-vendor-etc-tvconfig-pq.service ]; then
        # We want this mount to exist even if /vendor is mounted in read-write mode.
        sed -i -e '/^ConditionPathIsReadWrite=!/d' ${D}${systemd_unitdir}/system/data-vendor-etc-tvconfig-pq.service
    fi
}

do_install[dirs] = "${WORKDIR}"

SYSTEMD_SERVICE:${PN} += "var-lib.mount"
SYSTEMD_SERVICE:${PN}:remove += "var-lib.mount"
FILES:${PN}:remove += "${systemd_unitdir}/system/var-lib.mount"
