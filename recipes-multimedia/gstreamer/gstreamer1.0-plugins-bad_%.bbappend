FILESEXTRAPATHS:prepend := "${THISDIR}/gst1-plugins-bad:"
SRC_URI:append = "file://0001-gistwaylandsink-use-gstdrmbufferpool.patch "
SRC_URI:append = "file://0002-add-new-registration-id-0x44545348-for-audio-dts-in-tsdemux.patch "
SRC_URI:append = "file://0003-Add-fullscreen-support-in-waylandsink.patch "
SRC_URI:append = "file://0004-support-parse-DVB-DTS.patch "
SRC_URI:append = "file://0005-support-parse-EXT-X-MAP-for-m3u8.patch "
SRC_URI:append = "file://0006-get-more-data-if-offset-is-larger-than-buffer-size-in-h264parser.patch "
SRC_URI:append = "file://0007-do-not-advance-fragment-when-downloading-header.patch "
SRC_URI:append = "file://0008-add-new-registration-id-444f5649-for-tsdemux.patch "
SRC_URI:append = "file://0009-add-gap-time-of-pcr-to-last-pcr.patch "
SRC_URI:append = "file://0010-do-not-create-pts-in-h264parser-when-pts-is-invalid.patch "
SRC_URI:append = "file://0011-fx-not-found-start-code-issue-in-one-buffer.patch "
SRC_URI:append = "file://0012-tsdemux-send-event-AML-SET-MAX-BYTE-SIZE.patch "
SRC_URI:append = "file://0012-tsdemux-process-private-data.patch "
SRC_URI:append = "file://0013-playbin-support-HDCP.patch "
SRC_URI:append = "file://0014-Add-tsdemux-no-checkpcr-prop-for-wds.patch "
SRC_URI:append = "file://0015-tsdemux-gst-buffer-unref-iv_buffer.patch "
SRC_URI:append = "file://0016-Discard-ts-pcr-check-when-pcr-abnormal.patch "
SRC_URI:append = "file://0017-seek-patch-y-WXLTV-15204-mod-t921d.patch "
SRC_URI:append = "file://0018-mpegtsdemux-Fix-logic-bug-in-PCR-detection.patch "
SRC_URI:append = "file://0019-wait-switch-expose-then-seek-final.patch "
SRC_URI:append = "file://0020-tsdemux-handle-seek-with-GST_SEEK_FLAG_TRICKMODE_NO_.patch "
SRC_URI:append = "file://0021-protect_pts-for-miracast.patch "
SRC_URI:append = "file://0022-wait-switch-expose-then-seek-final_fix-multiseek.patch "
SRC_URI:append = "file://0023-adptivedemux-add-adptivedemux-seek-endless-loop-not-.patch "
SRC_URI:append = "file://0024-got-first-I-frame.patch "
SRC_URI:append = "file://0025-http-response-403-forbidden.patch "
SRC_URI:append = "file://0026-airplay-youtube-play-slowly-for-3-4min.patch "
SRC_URI:append = "file://0027-The-sowing-time-exceeds-duration-nosupport.patch "
SRC_URI:append = "file://0028-startTime-player-screen-struck.patch "
SRC_URI:append = "file://0029-Airplay-play-failed.patch "
SRC_URI:append = "file://0030-Some-video-source-error.patch "
SRC_URI:append = "file://0031-airplay-not-init-will-cause-coredump.patch "
SRC_URI:append = "file://0032-gstreamer-reduce-queue-size-and-refine-read-blocksiz.patch "
SRC_URI:append = "file://0033-tsdemux-support-auto-playbin-for-miracast-PD-OTT-285.patch "
SRC_URI:append = "file://0034-gstreamer-fetch-uri-403.patch "
SRC_URI:append = "file://0035-mpeg-format-start-play-time-error.patch "
SRC_URI:append = "file://0036-mpeg-segment-start-time-error.patch "
SRC_URI:append = "file://0037-hlsdemux-parsem3u8-apos-more-than-vpos.patch "
SRC_URI:append = "file://0038-airplay-select-url-according-bandwidth.patch "
SRC_URI:append = "file://0039-airplay-gst-send-message-for-app-use.patch "
SRC_URI:append = "file://0040-live-app-ts-streamer-pcr-error.patch "
SRC_URI:append = "file://0041-tsdemux-reset-base-pcrtime-error.patch "
SRC_URI:append = "file://0042-modify-ts-demux-to-parse-dv-parameter.patch "
SRC_URI_append = "file://0043-SWPL-110753_improve_tsdemux_seek.patch "
SRC_URI_append = "file://0044-SWPL-101568-support-hls-discontinuity.patch "
SRC_URI_append = "file://0045-SWPL-124457-set-base_pcr-after-frame-packets.patch "
SRC_URI_append = "file://0046-SWPL-100464-add-parse-sequence-extention.patch "
SRC_URI_append = "file://0047-SWPL-85490-recalculate-frame-rate.patch "
SRC_URI_append = "file://0048-modify-hls-ts-demux-videoparser-to-parse-sampleAES.patch "

DEPENDS += "gst-aml-drmbufferpool-plugins"

PACKAGECONFIG[vulkan] = ""

PACKAGECONFIG:remove = "smoothstreaming"


