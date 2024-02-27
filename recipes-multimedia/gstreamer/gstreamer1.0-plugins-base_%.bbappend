FILESEXTRAPATHS:prepend := "${THISDIR}/gst1-plugins-base:"


DEPENDS += " libopus"
RDEPENDS:${PN} += " libopus"

PACKAGECONFIG:append = " opus"
PACKAGECONFIG[opus]  = "-Dopus=enabled,-Dopus=disabled,libopus"
