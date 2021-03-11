FILESEXTRAPATHS_prepend := "${THISDIR}/gst1-plugins-bad:"
SRC_URI_append = "file://0001-gistwaylandsink-use-gstdrmbufferpool.patch "

DEPENDS += "gst-aml-drmbufferpool-plugins"

PACKAGECONFIG[vulkan] = ""

