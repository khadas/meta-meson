DESCRIPTION = "irdeto-cashal-rel"
SECTION = "irdeto-cashal-rel"
LICENSE = "AMLOGIC"
PV = "git${SRCPV}"
PR = "r0"

#Only enable it in OpenLinux
#SRC_URI:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'irdeto', 'git://${AML_GIT_ROOT_OP}/irdeto-cashal.git;protocol=${AML_GIT_ROOT_PROTOCOL};branch=projects/openlinux/source','', d)}"
SRC_URI:append = " ${@get_patch_list_with_path('${COREBASE}/aml-patches/../vendor/irdeto/irdeto-cashal-rel')}"

DEPENDS = "liblog irdeto-sdk aml-cas-hal cjson optee-userspace"
RDEPENDS:${PN} += ""

EX_CFLAGS = "-DPVR_CRYPTO_ENABLE -DSECURE_MEMORY_ENABLE"
EX_CFLAGS:ap232 = "-DPVR_CRYPTO_ENABLE -DSECURE_MEMORY_ENABLE"
EX_CFLAGS:aq2432 = ""
EX_CFLAGS:bf201 = "-DPVR_CRYPTO_ENABLE"
EX_CFLAGS:bg201 = "-DPVR_CRYPTO_ENABLE -DRELOAD_IFCP_LOADER"

USE_DYNAMIC_CCA_LIBRARY:bf201 = "1"
USE_DYNAMIC_CCA_LIBRARY:bg201 = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-128m-sei', '0', '1', d)}"

CCA_HARDWARE_NO = "HW05"
CCA_HARDWARE_NO:bf201 = "HW07"
CCA_HARDWARE_NO:bg201 = "HW08"

PN = 'irdeto-cashal-rel'
#SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"
EXTRA_OEMAKE="TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_HOST} EX_CFLAGS='${EX_CFLAGS}' USE_DYNAMIC_CCA_LIBRARY=${USE_DYNAMIC_CCA_LIBRARY} CCA_HARDWARE_NO=${CCA_HARDWARE_NO}"

do_compile () {
    oe_runmake  -C ${S} all
}

do_install() {

    install -d -m 0644 ${D}/${libdir}
    install -d -m 0644 ${D}/etc/cas/irdeto/cadata

    touch ${D}/${libdir}/libird_dvb.so

    if [ -e ${S}/libird_dvb.so ] ; then
        install -D -m 0644 ${S}/libird_dvb.so ${D}/${libdir}
        install -D -m 0644 ${S}/libird_glue_impl.a ${D}/${libdir}
        install -D -m 0644 ${S}/libird_spi_impl.a ${D}/${libdir}
    fi

    if [ -e ${S}/libird_cca.so ] ; then
        install -D -m 0644 ${S}/libird_cca.so ${D}/${libdir}
    fi

    if [ -e ${S}/cca/config/device_info.json ] ; then
        install -D -m 0644 ${S}/cca/config/device_info.json ${D}/etc/cas/irdeto
    fi

    for file in `ls -a ${S}/cca/data/`
    do
        if [ "${file##*.}" = "dat" ]; then
            install -D -m 0644 ${S}/cca/data/${file} ${D}/etc/cas/irdeto/cadata
        fi
    done

    for file in `ls -a ${S}/cca/data/`
    do
        if [ "${file##*.}" = "bin" ]; then
            install -D -m 0644 ${S}/cca/data/${file} ${D}/etc/cas/irdeto/cadata
        fi
    done

}

FILES:${PN} = "${libdir}/* /usr/lib/* ${bindir}/* /etc/cas/irdeto/cadata/* /etc/cas/irdeto/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
