inherit bin_package

FILESEXTRAPATHS:prepend := "${THISDIR}/ipk:"

LICENSE = "CLOSED"

SRC_URI = "${@' '.join(['file://' + f + ';subdir=ipk' \
              for f in os.listdir(os.path.join(d.getVar('THISDIR'),'ipk')) \
              if f.endswith('.ipk')])}"

S = "${WORKDIR}/ipk"

inherit update-rc.d

INITSCRIPT_NAME = "arka"
INITSCRIPT_PARAMS = "start 80 2 3 4 5 . stop 80 0 6 1 ."

RDEPENDS:${PN} = "dtvkit-release-prebuilt jsoncpp \
                aml-audio-service meson-display udev aml-hdmicec aml-mp-sdk \
                "
RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'disable-binderfs', '', 'libbinder', d)}"
RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'use-egl', 'freetype', 'directfb', d)}"

FILES:${PN}-dev = " ${includedir}/"
FILES:${PN} = "/"

INSANE_SKIP:${PN}:append = " file-rdeps dev-so libdir"

do_unpack[depends] += " xz-native:do_populate_sysroot"
