inherit bin_package

FILESEXTRAPATHS:prepend := "${THISDIR}/ipk:"

LICENSE = "CLOSED"

SRC_URI = "${@' '.join(['file://' + f + ';subdir=ipk' \
              for f in os.listdir(os.path.join(d.getVar('THISDIR'),'ipk')) \
              if f.endswith('.ipk')])}"

S = "${WORKDIR}/ipk"

RDEPENDS:${PN} = "libstdc++ libjpeg-turbo libpng glib-2.0 \
    aml-platformserver \
    gst-aml-drm-plugins gstreamer1.0 libgstapp-1.0 \
    libcrypto playready \
    westeros wpeframework wpeframework-interfaces \
    "


FILES:${PN}-dev = "${includedir}/"
FILES:${PN} = "/"

INSANE_SKIP:${PN}:append = " file-rdeps dev-so files-invalid"

do_unpack[depends] += "xz-native:do_populate_sysroot"
