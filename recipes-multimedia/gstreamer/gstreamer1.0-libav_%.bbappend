FILESEXTRAPATHS_prepend := "${THISDIR}/gst1-libav:"
SRC_URI_append = "file://0001-libav-CB0-implement-gstlibav-with-ffmpeg-in-Gstreame.patch "
SRC_URI_append = "file://0002-libav-add-av1-support.patch "
SRC_URI_append = "file://0003-remove-codec-data-for-h264-h265-codec-ts-containe-in-avdemux.patch "