inherit bin_package

FILESEXTRAPATHS:prepend := "${THISDIR}/ipk:"

LICENSE = "CLOSED"

SRC_URI = "${@' '.join(['file://' + f + ';subdir=ipk' \
              for f in os.listdir(os.path.join(d.getVar('THISDIR'),'ipk')) \
              if f.endswith('.ipk')])}"

S = "${WORKDIR}/ipk"

RDEPENDS:${PN} = "aml-audio-service c-ares expat fdk-aac freetype glib-2.0 \
    gst-aml-drm-plugins gstreamer1.0 lcms libamldeviceproperty libcrypto libcurl libelf \
    libgstapp-1.0 libgstaudio-1.0 libgstvideo-1.0 libicui18n libicuuc libjpeg-turbo \
    libnghttp2 libnl libnl-genl libpng libssl libstdc++ libunwind libwebp \
    optee-userspace openjpeg playready tremor westeros \
    wpeframework wpeframework-interfaces"


FILES:${PN}-dev = "${includedir}/"
#Need include everything
FILES:${PN} = "/"

INSANE_SKIP:${PN} += "file-rdeps dev-so"

do_unpack[depends] += "xz-native:do_populate_sysroot"
