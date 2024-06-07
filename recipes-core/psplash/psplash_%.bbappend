
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
FILESEXTRAPATHS:prepend := "${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', '${THISDIR}/files/tv:', '${THISDIR}/files/stb:', d)}"

SRC_URI:append = " file://0001-use-fb1-set-alpha-ff.patch"
SRC_URI:append = " file://psplash-quit"
SRC_URI:append = " file://psplash-quit.service"

do_install:append () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'psplash-force-timedout', 'true', 'false', d)}; then
        install -Dm 0755 ${WORKDIR}/psplash-quit ${D}/${bindir}/
        install -Dm 0644 ${WORKDIR}/psplash-quit.service ${D}${systemd_unitdir}/system/
    fi
}

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'psplash-force-timedout', 'psplash-quit.service', '', d)}"

FILES:${PN} += " \
    ${systemd_unitdir}/system/ \
"
