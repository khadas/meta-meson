PV = "2.20200229+git${SRCPV}"

SRC_URI = "git://github.com/SELinuxProject/refpolicy.git;protocol=https;branch=master;name=refpolicy;destsuffix=refpolicy"

SRCREV_refpolicy ?= "613708cad64943bae4e2de00df7b8e656446dd2f"

UPSTREAM_CHECK_GITTAGREGEX = "RELEASE_(?P<pver>\d+_\d+)"

FILESEXTRAPATHS:prepend := "${THISDIR}/refpolicy:"

include refpolicy_common.inc
