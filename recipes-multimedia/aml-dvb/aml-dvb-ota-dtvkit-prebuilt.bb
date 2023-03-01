SUMMARY = "amlogic dvb ota for dtvkit prebuilt"
LICENSE = "CLOSED"
DEPENDS = "${@bb.utils.contains('DISTRO_FEATURES', 'dtvkit-src', ' android-rpcservice', 'dtvkit-release-prebuilt', d)} "
RDEPENDS_${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'dtvkit-src', ' android-rpcservice', 'dtvkit-release-prebuilt', d)} "

ARM_TARGET = "lib32"
ARM_TARGET_aarch64 = "lib64"

SRCREV ?= "${AUTOREV}"

S = "${WORKDIR}/git"


do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"
do_install () {
    if ${@bb.utils.contains("DISTRO_FEATURES", "dtvkit-src", "false", "true", d)}; then
        install -D -m 0644 ${S}/prebuilt/${ARM_TARGET}/libdtvkit_ota_monitor.so ${D}/${libdir}/libdtvkit_ota_monitor.so
    fi
}

FILES_${PN} = "${libdir}/*  "
FILES_${PN}-dev = "${includedir}/* "

INSANE_SKIP_${PN} = "ldflags already-stripped"
INSANE_SKIP_${PN}-dev = "dev-elf dev-so"
