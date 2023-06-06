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

RDEPENDS:${PN} = "directfb dtvkit-release-prebuilt jsoncpp libbinder \
                aml-audio-service meson-display udev aml-hdmicec aml-mp-sdk \
                dtvkit-release-prebuilt aml-audio-service \
                "

FILES:${PN}-dev = " ${includedir}/"
FILES:${PN} = "/"

INSANE_SKIP:${PN}:append = " file-rdeps dev-so libdir"

do_unpack[depends] += " xz-native:do_populate_sysroot"