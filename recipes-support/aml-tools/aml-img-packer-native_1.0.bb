SUMMARY = "aml image packing utility"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

inherit native
include hosttools.inc

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SOC_FAMILY = "TBD"
SOC_FAMILY:sc2 = "sc2"
SOC_FAMILY:t7 = "t7"
SOC_FAMILY:s4 = "s4"
SOC_FAMILY:s5 = "s5"
SOC_FAMILY:s7 = "s7"
SOC_FAMILY:s7d = "s7d"
SOC_FAMILY:t5d = "t5d"
SOC_FAMILY:t5w = "t5w"
SOC_FAMILY:t3 = "t3"
SOC_FAMILY:t3x = "t3x"
SOC_FAMILY:t5m = "t5m"
SOC_FAMILY:s1a = "s1a"
SOC_FAMILY:g12a = "g12a"
SOC_FAMILY:g12b = "g12b"
SOC_FAMILY:sm1 = "sm1"

SOC_BOARD = "default"
SOC_BOARD:ab301 = "ab301"
SOC_BOARD:ah232 = "ah232"
SOC_BOARD:ah221 = "ah221"
SOC_BOARD:aq222 = "aq222"
SOC_BOARD:ap232 = "ap232"
SOC_BOARD:aq2432 = "aq2432"
SOC_BOARD:bf201 = "bf201"
SOC_BOARD:bg201 = "bg201"

PR = "r3"

S= "${WORKDIR}/git/aml-img-packer"

do_install () {
    install -d ${D}${bindir}/aml-img-packer/
    install -d ${D}${bindir}/aml-img-packer/${SOC_FAMILY}/logo_img_files
    install -m 0755 ${S}/aml_image_v2_packer_new ${D}${bindir}/aml-img-packer/
    install -m 0644 ${S}/aml_sdc_burn.ini ${D}${bindir}/aml-img-packer/
    install -m 0755 ${S}/img2simg ${D}${bindir}/aml-img-packer/
    install -m 0755 ${S}/ext2simg ${D}${bindir}/aml-img-packer/
    install -m 0755 ${S}/res_packer ${D}${bindir}/aml-img-packer/
    cd ${S}/${SOC_FAMILY}
    for file in $(find -maxdepth 1 -type f); do
            install -m 0644 -D ${file} ${D}${bindir}/aml-img-packer/${SOC_FAMILY}/${file}
        done
    cd ${S}/${SOC_FAMILY}/logo_img_files
    for file in $(find -maxdepth 1 -type f); do
            install -m 0644 -D ${file} ${D}${bindir}/aml-img-packer/${SOC_FAMILY}/logo_img_files/${file}
    done
    if [ -e ${S}/${SOC_FAMILY}/logo_img_files/${SOC_BOARD}/bootup.bmp ] ; then
        install -m 0644 -D ${S}/${SOC_FAMILY}/logo_img_files/${SOC_BOARD}/bootup.bmp ${D}${bindir}/aml-img-packer/${SOC_FAMILY}/logo_img_files/bootup.bmp
    fi
}
FILES:${PN} = "${bindir}/aml-img-packer/*"
