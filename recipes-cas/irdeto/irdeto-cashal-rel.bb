DESCRIPTION = "irdeto-cashal-rel"
SECTION = "irdeto-cashal-rel"
LICENSE = "AMLOGIC"
PV = "git${SRCPV}"
PR = "r0"

#Only enable it in OpenLinux
#SRC_URI:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'irdeto', 'git://${AML_GIT_ROOT_OP}/irdeto-cashal-rel.git;protocol=${AML_GIT_ROOT_PROTOCOL};branch=projects/openlinux/source','', d)}"
SRC_URI:append = " ${@get_patch_list_with_path('${COREBASE}/aml-patches/../vendor/irdeto/irdeto-cashal-rel')}"

DEPENDS = "liblog aml-secmem aml-mediahal-sdk irdeto-sdk aml-cas-hal cjson optee-userspace"
RDEPENDS:${PN} += ""

PN = 'irdeto-cashal-rel'
SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"
EXTRA_OEMAKE="TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_HOST}"

do_compile () {
    oe_runmake  -C ${S} all
}

do_install() {

    install -d -m 0644 ${D}/${libdir}
    install -d -m 0644 ${D}/etc/cas/irdeto/cadata

    touch ${D}/${libdir}/libird_dvb.so

    if [ -e ${S}/libird_dvb.so ] ; then
        install -D -m 0644 ${S}/libird_dvb.so ${D}/${libdir}
    fi

    for file in `ls -a ${S}/data/`
    do
        if [ "${file##*.}" = "dat" ]; then
            install -D -m 0644 ${S}/data/${file} ${D}/etc/cas/irdeto/cadata
        fi
    done

}

FILES:${PN} = "${libdir}/* /usr/lib/* ${bindir}/* /etc/cas/irdeto/cadata/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
