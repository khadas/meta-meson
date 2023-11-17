# SPDX-License-Identifier: MIT

HOMEPAGE = "https://docs.lvgl.io/latest/en/html/porting/index.html"
SUMMARY = "LVGL's Display and Touch pad drivers"
DESCRIPTION = "Collection of drivers: SDL, framebuffer, wayland and more..."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6fc0df890c5270ef045981b516bb8f2"

# TODO: Pin upstream release (current v7.11.0-80-g419a757)
SRC_URI = "git://github.com/lvgl/lv_drivers;destsuffix=${S};protocol=https;nobranch=1 \
           file://0002-amlogic-lv-drivers.patch \
"
SRCREV = "419a757c23aaa67c676fe3a2196d64808fcf2254"

DEPENDS = "libxkbcommon lvgl"

inherit cmake

S = "${WORKDIR}/${PN}-${PV}"

EXTRA_OECMAKE += "-Dinstall:BOOL=ON -DLIB_INSTALL_DIR=${BASELIB}"

TARGET_CFLAGS += "-DLV_CONF_INCLUDE_SIMPLE=1"
TARGET_CFLAGS += "-I${RECIPE_SYSROOT}/${includedir}/lvgl"

# "lv_drv_conf_template.h" as a template configure file, enable it by setting #if 0 to
# #if 1, and you must rename it to "lv_drv_conf.h".
do_configure:append() {
    [ -r "${S}/lv_drv_conf.h" ] \
        || sed -e "s|#if 0 .*Set it to \"1\" to enable the content.*|#if 1 // Enabled by ${PN}|g" \
	       -e "s|\(^ *# *define *WAYLAND_HOR_RES *\).*|\1${LVGL_CONFIG_WAYLAND_HOR_RES}|g" \
 	       -e "s|\(^ *# *define *WAYLAND_VER_RES *\).*|\1${LVGL_CONFIG_WAYLAND_VER_RES}|g" \
          < "${S}/lv_drv_conf_template.h" > "${S}/lv_drv_conf.h"
}

FILES:${PN}-dev += "\
    ${includedir}/lvgl/lv_drivers/ \
    "
