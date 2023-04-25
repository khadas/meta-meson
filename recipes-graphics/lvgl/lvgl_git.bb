HOMEPAGE = "https://lvgl.io/"
DESCRIPTION = "LVGL is an OSS graphics library to create embedded GUI"
SUMMARY = "Light and Versatile Graphics Library"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=bf1198c89ae87f043108cea62460b03a"

SRC_URI = "gitsm://github.com/lvgl/lvgl;destsuffix=${S};protocol=https;nobranch=1 \
            file://0001-amlogic-lvgl-porting.patch \
            file://0001-lvgl-color-depth-modify.patch \
"
SRCREV = "d38eb1e689fa5a64c25e677275172d9c8a4ab2f0"

inherit cmake
inherit features_check pkgconfig

PV = "8.1.0"
EXTRA_OECMAKE = "-DLIB_INSTALL_DIR=${BASELIB}"
S = "${WORKDIR}/${PN}-${PV}"

LVGL_CONFIG_LV_MEM_CUSTOM ?= "0"

# "lv_drv_conf_template.h" as a template configure file, enable it by setting #if 0 to
# #if 1, and you must rename it to "lv_drv_conf.h".
do_configure:prepend() {
    [ -r "${S}/lv_conf.h" ] \
        || sed -e 's|#if 0 .*Set it to "1" to enable .*|#if 1 // Enabled|g' \
	    -e "s|\(#define LV_MEM_CUSTOM .*\)0|\1${LVGL_CONFIG_LV_MEM_CUSTOM}|g" \
            < "${S}/lv_conf_template.h" > "${S}/lv_conf.h"
}

FILES_${PN}-dev += "\
    ${includedir}/${PN}/ \
    ${includedir}/${PN}/lvgl/ \
    "
