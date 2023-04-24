FILESEXTRAPATHS:prepend := "${THISDIR}/gst1-plugins-good:"
SRC_URI:append = "file://0001-fix-gst-not-respect-buffer-config.patch "
SRC_URI:append = "file://0002-support-ac-4.patch "
SRC_URI:append = "file://0003-support-dolby-vision-fourcc.patch "
SRC_URI:append = "file://0004-support-event-and-dma.patch "
SRC_URI:append = "file://0005-add-sinput-for-vdin.patch "
SRC_URI:append = "file://0006-add-more-condition-to-select-stream-in-qtdemux.patch "
SRC_URI:append = "file://0007-dont-treat-ac4-chunks-as-samples.patch "
SRC_URI:append = "file://0008-add-audio-dtse-case-in-qtdemux.patch "
SRC_URI:append = "file://0010-modify-free-sequence-to-resolve-video-switch-problam.patch "
SRC_URI:append = "file://00011-correct-pts-if-have-elst-in-qtdemux.patch "
SRC_URI:append = "file://00012-add-v4l2av1dec-element.patch "
SRC_URI:append = "file://00013-add-v4l2mpeg2dec-element.patch "
SRC_URI:append = "file://00014-add-v4l2-aml-vdec-h.patch "
SRC_URI:append = "file://00015-add-func-to-set-vdec-parm-in-v4l2dec.patch "
SRC_URI:append = "file://00016-compare-pts-with-valid-value-in-get-oldest-frame-function.patch "
SRC_URI:append = "file://00017-add-flow-in-vbp-to-adapt-to-the-buf-circulation-of-decoder.patch "
SRC_URI:append = "file://00018-set-default-capture-vbp-buf-mode.patch "
SRC_URI:append = "file://00019-qtdemux-fix-f4v-pts-error.patch "
SRC_URI:append = "file://00020-add-systemstream-in-v4l2-sinkpad-caps-when-reg.patch "
SRC_URI:append = "file://00021-SWPL-73078-DTS-wav.patch "
SRC_URI:append = "file://00022-mode-gst-env-name.patch "
SRC_URI:append = "file://00023_SH-9750-Dump_decode_frame.patch "
SRC_URI:append = "file://00024-qtdemux-remove-support-dolby-vision-unknown-Compatibility-ID.patch "
SRC_URI:append = "file://00026-correct-io-pointer-when-parse-trun.patch "
SRC_URI:append = "file://00027-add-case-to-parse-display-metadata.patch "
SRC_URI:append = "file://00028-fix_SWPL-90736_check_dv_blel_for_mp4.patch "
SRC_URI:append = "file://00029-qtdemux-remove-cslg-handling.patch "
SRC_URI:append = "file://00030-init-mastering-display-present.patch "
SRC_URI:append = "file://00031-add-case-18-for-ARIB-STD-B67-to-show-hdr-info.patch "
SRC_URI:append = "file://00032-set-systemstream-to-false-when-mpeg1.patch "
SRC_URI:append = "file://00033-SWPL-121035_fix_av1_codec_qtdemux.patch "
SRC_URI:append = "file://00034-SWPL-121124_av1_codecdata_mkvdemux.patch "
