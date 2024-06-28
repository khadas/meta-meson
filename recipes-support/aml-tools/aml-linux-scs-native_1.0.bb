SUMMARY = "aml linux scs sign"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

inherit native

DEPENDS:append = "unzip-native"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SOC_FAMILY = "TBD"
SOC_FAMILY:sc2= "sc2"
SOC_FAMILY:t7 = "t7"
SOC_FAMILY:s4 = "s4"

S = "${WORKDIR}/aml-linux-scs"

do_install () {
    local chipset_name=$(echo ${CHIPSET_NAME} | tr 'A-Z' 'a-z')
    install -d ${D}${bindir}/aml-linux-scs/
    if [ "${AML_SCS_SIGN_CONFIG_PATH}" = "" ];then
        cp -rf ${S}/${SOC_FAMILY}/${CHIPSET_NAME}/* ${D}${bindir}/aml-linux-scs/
        mkdir -p ${DEPLOY_DIR_IMAGE}
        cp -rf ${S}/${SOC_FAMILY}/${CHIPSET_NAME}/device-keys/fip/rsa/${chipset_name}/rootrsa-0/key/bl33-level-3-rsa-priv.pem ${DEPLOY_DIR_IMAGE}
    else
        cp -rf ${AML_SCS_SIGN_CONFIG_PATH}/${SOC_FAMILY}/${CHIPSET_NAME}/* ${D}${bindir}/aml-linux-scs/
    fi

    if [ "${AML_SCS_SIGN_TOOL}" = "" ];then
        local chipset_name=$(echo ${CHIPSET_NAME} | tr 'A-Z' 'a-z')
        install -d ${D}${bindir}/aml-linux-scs/bin
        install -d ${D}${bindir}/aml-linux-scs/exe4Android
        install -d ${D}${bindir}/aml-linux-scs/stool/${SOC_FAMILY}
        install -d ${D}${bindir}/aml-linux-scs/templates/${SOC_FAMILY}/${chipset_name}

        wget -q -N ${AML_TOOLS_SITE}/Aml_Linux_SCS_SignTool.zip
        unzip -q -u ${WORKDIR}/aml-linux-scs-${PV}/Aml_Linux_SCS_SignTool.zip

        sed -i 's|#!/usr/bin/env python2|#!/usr/bin/env python3|' ${WORKDIR}/aml-linux-scs-${PV}/bin/recovery-sign.py

        install -m 0755 ${WORKDIR}/aml-linux-scs-${PV}/amlogic_scs_sign_whole_pkg.bash ${D}${bindir}/aml-linux-scs/
        cp -rf ${WORKDIR}/aml-linux-scs-${PV}/bin/* ${D}${bindir}/aml-linux-scs/bin/
        cp -rf ${WORKDIR}/aml-linux-scs-${PV}/exe4Android/* ${D}${bindir}/aml-linux-scs/exe4Android/
        cp -rf ${WORKDIR}/aml-linux-scs-${PV}/stool/${SOC_FAMILY}/* ${D}${bindir}/aml-linux-scs/stool/${SOC_FAMILY}/
        cp -rf ${WORKDIR}/aml-linux-scs-${PV}/templates/${SOC_FAMILY}/${chipset_name}/* ${D}${bindir}/aml-linux-scs/templates/${SOC_FAMILY}/${chipset_name}/
    fi
}

FILES:${PN} = "${bindir}/aml-linux-scs/*"
INSANE_SKIP:${PN}:append = "already-stripped"
