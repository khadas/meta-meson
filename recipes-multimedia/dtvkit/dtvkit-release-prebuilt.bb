SUMMARY = "amlogic dtvkit prebuilt"
LICENSE = "CLOSED"
DEPENDS = "aml-mp-sdk "
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'dtvkit-src', ' android-rpcservice', '', d)} "
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', ' libtinyxml2', ' libxml2', d)} "
RDEPENDS:${PN} = "aml-mp-sdk aml-mediahal-sdk  aml-subtitleserver aml-libdvr jsoncpp liblog libjpeg-turbo libpng zlib freetype sqlite3 freetype openssl "
RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', ' libtinyxml2', ' libxml2', d)} "
RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-binderfs', '', ' libbinder', d)} "

inherit systemd
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'disable-binderfs', ' ', 'update-rc.d', d)}

INITSCRIPT_NAME = "dtvkit"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 6 1 ."

SYSTEMD_SERVICE:${PN} = "dtvkit.service"
FILES:${PN} += "${systemd_unitdir}/system/dtvkit.service"

ARM_TARGET = "lib32"
ARM_TARGET:aarch64 = "lib64"

#SRC_URI = "git://${AML_GIT_ROOT}/DTVKit/releaseDTVKit;protocol=${AML_GIT_PROTOCOL};branch=linux-rdk"
#use head version, ?= conditonal operator can be control revision in external rdk-next.conf like configuration file
#SRCREV ?= "${AUTOREV}"
SRC_URI +="file://dtvkit.service"
SRC_URI +="file://dtvkit_low_mem.service"
SRC_URI +="file://dtvkit.init "

TDK_VERSION:t5w = "v3.8/dev/T962D4"

CONFIG = "config_ah212.xml"
CONFIG:ah212 = "config_ah212.xml"
CONFIG:ah232 = "config_ah232.xml"
CONFIG:ap222 = "config_ap222.xml"
CONFIG:aq222 = "config_aq222.xml"
CONFIG:ap232 = "config_ap232.xml"
CONFIG:ah212-pip = "config_ah212_pip.xml"
CONFIG:t5d = "config_t5d.xml"
CONFIG:t5w = "config_t5w.xml"
CONFIG:bf201 = "config_bf201.xml"
CONFIG:bg201 = "config_bg201.xml"

S = "${WORKDIR}/git"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"
do_package_qa[noexec] = "1"
do_install () {
    mkdir -p ${D}/usr/bin
    mkdir -p ${D}/usr/lib
    mkdir -p ${D}/usr/include/dtvkit/inc
    mkdir -p ${D}/usr/include/dtvkit/dvb/inc
    mkdir -p ${D}/usr/include/dtvkit/midware/stb/inc
    mkdir -p ${D}/usr/include/dtvkit/platform/inc
    mkdir -p ${D}/usr/include/dtvkit/hw/inc
    mkdir -p ${D}/lib/optee_armtz
    mkdir -p ${D}/etc/

    install -D -m 0644 ${S}/inc/DVBCore/include/dvbcore/dvb/inc/*.h ${D}/usr/include/dtvkit/dvb/inc
    install -D -m 0644 ${S}/inc/DVBCore/include/dvbcore/platform/inc/*.h ${D}/usr/include/dtvkit/platform/inc
    install -D -m 0644 ${S}/inc/DVBCore/include/dvbcore/inc/*.h ${D}/usr/include/dtvkit/inc
    install -D -m 0644 ${S}/inc/DVBCore/include/dvbcore/midware/stb/inc/*.h ${D}/usr/include/dtvkit/midware/stb/inc

    install -D -m 0644 ${S}/inc/dtvkit-amlogic/include/dtvkit_platform/hw/inc/*.h ${D}/usr/include/dtvkit/hw/inc

    if ${@bb.utils.contains("DISTRO_FEATURES", "dtvkit-src", "false", "true", d)}; then
        install -D -m 0644 ${S}/${ARM_TARGET}/*.so ${D}/${libdir}
        if ${@bb.utils.contains("DISTRO_FEATURES", "disable-binderfs", "false", "true", d)}; then
            install -D -m 0755 ${S}/${ARM_TARGET}/CLIENT ${D}/${bindir}
            if [ -f ${S}/${ARM_TARGET}/dtvkitserver ]; then
                install -D -m 0755 ${S}/${ARM_TARGET}/dtvkitserver ${D}/${bindir}
            fi
        fi
    fi
    install -d ${D}/${systemd_unitdir}/system
    if ${@bb.utils.contains("DISTRO_FEATURES", "zapper", "false", "true", d)}; then
        install -m 0644 ${WORKDIR}/dtvkit.service ${D}/${systemd_unitdir}/system//dtvkit.service
    fi

    install -D -m 0644 ${S}/config/${CONFIG} ${D}/etc/config.xml
    install -D -m 0644 ${S}/config/*.json  ${D}/etc/
    install -D -m 0644 ${S}/version.txt  ${D}/etc/dtvkitserver_releaseinfo.txt
    echo "TDK_VERSION is ${TDK_VERSION}"
    if [ -f ${S}/ta/${TDK_VERSION}/*.ta ];then
       install -D -m 0755 ${S}/ta/${TDK_VERSION}/*.ta ${D}/lib/optee_armtz/
    fi

    if ${@bb.utils.contains("DISTRO_FEATURES", "disable-binderfs", "false", "true", d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/dtvkit.init ${D}${sysconfdir}/init.d/dtvkit
    fi
}

FILES:${PN} = "${libdir}/* ${bindir}/* ${sysconfdir}/* /lib/optee_armtz/* "
FILES:${PN}-dev = "${includedir}/* "

INSANE_SKIP:${PN} = "ldflags already-stripped installed-vs-shipped"
INSANE_SKIP:${PN}-dev = "dev-elf dev-so"
