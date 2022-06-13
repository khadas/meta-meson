SUMMARY = "test-tools"
LICENSE = "CLOSED"

SRC_URI += "file://test_tools.service \
            file://board_info         \
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

    case ${MACHINE_ARCH} in
    mesona213y*)
        echo "M8B M805 M102" > ${WORKDIR}/board_info
    ;;
    mesons4*)
        echo "S4 S905Y4 AP222" > ${WORKDIR}/board_info
    ;;
    mesonsc2*)

        echo "SC2 S905X4 AH212" > ${WORKDIR}/board_info
    ;;
    *)
    ;;
    esac
    install -d ${D}/etc/
    install -m 0644 ${WORKDIR}/board_info ${D}/etc/

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/test_tools.service ${D}/${systemd_unitdir}/system/

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/test_tools_autoreboot.service ${D}/${systemd_unitdir}/system/
}

FILES_${PN} = "/test_plan/* /etc/* "
SYSTEMD_SERVICE_${PN} = "test_tools.service test_tools_autoreboot.service"
