FILESEXTRAPATHS:prepend := "${THISDIR}/refpolicy_aml:"
PATCH_LIST := "${@get_patch_list('${THISDIR}/refpolicy_aml')}"

require refpolicy-support-monolithic.inc

SRC_URI += " ${PATCH_LIST} "
