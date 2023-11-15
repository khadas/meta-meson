DESCRIPTION = "irdeto-sdk"
SECTION = "irdeto-sdk"
LICENSE = "CLOSE"
PV = "git${SRCPV}"
PR = "r0"

#Only enable it in OpenLinux
#IRDETO_BRANCH = "TBD"
#IRDETO_BRANCH:sc2 = "openlinux/sc2-msr4-linux"
#IRDETO_BRANCH:s4 = "openlinux/s4d-msr4-linux"
#SRC_URI:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'irdeto', 'git://${AML_GIT_ROOT_OP}/irdeto-sdk.git;protocol=${AML_GIT_ROOT_PROTOCOL};branch=${IRDETO_BRANCH}','', d)}"
SRC_URI:append = " ${@get_patch_list_with_path('${COREBASE}/aml-patches/vendor/irdeto/irdeto-sdk')}"

PN = 'irdeto-sdk'
SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

IRDETO_PATH = "TBD"
IRDETO_PATH:sc2 = "v3/dev/S905C2/signed"
IRDETO_PATH:s4 = "s905c3"
IRDETO_PATH:aq2432 = "s805c3"
IRDETO_PATH:bf201 = "s805c1a"

REE_ONLY = "${@bb.utils.contains('DISTRO_FEATURES', 'irdeto-ree-only', 'true', 'false', d)}"

do_install() {
    install -d -m 0755 ${D}/usr/lib
    install -d -m 0755 ${D}/lib/optee_armtz
    install -d -m 0755 ${D}/usr/include
    install -D -m 0644 ${S}/lib/ca/libirdetoca.so ${D}/usr/lib
    if [ "${REE_ONLY}" = "false" ]; then
        install -D -m 0644 ${S}/lib/ta/${IRDETO_PATH}/b64fd559-658d-48a4-bbc7-b95d8663f457.ta ${D}/lib/optee_armtz
        install -D -m 0644 ${S}/lib/ta/${IRDETO_PATH}/1d0f7170-3209-481c-b4ad-8fca95113b71.ta ${D}/lib/optee_armtz
    fi
    install -D -m 0644 ${S}/include/*.h ${D}/usr/include
}

FILES:${PN} = "${libdir}/* /lib/optee_armtz/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
