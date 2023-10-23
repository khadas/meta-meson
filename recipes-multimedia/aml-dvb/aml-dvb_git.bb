SUMMARY = "aml dvb samples"
LICENSE = "LGPL-2.0+"
LIC_FILES_CHKSUM = "file://Doxyfile;md5=c771730fa57fc498cd9dc7d74b84934d"

#SRC_URI = "git://${AML_GIT_ROOT}/dvb.git;protocol=${AML_GIT_PROTOCOL};branch=tv-kernel-4.9"
SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/dvb')}"
DEPENDS = " aml-zvbi sqlite3 alsa-lib"
RDEPENDS:${PN} += "aml-zvbi sqlite3 alsa-lib"

do_configure[noexec] = "1"
inherit autotools pkgconfig
S="${WORKDIR}/git"

EXTRA_OEMAKE="TARGET_DIR=${S} \ 
              TARGET_LDFLAGS=' -lstdc++ -lpthread -lrt -lm -ldl -lc -lgcc -L${STAGING_DIR_TARGET}/usr/lib' \
             " 
do_compile() {
    cd ${S}
    mkdir -p ${S}/usr/lib
    mkdir -p ${S}/usr/bin
    oe_runmake  all
}
do_install() {
   install -d ${D}${libdir}
   install -d ${D}${bindir}
   install -d ${D}${includedir}/libdvbsi
   install -d ${D}${includedir}/amports

    cd ${S}
    install -D -m 0644 ${S}/am_adp/libam_adp.so ${D}${libdir}
    install -D -m 0644 ${S}/include/am_adp/*.h ${D}/usr/include
    install -D -m 0644 ${S}/include/am_adp/libdvbsi/*.h ${D}/usr/include/libdvbsi
    install -D -m 0644 ${S}/android/ndk/include/linux/amports/*.h ${D}${includedir}/amports/
    install -d ${D}${includedir}/am_adp
    install -m 0755 ${S}/include/am_adp/am_evt.h ${D}${includedir}/am_adp
    install -m 0755 ${S}/include/am_adp/am_types.h ${D}${includedir}/am_adp
    install -m 0755 ${S}/include/am_adp/am_userdata.h ${D}${includedir}/am_adp

    install -d ${D}${includedir}/am_adp
    install -D -m 0644 ${S}/include/am_adp/*.h -t ${D}${includedir}/am_adp
    install -d ${D}${includedir}/am_adp/libdvbsi
    install -D -m 0644 ${S}/include/am_adp/libdvbsi/*.h ${D}${includedir}/am_adp/libdvbsi
    install -d ${D}${includedir}/am_adp/libdvbsi/tables
    install -D -m 0644 ${S}/include/am_adp/libdvbsi/tables/*.h ${D}${includedir}/am_adp/libdvbsi/tables
    install -d ${D}${includedir}/am_adp/libdvbsi/descriptors
    install -D -m 0644 ${S}/include/am_adp/libdvbsi/descriptors/*.h ${D}${includedir}/am_adp/libdvbsi/descriptors
    install -D -m 0644 ${S}/am_adp/libam_adp.so ${D}${libdir}
    install -D -m 0644 ${S}/am_adp/libam_adp.a ${D}${libdir}
    install -d ${D}${includedir}/am_mw
    install -D -m 0644 ${S}/include/am_mw/*.h -t ${D}${includedir}/am_mw
    install -d ${D}${includedir}/am_mw/atsc
    install -D -m 0644 ${S}/include/am_mw/atsc/*.h -t ${D}${includedir}/am_mw/atsc
    install -D -m 0644 ${S}/am_mw/libam_mw.so ${D}${libdir}
    install -D -m 0644 ${S}/am_mw/libam_mw.a ${D}${libdir}

    install -d ${D}${includedir}/ndk
    install -D -m 0644 ${S}/android/ndk/include/*.h -t ${D}${includedir}/ndk
    install -d ${D}${includedir}/ndk/linux/amports
    install -D -m 0644 ${S}/android/ndk/include/linux/amports/*.h -t ${D}${includedir}/ndk/linux/amports
    install -d ${D}${includedir}/ndk/linux/dvb
    install -D -m 0644 ${S}/android/ndk/include/linux/dvb/*.h -t ${D}${includedir}/ndk/linux/dvb
}

FILES:${PN} = "${libdir}/* ${bindir}/*"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"
