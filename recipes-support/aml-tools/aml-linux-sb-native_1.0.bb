SUMMARY = "aml linux secure boot sign"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

inherit native

DEPENDS:append = "wget-native unzip-native"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SOC_FAMILY = "TBD"
SOC_FAMILY:t5d = "t5d"

S = "${WORKDIR}/aml-linux-sb"

do_install () {
    install -d ${D}${bindir}/aml-linux-sb/keys
    if [ "${AML_SB_SIGN_CONFIG_PATH}" = "" ]; then
        cp -rf ${S}/${SOC_FAMILY}/* ${D}${bindir}/aml-linux-sb/keys/
    else
        cp -rf ${AML_SB_SIGN_CONFIG_PATH}/${SOC_FAMILY}/* ${D}${bindir}/aml-linux-sb/keys/
    fi

    if [ "${AML_SB_SIGN_TOOL}" = "" ]; then
        install -d ${D}${bindir}/aml-linux-sb/andr_tls_4_ota
        install -d ${D}${bindir}/aml-linux-sb/bin
        install -d ${D}${bindir}/aml-linux-sb/exe4Android
        install -d ${D}${bindir}/aml-linux-sb/stool

        wget -q -N ${AML_TOOLS_SITE}/Aml_Linux_SecureBootV3_SignTool.zip
        unzip -q -u ${WORKDIR}/aml-linux-sb-${PV}/Aml_Linux_SecureBootV3_SignTool.zip

        install -m 0755 ${WORKDIR}/aml-linux-sb-${PV}/amlogic_secureboot_sign_whole_pkg.bash ${D}${bindir}/aml-linux-sb/
        cp -rf ${WORKDIR}/aml-linux-sb-${PV}/andr_tls_4_ota/* ${D}${bindir}/aml-linux-sb/andr_tls_4_ota/
        cp -rf ${WORKDIR}/aml-linux-sb-${PV}/bin/* ${D}${bindir}/aml-linux-sb/bin/
        cp -rf ${WORKDIR}/aml-linux-sb-${PV}/exe4Android/* ${D}${bindir}/aml-linux-sb/exe4Android/
        cp -rf ${WORKDIR}/aml-linux-sb-${PV}/stool/* ${D}${bindir}/aml-linux-sb/stool/
    fi
}

FILES:${PN} = "${bindir}/aml-linux-sb/*"
INSANE_SKIP:${PN}:append = "already-stripped"
