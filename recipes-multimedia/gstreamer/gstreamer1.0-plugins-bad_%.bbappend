FILESEXTRAPATHS_prepend := "${THISDIR}/gst1-plugins-bad:"
SRC_URI_append = "file://0001-gistwaylandsink-use-gstdrmbufferpool.patch "
SRC_URI_append = "file://0002-add-new-registration-id-0x44545348-for-audio-dts-in-tsdemux.patch "


DEPENDS += "gst-aml-drmbufferpool-plugins"

PACKAGECONFIG[vulkan] = ""

