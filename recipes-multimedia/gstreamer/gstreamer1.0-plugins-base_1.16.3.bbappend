FILESEXTRAPATHS:prepend := "${THISDIR}/gst1-plugins-base:"
SRC_URI:append = "file://0001-add-hdr-meta-parse.patch "
SRC_URI:append = "file://0002-appsrc-clear-position-when-flush-stop-event-reset_ti.patch "
SRC_URI:append = "file://0003-add-new-ftye-wmf-for-qt_type_find.patch "
SRC_URI:append = "file://0004-fix-SWPL-78539-support-vorbis.patch "
SRC_URI:append = "file://0005-support-ac4-typefind.patch "
SRC_URI:append = "file://0006-add-new-API-for-HDR-display-metadata.patch "
SRC_URI:append = "file://0007-add-buf-clip-control-and-add-dump-es.patch "
SRC_URI:append = "file://0008-support-check-buffers-count-and-time-queued-in-appsr.patch "
SRC_URI:append = "file://0009-remove-gst-mp3-typefind-feature.patch "

DEPENDS += " libopus"
RDEPENDS:${PN} += " libopus"

PACKAGECONFIG:append = " opus"
PACKAGECONFIG[opus]  = "-Dopus=enabled,-Dopus=disabled,libopus"
