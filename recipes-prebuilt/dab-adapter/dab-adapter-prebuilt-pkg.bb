inherit bin_package

FILESEXTRAPATHS:prepend := "${THISDIR}/ipk:"

LICENSE = "CLOSED"

SRC_URI = "${@' '.join(['file://' + f + ';subdir=ipk' \
              for f in os.listdir(os.path.join(d.getVar('THISDIR'),'ipk')) \
              if f.endswith('.ipk')])}"

S = "${WORKDIR}/ipk"

RDEPENDS:${PN} = "mosquitto \
    "

FILES:${PN}-dev = " ${includedir}/"
FILES:${PN} = "/"

INSANE_SKIP:${PN}:append = " file-rdeps dev-so libdir"

do_unpack[depends] += " xz-native:do_populate_sysroot"
