SUMMARY = "aml swupdate utility"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

inherit native
include hosttools.inc

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SOC_FAMILY = "s4"
SOC_FAMILY_t5d = "t5d"

PR = "r1"

S= "${WORKDIR}/git"

do_install () {
	install -d ${D}${bindir}/aml-swupdate/
	install -m 0644 ${S}/aml-swupdate/swupdate-priv.pem ${D}${bindir}/aml-swupdate/
	install -m 0755 ${S}/aml-swupdate/update.sh ${D}${bindir}/aml-swupdate/
	install -m 0755 ${S}/aml-swupdate/sw_package_create.sh ${D}${bindir}/aml-swupdate/
	cd ${S}/aml-swupdate/${SOC_FAMILY}
	for file in $(find -maxdepth 1 -type f); do
		install -m 0644 -D ${file} ${D}${bindir}/aml-swupdate/${SOC_FAMILY}/${file}
	done
}
FILES_${PN} = "${bindir}/aml-swupdate/*"
