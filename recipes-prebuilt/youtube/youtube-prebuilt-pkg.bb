inherit bin_package

FILESEXTRAPATHS:prepend := "${THISDIR}/ipk:"

LICENSE = "CLOSED"

SRC_URI = "${@' '.join(['file://' + f + ';subdir=ipk' \
              for f in os.listdir(os.path.join(d.getVar('THISDIR'),'ipk')) \
              if f.endswith('.ipk')])}"

S = "${WORKDIR}/ipk"

RDEPENDS:${PN} = "aml-mediadrm-widevine aml-platformserver aml-secmem aml-youtubesign-bin \
    gst-aml-drm-plugins gstreamer1.0 libgstapp-1.0 libgstaudio-1.0 libgstpbutils-1.0 libgstvideo-1.0 \
    glib-2.0 \
    westeros wpeframework wpeframework-interfaces"

FILES:${PN}-dev = " ${includedir}/"
FILES:${PN} = "/"

INSANE_SKIP:${PN}:append = " file-rdeps dev-so libdir"

do_unpack[depends] += " xz-native:do_populate_sysroot"
