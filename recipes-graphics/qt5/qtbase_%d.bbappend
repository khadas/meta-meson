SUMMARY = "Qt, a Launcher framwork"
DESCRIPTION = "Qt is the reference implementation of a Launcher framwork"
HOMEPAGE = "https://doc.qt.io/"

PACKAGECONFIG_RELEASE ?= "release"
# This is in qt5.inc, because qtwebkit-examples are using it to enable ca-certificates dependency
PACKAGECONFIG_OPENSSL ?= "openssl"
PACKAGECONFIG_DEFAULT ?= "accessibility dbus udev evdev widgets libs freetype pcre \
    ${@bb.utils.contains('SELECTED_OPTIMIZATION', '-Os', 'optimize-size ltcg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'qt5-static', 'static', '', d)} \
"

PACKAGECONFIG = " \
    ${PACKAGECONFIG_RELEASE} \
    ${PACKAGECONFIG_DEFAULT} \
    ${PACKAGECONFIG_OPENSSL} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'qt5', \
        'xkb xkbcommon \
        eglfs \
        gles2 \
        icu \
        fontconfig \
        gif \
        jpeg \
        libpng \
        ', '', d)} \
    "

PACKAGECONFIG += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'qt5-tests', \
        'examples \
        tools \
        tests \
        ', '', d)} \
    "