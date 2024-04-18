HOMEPAGE = "https://lvgl.io/"
DESCRIPTION = "LVGL is an OSS graphics library to create embedded GUI"
SUMMARY = "Light and Versatile Graphics Library"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=bf1198c89ae87f043108cea62460b03a"

SRC_URI = "gitsm://github.com/lvgl/lvgl;destsuffix=${S};protocol=https;nobranch=1 \
            file://lv_conf.h \
"

SRCREV = "9491c3ff6d2f8e56b13d8fb493d4b3ee98ef1a4b"

inherit cmake
inherit features_check pkgconfig

PV = "8.3.0"
EXTRA_OECMAKE = "-DLIB_INSTALL_DIR=${BASELIB} -DLV_CONF_PATH=${WORKDIR}/lv_conf.h"

S = "${WORKDIR}/${PN}-${PV}"

do_install:append(){
    install -m 0644 ${WORKDIR}/lv_conf.h ${D}/usr/include/
}


FILES:${PN}-dev += "\
    ${includedir}/${PN}/ \
    ${includedir}/${PN}/lvgl/ \
    "

