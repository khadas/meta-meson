SUMMARY = "aml swupdate utility"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

inherit native
include hosttools.inc

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SOC_FAMILY = "TBD"
SOC_FAMILY:s7 = "s7"
SOC_FAMILY:s4 = "s4"
SOC_FAMILY:t5d = "t5d"
SOC_FAMILY:sm1 = "sm1"
SOC_FAMILY:g12b = "g12b"
SOC_FAMILY:t5w = "t5w"
SOC_FAMILY:sc2 = "sc2"
SOC_FAMILY:s1a = "s1a"
SOC_FAMILY:t7 = "t7"
SOC_FAMILY:t3 = "t3"

PR = "r1"

S= "${WORKDIR}/git/aml-swupdate"

do_install () {
	install -d ${D}${bindir}/aml-swupdate/
	install -m 0644 ${S}/swupdate-priv.pem ${D}${bindir}/aml-swupdate/
	install -m 0755 ${S}/update.sh ${D}${bindir}/aml-swupdate/
	install -m 0755 ${S}/sw_package_create.sh ${D}${bindir}/aml-swupdate/
	install -m 0755 ${S}/encryption_key ${D}${bindir}/aml-swupdate/
	install -m 0755 ${S}/sw_enc_package_create.sh ${D}${bindir}/aml-swupdate/

	cd ${S}/${SOC_FAMILY}
	for file in $(find -maxdepth 1 -type f); do
		install -m 0644 -D ${file} ${D}${bindir}/aml-swupdate/${SOC_FAMILY}/${file}
	done
}
FILES:${PN} = "${bindir}/aml-swupdate/*"
