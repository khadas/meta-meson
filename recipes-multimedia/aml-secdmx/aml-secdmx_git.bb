DESCRIPTION = "aml secdmx library"
LICENSE = "AMLOGIC"

#SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"

S = "${WORKDIR}/git"

do_compile[noexec] = "1"

DEPENDS += "optee-userspace liblog"

EXTRA_OEMAKE=" STAGING_DIR=${STAGING_DIR_TARGET} \
                 TARGET_DIR=${D} \
                 "
ARM_TARGET="arm.aapcs-linux.hard"
ARM_TARGET:aarch64 ="aarch64.lp64."
TA_TARGET="noarch"

do_install() {
    install -d ${D}${libdir}
    install -m 0755 -d ${D}${includedir}
    install ${S}/${ARM_TARGET}/$(PLATFORM_TDK_VERSION)/libdmx_client_linux.so ${D}${libdir}/libdmx_client.so
    install -m 0644 ${S}/include/* ${D}/${includedir}

    install -d ${D}/lib/optee_armtz
    install -m 0644 ${S}/ta/v3.8/dev/${CHIPSET_NAME}/b472711b-3ada-4c37-8c2a-7c64d8af0223.ta ${D}/lib/optee_armtz
}

INSANE_SKIP:${PN} = "dev-so ldflags dev-elf"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"

FILES:${PN} += " ${bindir}/* ${libdir}/*.so ${libdir}/teetz/*.ta /lib/optee_armtz/*.ta"
FILES:${PN}-dev = "${includedir}/* "

