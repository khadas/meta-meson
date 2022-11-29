SUMMARY = "AML Efuse TA"

LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

SRC_URI_append = "${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/efuse')}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

inherit autotools pkgconfig

do_install() {
    install -d -m 0644 ${D}/lib/optee_armtz

    if [ "$(echo ${TDK_VERSION} | grep "v3.8")" != "" ]
    then
        TDK_VERSION_NEW=`echo ${TDK_VERSION} | sed 's/v3.8/v3/'`
        install -D -m 0755 ${S}/ta/${TDK_VERSION_NEW}/*.ta ${D}/lib/optee_armtz/
    fi
}

FILES_${PN} += "/lib/optee_armtz/*"
FILES_${PN}-dev = " "
INSANE_SKIP_${PN} = "ldflags dev-so dev-elf"
INSANE_SKIP_${PN}-dev = "ldflags dev-so dev-elf"
ALLOW_EMPTY_${PN} = "1"

