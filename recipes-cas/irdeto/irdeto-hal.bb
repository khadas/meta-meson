DESCRIPTION = "irdeto-hal"
SECTION = "irdeto-hal"
LICENSE = "CLOSE"
PV = "git${SRCPV}"
PR = "r0"

#Only enable it in OpenLinux
#IRDETO_BRANCH = "TBD"
#IRDETO_BRANCH:sc2 = "openlinux/sc2-msr4-linux"
#IRDETO_BRANCH:s4 = "openlinux/s4d-msr4-linux"
#SRC_URI:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'irdeto', 'git://${AML_GIT_ROOT_OP}/irdeto-imw.git;protocol=${AML_GIT_ROOT_PROTOCOL};branch=${IRDETO_BRANCH}','', d)}"
SRC_URI:append = " ${@get_patch_list_with_path('${COREBASE}/aml-patches/vendor/irdeto/hal')}"

PN = 'irdeto-hal'
#SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

DEPENDS += "liblog aml-libdvr aml-mediahal-sdk irdeto-sdk optee-userspace"
RDEPENDS:${PN} += "liblog aml-libdvr aml-mediahal-sdk irdeto-sdk optee-userspace"

EXTRA_OEMAKE = "STAGING_DIR=${STAGING_DIR_TARGET} TARGET_DIR=${D} \
          mode=debug \
        "

do_compile () {
    cd ${S}
    oe_runmake -j1 ${EXTRA_OEMAKE} all
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 -D ${S}/target/debug.target.test/idway/IDwayJ.exe ${D}/usr/bin/
    install -m 0755 -D ${S}/target/debug.target.test/idway/startIDwayJ.sh ${D}/usr/bin/

    install -d -m 0644 ${D}/etc/cas/irdeto
    install -D -m 0644 ${S}/prebuilts/conf/irdeto_hal.conf ${D}/etc/cas/irdeto/
}

FILES:${PN} = "${bindir}/* /etc/cas/irdeto/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
