FILESEXTRAPATHS_prepend := "${THISDIR}/gst1-libav:"
SRC_URI_append = "file://0001-libav-CB0-implement-gstlibav-with-ffmpeg-in-Gstreame.patch "
SRC_URI_append = "file://0002-libav-add-av1-support.patch "
SRC_URI_append = "file://0003-increase-the-MAX_STREAMS-from-20-to-40.patch "
SRC_URI_append = "file://0004-set-framerate-into-caps.patch "
SRC_URI_append = "file://0005-fix_SWPL-75485_support_ac4.patch "
SRC_URI_append = "file://0006-make-negative-pts-to-invalid.patch "
SRC_URI_append = "file://0007-skip-mpegts-h264-h265-and-avi-h264-h265-parser-element.patch "
SRC_URI_append = "file://0008-fix-SWPL-76313-wav-playback.patch "
SRC_URI_append = "file://0009-skip-matroska-webm-h264-h265-parser-element.patch "
SRC_URI_append = "file://0010-CB1-correct-mpeg-sink-caps.patch "
SRC_URI_append = "file://0011-fix_SWPL-79942-support-lpcm.patch "

TARGET_CFLAGS += "-DAMFFMPEG"