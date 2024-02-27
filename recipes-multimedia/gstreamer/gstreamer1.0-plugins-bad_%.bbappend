FILESEXTRAPATHS:prepend := "${THISDIR}/gst1-plugins-bad:"


DEPENDS += "gst-aml-drmbufferpool-plugins"

PACKAGECONFIG[vulkan] = ""

PACKAGECONFIG:remove = "smoothstreaming"


