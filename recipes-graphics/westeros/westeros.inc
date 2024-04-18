LICENSE = "Apache-2.0"

#PV = "1.0+gitr${SRCPV}"

# SRC_URI = "${WESTEROS_URI}"
#SRCREV = "${WESTEROS_SRCREV}"

WESTEROS_URI = "git://github.com/rdkcmf/westeros;name=westeros"
# Tip of westeros master as of June 21, 2021
WESTEROS_SRCREV ?= "55133756057db2592fa2b4f2a7dea01db846def4"

LICENSE_LOCATION ?= "${S}/LICENSE"
LIC_FILES_CHKSUM = "file://${LICENSE_LOCATION};md5=8fb65319802b0c15fc9e0835350ffa02"

SRCREV_FORMAT = "westeros"
