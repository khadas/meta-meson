FILESEXTRAPATHS_prepend := "${THISDIR}/gst1-libav:"
SRC_URI_append = "file://0001-libav-CB0-implement-gstlibav-with-ffmpeg-in-Gstreame.patch "
SRC_URI_append = "file://0002-libav-add-av1-support.patch "
SRC_URI_append = "file://0003-increase-the-MAX_STREAMS-from-20-to-40.patch "
SRC_URI_append = "file://0004-set-framerate-into-caps.patch "
SRC_URI_append = "file://0005-fix_SWPL-75485_support_ac4.patch "
SRC_URI_append = "file://0006-make-negative-pts-to-invalid.patch "
#SRC_URI_append = "file://0007-add-caps-field-to-skip-h265-parser.patch "
#SRC_URI_append = "file://0008-add-caps-field-to-skip-h264-parser.patch "
SRC_URI_append = "file://0009-add-tsdemux-and-remove-codec_data-for_H264_and_H265.patch "
SRC_URI_append = "file://0010-fix-SWPL-76313-wav-playback.patch "
TARGET_CFLAGS += "-DAMFFMPEG"