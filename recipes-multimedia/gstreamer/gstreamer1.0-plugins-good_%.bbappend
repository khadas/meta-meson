FILESEXTRAPATHS_prepend := "${THISDIR}/gst1-plugins-good:"
SRC_URI_append = "file://0001-fix-gst-not-respect-buffer-config.patch "
SRC_URI_append = "file://0002-support-ac-4.patch "
SRC_URI_append = "file://0003-support-dolby-vision-fourcc.patch "
SRC_URI_append = "file://0004-support-event-and-dma.patch "
SRC_URI_append = "file://0005-add-sinput-for-vdin.patch "
SRC_URI_append = "file://0006-add-more-condition-to-select-stream-in-qtdemux.patch "
SRC_URI_append = "file://0007-dont-treat-ac4-chunks-as-samples.patch "
SRC_URI_append = "file://0008-add-audio-dtse-case-in-qtdemux.patch "


