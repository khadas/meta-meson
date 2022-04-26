FILESEXTRAPATHS_prepend := "${THISDIR}/gst1-plugins-base:"
SRC_URI_append = "file://0001-add-hdr-meta-parse.patch "
SRC_URI_append = "file://0002-appsrc-clear-position-when-flush-stop-event-reset_ti.patch "
SRC_URI_append = "file://0003-add-new-ftye-wmf-for-qt_type_find.patch "
DEPENDS += " libopus"
RDEPENDS_${PN} += " libopus"

PACKAGECONFIG_append = " opus"
PACKAGECONFIG[opus]  = "-Dopus=enabled,-Dopus=disabled,libopus"
