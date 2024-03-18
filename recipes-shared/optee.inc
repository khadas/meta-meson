LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

TBRANCH:t5d = "tdk-v2.4.4"
TBRANCH = "tdk-v3.8.0"
#SRC_URI = "git://${AML_GIT_ROOT}/vendor/amlogic/tdk.git;protocol=${AML_GIT_PROTOCOL};branch=${TBRANCH};destsuffix=tdk;name=tdk"

PATCHTOOL= "git"
#For common patches
TDIR:t5d = "tdk/v2.4.4"
TDIR = "tdk/v3.8.0"
TDIR:s7 = "tdk/v3.18.0"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/vendor/amlogic/${TDIR}', '${TDIR}')}"

#SRCREV ?= "${AUTOREV}"
SRCREV_FORMAT = "tdk"
#INC_PR = "${TDK_VERSION}"
PV = "git${SRCPV}"
S = "${WORKDIR}/tdk"
