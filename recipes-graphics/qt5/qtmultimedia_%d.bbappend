DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'gstreamer1', 'gstreamer1.0 gstreamer1.0-plugins-base', '', d)}"

PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'qt5-tests', 'examples tests', '', d)}"

EXTRA_QMAKEVARS_CONFIGURE:remove = "${@bb.utils.contains('DISTRO_FEATURES', 'gstreamer1', '-no-gstreamer', '', d)}"
