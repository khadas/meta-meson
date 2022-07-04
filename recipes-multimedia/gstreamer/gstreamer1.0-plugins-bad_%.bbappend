FILESEXTRAPATHS_prepend := "${THISDIR}/gst1-plugins-bad:"
SRC_URI_append = "file://0001-gistwaylandsink-use-gstdrmbufferpool.patch "
SRC_URI_append = "file://0002-add-new-registration-id-0x44545348-for-audio-dts-in-tsdemux.patch "
SRC_URI_append = "file://0003-Add-fullscreen-support-in-waylandsink.patch "
SRC_URI_append = "file://0004-support-parse-DVB-DTS.patch "
SRC_URI_append = "file://0005-support-parse-EXT-X-MAP-for-m3u8.patch "
SRC_URI_append = "file://0006-get-more-data-if-offset-is-larger-than-buffer-size-in-h264parser.patch "
SRC_URI_append = "file://0007-do-not-advance-fragment-when-downloading-header.patch "
SRC_URI_append = "file://0008-add-new-registration-id-444f5649-for-tsdemux.patch "

DEPENDS += "gst-aml-drmbufferpool-plugins"

PACKAGECONFIG[vulkan] = ""

