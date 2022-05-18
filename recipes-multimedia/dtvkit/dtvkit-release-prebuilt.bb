SUMMARY = "amlogic dtvkit prebuilt"
LICENSE = "CLOSED"
DEPENDS = "aml-mp-sdk"
RDEPENDS_${PN} = "aml-mp-sdk"


ARM_TARGET = "lib32"
ARM_TARGET_aarch64 = "lib64"

#SRC_URI = "git://${AML_GIT_ROOT}/DTVKit/releaseDTVKit;protocol=${AML_GIT_PROTOCOL};branch=linux-rdk"
#use head version, ?= conditonal operator can be control revision in external rdk-next.conf like configuration file
SRCREV ?= "${AUTOREV}"

CONFIG = "config_ah212.xml"
CONFIG_ah212 = "config_ah212.xml"
CONFIG_ah232 = "config_ah232.xml"
CONFIG_ap222 = "config_ap222.xml"
CONFIG_aq222 = "config_aq222.xml"
CONFIG_ap232 = "config_ap232.xml"
CONFIG_ah212-pip = "config_ah212_pip.xml"
CONFIG_t5d = "config_t5d.xml"
CONFIG_t5w = "config_t5w.xml"

S = "${WORKDIR}/git"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"
do_install () {
	mkdir -p ${D}/usr/bin
	mkdir -p ${D}/usr/lib
	mkdir -p ${D}/usr/include/dtvkit/inc
	mkdir -p ${D}/usr/include/dtvkit/dvb/inc
	mkdir -p ${D}/usr/include/dtvkit/midware/stb/inc
	mkdir -p ${D}/usr/include/dtvkit/platform/inc
	mkdir -p ${D}/usr/include/dtvkit/hw/inc

	mkdir -p ${D}/etc/

    install -D -m 0644 ${S}/DVBCore/include/dvbcore/dvb/inc/*.h ${D}/usr/include/dtvkit/dvb/inc
    install -D -m 0644 ${S}/DVBCore/include/dvbcore/platform/inc/*.h ${D}/usr/include/dtvkit/platform/inc
    install -D -m 0644 ${S}/DVBCore/include/dvbcore/inc/*.h ${D}/usr/include/dtvkit/inc
    install -D -m 0644 ${S}/DVBCore/include/dvbcore/midware/stb/inc/*.h ${D}/usr/include/dtvkit/midware/stb/inc

    install -D -m 0644 ${S}/dtvkit-amlogic/include/dtvkit_platform/hw/inc/*.h ${D}/usr/include/dtvkit/hw/inc

    if ${@bb.utils.contains("DISTRO_FEATURES", "dtvkit-src", "false", "true", d)}; then
        install -D -m 0644 ${S}/android-rpcservice/${ARM_TARGET}/libdtvkitserver.so ${D}/${libdir}
    fi
    install -D -m 0644 ${S}/android-rpcservice/config/${CONFIG} ${D}/etc/config.xml
    install -D -m 0644 ${S}/android-rpcservice/config/*.json  ${D}/etc/
}

FILES_${PN} = "${libdir}/* ${bindir}/* ${sysconfdir}/*"
FILES_${PN}-dev = "${includedir}/* "

INSANE_SKIP_${PN} = "ldflags already-stripped"
INSANE_SKIP_${PN}-dev = "dev-elf dev-so"
