SUMMARY = "Wifi firmware"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/amlogic/wifi.git;protocol=${AML_GIT_PROTOCOL};branch=n-amlogic"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/hardware/aml-4.9/amlogic/wifi')}"

#SRCREV ?= "${AUTOREV}"
PV = "git${SRCPV}"

inherit pkgconfig

do_configure[noexec] = "1"
do_compile[noexec] = "1"

PACKAGES =+ "${PN}-ap6181 \
             ${PN}-ap6210 \
             ${PN}-ap6476 \
             ${PN}-ap6493 \
             ${PN}-ap6398 \
             ${PN}-ap6330 \
             ${PN}-bcm40181 \
             ${PN}-bcm40183 \
             ${PN}-ap62x2 \
             ${PN}-ap6335 \
             ${PN}-ap6234 \
             ${PN}-ap6441 \
             ${PN}-ap6212 \
             ${PN}-ap6256 \
             ${PN}-bcm4354 \
             ${PN}-bcm4356 \
             ${PN}-bcm43458 \
             ${PN}-qca6174\
			 ${PN}-ap6275 \
            "

do_install() {
	mkdir -p ${D}${sysconfdir}/bluetooth/
#ap6181
	mkdir -p ${D}${sysconfdir}/wifi/6181/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6181/Wi-Fi/*.bin ${D}${sysconfdir}/wifi/6181/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6181/Wi-Fi/nvram_ap6181.txt ${D}${sysconfdir}/wifi/6181/
#ap6210
	mkdir -p ${D}${sysconfdir}/wifi/6210/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6210/Wi-Fi/*.bin ${D}${sysconfdir}/wifi/6210/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6210/Wi-Fi/nvram_ap6210.txt ${D}${sysconfdir}/wifi/6210/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6210/BT/bcm20710a1.hcd ${D}${sysconfdir}/bluetooth/
#ap6476
	mkdir -p ${D}${sysconfdir}/wifi/6476/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6476/Wi-Fi/*.bin ${D}${sysconfdir}/wifi/6476/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6476/Wi-Fi/nvram_ap6476.txt ${D}${sysconfdir}/wifi/6476/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6476/GPS/bcm2076b1.hcd ${D}${sysconfdir}/bluetooth/
#ap6493
	mkdir -p ${D}${sysconfdir}/wifi/6493/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6493/Wi-Fi/*.bin ${D}${sysconfdir}/wifi/6493/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6493/Wi-Fi/nvram_ap6493.txt ${D}${sysconfdir}/wifi/6493/
#ap6398
	mkdir -p ${D}${sysconfdir}/wifi/6398/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6398/Wi-Fi/*.bin ${D}${sysconfdir}/wifi/6398/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6398/Wi-Fi/nvram_ap6398s.txt ${D}${sysconfdir}/wifi/6398/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6398/BT/BCM4359C0SR2.hcd ${D}/etc/bluetooth/
#ap6330
	mkdir -p ${D}${sysconfdir}/wifi/6330/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6330/Wi-Fi/*.bin ${D}${sysconfdir}/wifi/6330/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6330/Wi-Fi/nvram_ap6330.txt ${D}${sysconfdir}/wifi/6330/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6330/BT/bcm40183b2.hcd ${D}${sysconfdir}/bluetooth/
#ap6256
	mkdir -p ${D}${sysconfdir}/wifi/6256/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6256/Wi-Fi/*.bin ${D}${sysconfdir}/wifi/6256/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6256/Wi-Fi/nvram_ap6256.txt ${D}${sysconfdir}/wifi/6256/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6256/BT/BCM4345C5.hcd ${D}/etc/bluetooth/
#bcm40181
	mkdir -p ${D}${sysconfdir}/wifi/40181/
	install -D -m 0644 ${S}/bcm_ampak/config/40181/*.bin ${D}${sysconfdir}/wifi/40181/
	install -D -m 0644 ${S}/bcm_ampak/config/40181/nvram.txt ${D}${sysconfdir}/wifi/40181/nvram.txt
#bcm40183
	mkdir -p ${D}${sysconfdir}/wifi/40183/
	install -D -m 0644 ${S}/bcm_ampak/config/40183/*.bin ${D}${sysconfdir}/wifi/40183/
	install -D -m 0644 ${S}/bcm_ampak/config/40183/nvram.txt ${D}${sysconfdir}/wifi/40183/nvram.txt
#ap62x2
	mkdir -p ${D}${sysconfdir}/wifi/62x2/
	install -D -m 0644 ${S}/bcm_ampak/config/62x2/*.bin ${D}${sysconfdir}/wifi/62x2/
	install -D -m 0644 ${S}/bcm_ampak/config/62x2/nvram.txt ${D}${sysconfdir}/wifi/62x2/nvram.txt
	install -D -m 0644 ${S}/bcm_ampak/config/62x2/BT/bcm43241b4.hcd ${D}${sysconfdir}/bluetooth/
#ap6335
	mkdir -p ${D}${sysconfdir}/wifi/6335/
	install -D -m 0644 ${S}/bcm_ampak/config/6335/*.bin ${D}${sysconfdir}/wifi/6335/
	install -D -m 0644 ${S}/bcm_ampak/config/6335/nvram.txt ${D}${sysconfdir}/wifi/6335/nvram.txt
	install -D -m 0644 ${S}/bcm_ampak/config/6335/BT/bcm4335c0.hcd ${D}${sysconfdir}/bluetooth/
#ap6234
	mkdir -p ${D}${sysconfdir}/wifi/6234/
	install -D -m 0644 ${S}/bcm_ampak/config/6234/*.bin ${D}${sysconfdir}/wifi/6234/
	install -D -m 0644 ${S}/bcm_ampak/config/6234/nvram.txt ${D}${sysconfdir}/wifi/6234/nvram.txt
	install -D -m 0644 ${S}/bcm_ampak/config/6234/BT/bcm43341b0.hcd ${D}${sysconfdir}/bluetooth/
#ap6441
	mkdir -p ${D}${sysconfdir}/wifi/6441/
	install -D -m 0644 ${S}/bcm_ampak/config/6441/*.bin ${D}${sysconfdir}/wifi/6441/
	install -D -m 0644 ${S}/bcm_ampak/config/6441/nvram.txt ${D}${sysconfdir}/wifi/6441/nvram.txt
	install -D -m 0644 ${S}/bcm_ampak/config/6441/BT/bcm43341b0.hcd ${D}${sysconfdir}/bluetooth/
#ap6212
	mkdir -p ${D}${sysconfdir}/wifi/6212/
	install -D -m 0644 ${S}/bcm_ampak/config/6212/*.bin ${D}${sysconfdir}/wifi/6212/
	install -D -m 0644 ${S}/bcm_ampak/config/6212/nvram.txt ${D}${sysconfdir}/wifi/6212/nvram.txt
	install -D -m 0644 ${S}/bcm_ampak/config/6212/BT/bcm43438a0.hcd ${D}${sysconfdir}/bluetooth/
#bcm4354
	mkdir -p ${D}${sysconfdir}/wifi/4354/
	install -D -m 0644 ${S}/bcm_ampak/config/4354/*.bin ${D}${sysconfdir}/wifi/4354/
	install -D -m 0644 ${S}/bcm_ampak/config/4354/nvram*.txt ${D}${sysconfdir}/wifi/4354/nvram.txt
	install -D -m 0644 ${S}/bcm_ampak/config/4354/bcm4354a1.hcd ${D}${sysconfdir}/bluetooth/
#bcm4356
	mkdir -p ${D}${sysconfdir}/wifi/4356/
	install -D -m 0644 ${S}/bcm_ampak/config/4356/*.bin ${D}${sysconfdir}/wifi/4356/
	install -D -m 0644 ${S}/bcm_ampak/config/4356/nvram*.txt ${D}${sysconfdir}/wifi/4356/nvram.txt
	install -D -m 0644 ${S}/bcm_ampak/config/4356/bcm4356a2.hcd ${D}${sysconfdir}/bluetooth/
#bcm43458
	mkdir -p ${D}${sysconfdir}/wifi/43458/
	install -D -m 0644 ${S}/bcm_ampak/config/43458/*.bin ${D}${sysconfdir}/wifi/43458/
	install -D -m 0644 ${S}/bcm_ampak/config/43458/nvram*.txt ${D}${sysconfdir}/wifi/43458/nvram.txt
	install -D -m 0644 ${S}/bcm_ampak/config/43458/BCM4345C0.hcd ${D}${sysconfdir}/bluetooth/

#qca6174
	mkdir -p ${D}${sysconfdir}/wifi/qca6174/wlan/
	mkdir -p ${D}${sysconfdir}/bluetooth/qca6174/
	mkdir -p ${D}${base_libdir}/firmware/qca_bt/
	install -D -m 0644 ${S}/qcom/config/qca6174/wifi/*.bin ${D}${sysconfdir}/wifi/qca6174/
	install -D -m 0644 ${S}/qcom/config/qca6174/wifi/wlan/* ${D}${sysconfdir}/wifi/qca6174/wlan/
	install -D -m 0644 ${S}/qcom/config/qca6174/bt/* ${D}${sysconfdir}/bluetooth/qca6174/
	install -D -m 0644 ${S}/qcom/config/qca6174/bt/* ${D}${base_libdir}/firmware/qca_bt/

#ap6275
	mkdir -p ${D}${sysconfdir}/wifi/6275/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6275/Wi-Fi/*.bin ${D}${sysconfdir}/wifi/6275/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6275/Wi-Fi/nvram_ap6275s.txt ${D}${sysconfdir}/wifi/6275/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6275/Wi-Fi/clm_bcm43752a2_ag.blob ${D}${sysconfdir}/wifi/6275/
	install -D -m 0644 ${S}/bcm_ampak/config/AP6275/BT/BCM4362A2.hcd ${D}/etc/bluetooth/
}

FILES:${PN}-ap6275 = "\
                ${sysconfdir}/wifi/6275/*\
				${sysconfdir}/bluetooth/BCM4362A2.hcd"

FILES:${PN}-ap6181 = "\
                ${sysconfdir}/wifi/6181/*"\

FILES:${PN}-ap6210 = "\
                ${sysconfdir}/wifi/6210/* \
                ${sysconfdir}/bluetooth/bcm20710a1.hcd"

FILES:${PN}-ap6476 = "\
                ${sysconfdir}/wifi/6476/* \
                ${sysconfdir}/bluetooth/bcm2076b1.hcd"

FILES:${PN}-ap6493 = "\
                ${sysconfdir}/wifi/6493/*"

FILES:${PN}-ap6256 = "\
                ${sysconfdir}/wifi/6256/* \
                ${sysconfdir}/bluetooth/BCM4345C5.hcd"

FILES:${PN}-ap6398 = "\
                ${sysconfdir}/wifi/6398/* \
                ${sysconfdir}/bluetooth/BCM4359C0SR2.hcd"

FILES:${PN}-ap6330 = "\
                ${sysconfdir}/wifi/6330/* \
                ${sysconfdir}/bluetooth/bcm40183b2.hcd"

FILES:${PN}-bcm40181 = "\
                ${sysconfdir}/wifi/40181/*"

FILES:${PN}-bcm40183 = "\
                ${sysconfdir}/wifi/40183/*"

FILES:${PN}-ap62x2 = "\
                ${sysconfdir}/wifi/62x2/* \
                ${sysconfdir}/bluetooth/bcm43241b4.hcd"

FILES:${PN}-ap6335 = "\
                ${sysconfdir}/wifi/6335/* \
                ${sysconfdir}/bluetooth/bcm4335c0.hcd"

FILES:${PN}-ap6234 = "\
                ${sysconfdir}/wifi/6234/* \
                ${sysconfdir}/bluetooth/bcm43341b0.hcd"

FILES:${PN}-ap6441 = "\
                ${sysconfdir}/wifi/6441/* \
                ${sysconfdir}/bluetooth/bcm43341b0.hcd"

FILES:${PN}-ap6212 = "\
                ${sysconfdir}/wifi/6212/*\
                ${sysconfdir}/bluetooth/bcm43438a0.hcd"

FILES:${PN}-bcm4354 = "\
               ${sysconfdir}/wifi/4354/* \
               ${sysconfdir}/bluetooth/bcm4354a1.hcd"

FILES:${PN}-bcm4356 = " \
                ${sysconfdir}/wifi/4356/* \
                ${sysconfdir}/bluetooth/bcm4356a2.hcd"

FILES:${PN}-bcm43458 = " \
                ${sysconfdir}/bluetooth/BCM4345C0.hcd \
                ${sysconfdir}/wifi/43458/*"

FILES:${PN}-qca6174= " \
                ${sysconfdir}/bluetooth/qca6174/* \
                ${base_libdir}/firmware/qca_bt/* \
                ${sysconfdir}/wifi/qca6174/*"
# Header file provided by a separate package
DEPENDS += ""

S = "${WORKDIR}/git"
