SUMMARY = "Meson init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

#SRC_URI = "git://${AML_GIT_ROOT}${AML_GIT_ROOT_YOCTO_SUFFIX}/rdk/prebuilt/vendor;protocol=${AML_GIT_PROTOCOL};branch=master"
SRCREV ?= "${AUTOREV}"
PV = "${SRCPV}"
PR = "r0"

SOC = "Amlogic"
SOC_u212 = "s905x2"
SOC_ab301 = "t962x3"
SOC_ab311 = "t962e2"
SOC_am301 = "t950d4"
SOC_at301 = "t962d4"
SOC_ar321 = "t965d4"
SOC_ap222 = "s905y4"
SOC_ap223 = "s905y4"
SOC_ah212 = "s905x4"
SOC_aq222 = "s805x2"
SOC_pxp = "p1_pxp"

S = "${WORKDIR}/git/"

do_install() {
	if [ -d ${S}/etc/tvconfig/${SOC} ]; then
		if [ -e ${S}/etc/tvconfig/${SOC}/audio_effects.xml ]; then
			install -m 0644 -D ${S}/etc/tvconfig/${SOC}/audio_effects.xml ${D}/etc/audio_effects.xml
		fi
		if [ -e ${S}/etc/tvconfig/${SOC}/ms12_tuning.dat ]; then
			install -m 0644 -D ${S}/etc/tvconfig/${SOC}/ms12_tuning.dat ${D}/etc/ms12_tuning.dat
		fi
		install -d ${D}/etc/tvconfig/pq
		if [ -e ${S}/etc/tvconfig/${SOC}/PQ ]; then
			install -m 0644 -D ${S}/etc/tvconfig/${SOC}/PQ/pq.db ${D}/etc/tvconfig/pq/pq.db
			install -m 0644 -D ${S}/etc/tvconfig/${SOC}/PQ/pq_default.ini ${D}/etc/tvconfig/pq/pq_default.ini
			sed -i 's@/vendor/etc/@/data/persistent/@' ${D}/etc/tvconfig/pq/pq_default.ini
			if [ -e ${S}/etc/tvconfig/${SOC}/PQ/overscan.db ]; then
				install -m 0644 -D ${S}/etc/tvconfig/${SOC}/PQ/overscan.db ${D}/etc/tvconfig/pq/overscan.db
			fi
		fi
		if [ -e ${S}/etc/tvconfig/${SOC}/tvconfig ]; then
			cd ${S}/etc/tvconfig/${SOC}/tvconfig
			for file in $(find -type f); do
				install -m 0644 -D ${file} ${D}/etc/tvconfig/${file}
			done
		fi
	else
		# fix build fail for vendor folder empty
		install -d ${D}/etc/tvconfig/
		touch ${D}/etc/tvconfig/dummy

		if [ -d ${S}/${SOC} ]; then
			install -d ${D}/p1_pxp
			cd ${S}/${SOC}
			for file in $(find -type f); do
				install -m 0644 -D ${file} ${D}/p1_pxp/${file}
			done
		fi
	fi
}

FILES_${PN} = " /etc/*"
FILES_${PN}_pxp = " /p1_pxp/*"
FILES_${PN}-dev = " "
PACKAGE_ARCH = "${MACHINE_ARCH}"
INSANE_SKIP_${PN} = "dev-so dev-elf already-stripped"
