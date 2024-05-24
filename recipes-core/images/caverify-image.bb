SUMMARY = "generate caverify.img"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

DEPENDS += " recovery-image"
do_install[depends] += "recovery-image:do_image_complete"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"

S="${WORKDIR}/git"

do_install() {
    mkdir -p ${DEPLOY_DIR_IMAGE}
    cp ${S}/irdeto_loader_preset_tool ${DEPLOY_DIR_IMAGE}/
    cd ${DEPLOY_DIR_IMAGE}
    ./irdeto_loader_preset_tool
    rm irdeto_loader_preset_tool
    cd -
}

