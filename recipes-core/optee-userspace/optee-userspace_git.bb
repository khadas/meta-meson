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

    echo "PLATFORM_TDK_VERSION is ${PLATFORM_TDK_VERSION}"
    case "${PLATFORM_TDK_VERSION}" in
    "v3.8.0")
        install -m 0755 ${S}/${ARM_TARGET}/lib/libteec.so ${D}${libdir}/libteec.so.1.0
        ln -s libteec.so.1.0 ${D}${libdir}/libteec.so.1
        ln -s libteec.so.1 ${D}${libdir}/libteec.so
    ;;
    "v3.18.0")
        install -m 0755 ${S}/${ARM_TARGET}/lib/libckteec.so.0.1.0 ${D}${libdir}
        ln -s libckteec.so.0.1.0 ${D}${libdir}/libckteec.so.0.1
        ln -s libckteec.so.0.1 ${D}${libdir}/libckteec.so.0
        ln -s libckteec.so.0 ${D}${libdir}/libckteec.so
        install -m 0755 ${S}/${ARM_TARGET}/lib/libseteec.so.0.1.0 ${D}${libdir}
        ln -s libseteec.so.0.1.0 ${D}${libdir}/libseteec.so.0.1
        ln -s libseteec.so.0.1 ${D}${libdir}/libseteec.so.0
        ln -s libseteec.so.0 ${D}${libdir}/libseteec.so
        install -m 0755 ${S}/${ARM_TARGET}/lib/libteec.so.1.0.0 ${D}${libdir}
        ln -s libteec.so.1.0.0 ${D}${libdir}/libteec.so.1.0
        ln -s libteec.so.1.0 ${D}${libdir}/libteec.so.1
        ln -s libteec.so.1 ${D}${libdir}/libteec.so
    ;;
    esac

    mkdir -p ${D}${includedir}
    install -m 0755 ${S}/${ARM_TARGET}/include/*.h ${D}${includedir}/

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

INSANE_SKIP:${PN} = "ldflags dev-so dev-elf"

