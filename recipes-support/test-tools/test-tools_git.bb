SUMMARY = "test-tools"
LICENSE = "CLOSED"

SRC_URI += "file://test_tools.service \
            file://test_tools_autoreboot.service \
"

DEPENDS = "rsync-native"

SYSTEMD_AUTO_ENABLE = "enable"
inherit systemd pkgconfig

S = "${WORKDIR}/package"

do_install() {
    ${S}/test_plan/install_prebuilt_bin.sh ${TARGET_ARCH} ${S}/test_plan
    install -d ${D}/test_plan/
    rsync -arv ${S}/test_plan/* ${D}/test_plan --exclude=prebuilt --exclude=src --exclude=install_prebuilt_bin.sh

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/test_tools.service ${D}/${systemd_unitdir}/system/

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/test_tools_autoreboot.service ${D}/${systemd_unitdir}/system/
}

FILES:${PN} = "/test_plan/* /etc/* "
SYSTEMD_SERVICE:${PN} = "test_tools.service test_tools_autoreboot.service"
