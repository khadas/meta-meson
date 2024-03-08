SUMMARY = "generate gpt.bin"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM=""

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "${@bb.utils.contains("DISTRO_FEATURES", "absystem", "file://partition_table_ab.txt", "file://partition_table_k64.txt", d)}"
SRC_URI:kernel32 = "${@bb.utils.contains("DISTRO_FEATURES", "absystem", "file://partition_table_ab.txt", "file://partition_table_k32.txt", d)}"

PARTITION_TABLE = "partition_table.txt"

S="${WORKDIR}/git"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"

do_install() {
    mv  ${WORKDIR}/partition_table_*.txt ${WORKDIR}/${PARTITION_TABLE}
    ${S}/makegpt -o gpt_out.bin -s 16G -v 2 --partitions ${WORKDIR}/${PARTITION_TABLE}
    mkdir -p ${DEPLOY_DIR_IMAGE}
    install -m 0644 gpt_out.bin   ${DEPLOY_DIR_IMAGE}/gpt.img
}

