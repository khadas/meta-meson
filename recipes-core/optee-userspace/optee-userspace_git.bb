DESCRIPTION = "optee "

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
include ../../recipes-shared/optee.inc

#SRC_URI:append = " ${@get_patch_list('${THISDIR}/${PN}')}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

PROVIDES = "optee-userspace-securebl32"
PACKAGES =+ "\
    ${PN}-securebl32 \
    "
#PR = "${INC_PR}.1"

ARM_TARGET="ca_export_arm"
ARM_TARGET:aarch64 ="ca_export_arm64"

do_install() {
    mkdir -p ${D}${bindir}
    install -m 0755 ${S}/${ARM_TARGET}/bin/tee-supplicant ${D}${bindir}
    install -m 0755 ${S}/${ARM_TARGET}/bin/tee_stest ${D}${bindir}

    mkdir -p ${D}${libdir}
    install -m 0755 ${S}/${ARM_TARGET}/lib/libteec.so ${D}${libdir}/libteec.so.1.0

    mkdir -p ${D}${includedir}
    install -m 0755 ${S}/${ARM_TARGET}/include/*.h ${D}${includedir}/

    ln -s libteec.so.1 ${D}${libdir}/libteec.so
    ln -s libteec.so.1.0 ${D}${libdir}/libteec.so.1
    echo "TDK_VERSION is ${TDK_VERSION}"
    case "${TDK_VERSION}" in
    "v2.4")
        mkdir -p ${D}${datadir}/tdk/secureos
        cp -rf ${S}/secureos/* ${D}${datadir}/tdk/secureos
    ;;
    esac
}

FILES:${PN} += " ${libdir}/* "

FILES:${PN} += "${bindir}/tee-supplicant"

FILES:${PN}-dev = ""
FILES:${PN}-securebl32 += " /usr/share/tdk/secureos/*"

INSANE_SKIP:${PN} = "ldflags dev-so dev-elf installed-vs-shipped"

