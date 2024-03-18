SUMMARY = "aml hdcp service"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/hdcp;protocol=${AML_GIT_PROTOCOL};branch=projects/buildroot/tdk-v2.4"
SRC_URI:append = " file://aml_hdcp.service"
SRC_URI:append = " file://aml_hdcp.init"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/hdcp')}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git"
DEPENDS += "optee-userspace"

inherit autotools pkgconfig systemd update-rc.d
ARM_TARGET:aarch64 = "64"

INITSCRIPT_NAME = "aml_hdcp"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 6 1 ."

do_install() {
    # install headers
    install -d -m 0755 ${D}/lib/firmware/hdcp/
    touch ${D}/lib/firmware/hdcp/firmware.le
    install -d -m 0755 ${D}/lib/optee_armtz
    install -d -m 0755 ${D}/usr/bin
    install -D -m 0755 ${S}/ca/${PLATFORM_TDK_VERSION}/bin${ARM_TARGET}/tee_hdcp ${D}/usr/bin/
    install -D -m 0755 ${S}/ta/${TDK_VERSION}/*.ta ${D}/lib/optee_armtz/
    if [ "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "yes", "no", d)}" = "yes"  ]; then
        install -D -m 0644 ${WORKDIR}/aml_hdcp.service ${D}${systemd_unitdir}/system/aml_hdcp.service
        if [ "${@bb.utils.contains("DISTRO_FEATURES", "amlogic-tv", "yes", "no", d)}" = "yes"  ]; then
            sed -i '/ExecStartPre=/c\ExecStartPre=/usr/bin/tee_hdcp -i /lib/firmware/hdcp/firmware.le -o /tmp/firmware.le' ${D}${systemd_unitdir}/system/aml_hdcp.service
            sed -i "/ExecStart=/c\ExecStart=/bin/sh -c '/usr/bin/hdcp_rx22 -f /tmp/firmware.le&'" ${D}${systemd_unitdir}/system/aml_hdcp.service
        fi
    fi

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/aml_hdcp.init ${D}${sysconfdir}/init.d/aml_hdcp
}

SYSTEMD_SERVICE:${PN} = "aml_hdcp.service "
FILES:${PN} += "/lib/optee_armtz/* /usr/bin/* /lib/firmware/hdcp/* ${sysconfdir}"
INSANE_SKIP:${PN} = "ldflags dev-so dev-elf"
INSANE_SKIP:${PN}-dev = "ldflags dev-so dev-elf"
