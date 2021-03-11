FILESEXTRAPATHS_prepend := "${THISDIR}/gst1-plugins-good:"
SRC_URI_append = "file://0001-fix-gst-not-respect-buffer-config.patch "
SRC_URI_append = "file://0002-support-ac-4.patch "
SRC_URI_append = "file://0003-support-dolby-vision-fourcc.patch "
SRC_URI_append = "file://0004-support-event-and-dma.patch "

