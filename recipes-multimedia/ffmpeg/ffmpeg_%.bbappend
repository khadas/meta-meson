PN = 'ffmpeg'
LICENSE = "AMLOGIC"
LICENSE:${PN} = "AMLOGIC"
LICENSE:libavcodec = "AMLOGIC"
LICENSE:libavdevice = "AMLOGIC"
LICENSE:libavfilter = "AMLOGIC"
LICENSE:libavformat = "AMLOGIC"
LICENSE:libavresample = "AMLOGIC"
LICENSE:libavutil = "AMLOGIC"
LICENSE:libpostproc = "AMLOGIC"
LICENSE:libswresample = "AMLOGIC"
LICENSE:libswscale = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#SRCREV = "${AUTOREV}"
S = "${WORKDIR}/git"
#SRC_URI = " git://${AML_GIT_ROOT}/platform/external/ffmpeg-aml;protocol=${AML_GIT_PROTOCOL};branch=s-amlogic"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = " file://0001-do-remove-config-headfile.patch"

TARGET_CFLAGS += "-DAMFFMPEG"

EXTRA_OECONF:append = " \
    --disable-gpl \
    --disable-libx264 \
    --disable-ffprobe \
    --disable-ffmpeg \
    --disable-postproc \
    --disable-symver \
    --disable-doc \
    --disable-extra-warnings \
    --disable-bsf=dca_core \
    --disable-bsf=eac3_core \
    --disable-bsf=truehd_core \
    --disable-bsf=vvc_mp4toannexb \
    --disable-bsf=vvc_metadata \
    --disable-parser=mlp \
    --disable-parser=dca \
    --disable-parser=dolby_e \
    --disable-parser=vvc \
    --disable-demuxer=s337m \
    --disable-decoder=apng               \
    --disable-decoder=cavs               \
    --disable-decoder=dxa                \
    --disable-decoder=exr                \
    --disable-decoder=flashsv            \
    --disable-decoder=flashsv2           \
    --disable-decoder=g2m                \
    --disable-decoder=h264_crystalhd     \
    --disable-decoder=h264_mediacodec    \
    --disable-decoder=h264_mmal          \
    --disable-decoder=h264_qsv           \
    --disable-decoder=h264_rkmpp         \
    --disable-decoder=hevc_qsv           \
    --disable-decoder=hevc_rkmpp         \
    --disable-decoder=lscr               \
    --disable-decoder=mpeg4_crystalhd    \
    --disable-decoder=mpeg4_mmal         \
    --disable-decoder=mpeg2_mmal         \
    --disable-decoder=mpeg2_crystalhd    \
    --disable-decoder=mpeg2_qsv          \
    --disable-decoder=mpeg2_mediacodec   \
    --disable-decoder=mscc               \
    --disable-decoder=msmpeg4_crystalhd  \
    --disable-decoder=mvha               \
    --disable-decoder=mwsc               \
    --disable-decoder=png                \
    --disable-decoder=rasc               \
    --disable-decoder=rscc               \
    --disable-decoder=screenpresso       \
    --disable-decoder=srgc               \
    --disable-decoder=tdsc               \
    --disable-decoder=tscc               \
    --disable-decoder=vc1_crystalhd      \
    --disable-decoder=vc1_mmal           \
    --disable-decoder=vc1_qsv            \
    --disable-decoder=vp8_rkmpp          \
    --disable-decoder=vp9_rkmpp          \
    --disable-decoder=wcmv               \
    --disable-decoder=wmv1               \
    --disable-decoder=wmv2               \
    --disable-decoder=wmv3_crystalhd     \
    --disable-decoder=zerocodec          \
    --disable-decoder=zlib               \
    --disable-decoder=zmbv               \
    --disable-decoder=ac3                \
    --disable-decoder=ac3_fixed          \
    --disable-decoder=amrnb              \
    --disable-decoder=dca                \
    --disable-decoder=eac3               \
    --disable-decoder=mlp                \
    --disable-decoder=truehd             \
    --disable-decoder=aac_at             \
    --disable-decoder=ac3_at             \
    --disable-decoder=adpcm_ima_qt_at    \
    --disable-decoder=alac_at            \
    --disable-decoder=amr_nb_at          \
    --disable-decoder=eac3_at            \
    --disable-decoder=gsm_ms_at          \
    --disable-decoder=ilbc_at            \
    --disable-decoder=mp1_at             \
    --disable-decoder=mp2_at             \
    --disable-decoder=mp3_at             \
    --disable-decoder=pcm_alaw_at        \
    --disable-decoder=pcm_mulaw_at       \
    --disable-decoder=qdmc_at            \
    --disable-decoder=qdm2_at            \
    --disable-decoder=libaribb24         \
    --disable-decoder=libcelt            \
    --disable-decoder=libcodec2          \
    --disable-decoder=libdav1d           \
    --disable-decoder=libdavs2           \
    --disable-decoder=libfdk_aac         \
    --disable-decoder=libgsm             \
    --disable-decoder=libgsm_ms          \
    --disable-decoder=libilbc            \
    --disable-decoder=libopencore_amrnb  \
    --disable-decoder=libopencore_amrwb  \
    --disable-decoder=libopenjpeg        \
    --disable-decoder=libopus            \
    --disable-decoder=librsvg            \
    --disable-decoder=libspeex           \
    --disable-decoder=libuavs3d          \
    --disable-decoder=libvorbis          \
    --disable-decoder=libvpx_vp8         \
    --disable-decoder=libvpx_vp9         \
    --disable-decoder=libzvbi_teletext   \
    --disable-decoder=libaom_av1         \
    --disable-decoder=av1_cuvid          \
    --disable-decoder=av1_qsv            \
    --disable-decoder=libopenh264        \
    --disable-decoder=h264_cuvid         \
    --disable-decoder=hevc_cuvid         \
    --disable-decoder=hevc_mediacodec    \
    --disable-decoder=mjpeg_cuvid        \
    --disable-decoder=mjpeg_qsv          \
    --disable-decoder=mpeg1_cuvid        \
    --disable-decoder=mpeg2_cuvid        \
    --disable-decoder=mpeg4_cuvid        \
    --disable-decoder=mpeg4_mediacodec   \
    --disable-decoder=vc1_cuvid          \
    --disable-decoder=vp8_cuvid          \
    --disable-decoder=vp8_mediacodec     \
    --disable-decoder=vp8_qsv            \
    --disable-decoder=vp9_cuvid          \
    --disable-decoder=vp9_mediacodec     \
    --disable-decoder=vp9_mediacodec     \
    --disable-decoder=vp9_qsv            \
    --disable-decoder=dolby_e            \
    --disable-decoder=mpl2               \
    --disable-decoder=truemotion1        \
    --disable-decoder=mss1               \
    --disable-decoder=mss2               \
    --disable-decoder=msa1               \
    --disable-decoder=mts2               \
    --disable-decoder=vvc                \
    --disable-demuxer=ac3          \
    --disable-demuxer=avisynth     \
    --disable-demuxer=dts          \
    --disable-demuxer=dtshd        \
    --disable-demuxer=eac3         \
    --disable-demuxer=mlp          \
    --disable-demuxer=spdif        \
    --disable-demuxer=truehd       \
    --disable-demuxer=libgme       \
    --disable-demuxer=libmodplug   \
    --disable-demuxer=libopenmpt   \
    --disable-demuxer=vapoursynth  \
    --disable-demuxer=dv  \
    --disable-demuxer=mpl2 \
    --disable-demuxer=vvc \
    --disable-muxer=ac3         \
    --disable-muxer=dts         \
    --disable-muxer=eac3        \
    --disable-muxer=spdif       \
    --disable-muxer=chromaprint \
    --disable-muxer=dv \
    --disable-muxer=cavs2video \
    --disable-muxer=smoothstreaming \
    --disable-encoder=apng               \
    --disable-encoder=exr                \
    --disable-encoder=flashsv            \
    --disable-encoder=flashsv2           \
    --disable-encoder=hap                \
    --disable-encoder=png                \
    --disable-encoder=utvideo            \
    --disable-encoder=zlib               \
    --disable-encoder=zmbv               \
    --disable-encoder=ac3                \
    --disable-encoder=ac3_fixed          \
    --disable-encoder=dca                \
    --disable-encoder=eac3               \
    --disable-encoder=flac               \
    --disable-encoder=mlp                \
    --disable-encoder=truehd             \
    --disable-encoder=aac_at             \
    --disable-encoder=alac_at            \
    --disable-encoder=ilbc_at            \
    --disable-encoder=pcm_alaw_at        \
    --disable-encoder=pcm_mulaw_at       \
    --disable-encoder=libaom_av1         \
    --disable-encoder=libcodec2          \
    --disable-encoder=libfdk_aac         \
    --disable-encoder=libgsm             \
    --disable-encoder=libgsm_ms          \
    --disable-encoder=libilbc            \
    --disable-encoder=libmp3lame         \
    --disable-encoder=libopencore_amrnb  \
    --disable-encoder=libopenjpeg        \
    --disable-encoder=libopus            \
    --disable-encoder=librav1e           \
    --disable-encoder=libshine           \
    --disable-encoder=libspeex           \
    --disable-encoder=libsvtav1          \
    --disable-encoder=libtheora          \
    --disable-encoder=libtwolame         \
    --disable-encoder=libvo_amrwbenc     \
    --disable-encoder=libvorbis          \
    --disable-encoder=libvpx_vp8         \
    --disable-encoder=libvpx_vp9         \
    --disable-encoder=libwebp_anim       \
    --disable-encoder=libwebp            \
    --disable-encoder=libx262            \
    --disable-encoder=libx264            \
    --disable-encoder=libx264rgb         \
    --disable-encoder=libx265            \
    --disable-encoder=libxavs            \
    --disable-encoder=libxavs2           \
    --disable-encoder=libxvid            \
    --disable-encoder=aac_mf             \
    --disable-encoder=ac3_mf             \
    --disable-encoder=libopenh264        \
    --disable-encoder=h264_amf           \
    --disable-encoder=h264_mf            \
    --disable-encoder=h264_nvenc         \
    --disable-encoder=h264_omx           \
    --disable-encoder=h264_qsv           \
    --disable-encoder=h264_vaapi         \
    --disable-encoder=h264_videotoolbox  \
    --disable-encoder=nvenc              \
    --disable-encoder=nvenc_h264         \
    --disable-encoder=nvenc_hevc         \
    --disable-encoder=hevc_amf           \
    --disable-encoder=hevc_mf            \
    --disable-encoder=hevc_nvenc         \
    --disable-encoder=hevc_qsv           \
    --disable-encoder=hevc_vaapi         \
    --disable-encoder=hevc_videotoolbox  \
    --disable-encoder=libkvazaar         \
    --disable-encoder=mjpeg_qsv          \
    --disable-encoder=mjpeg_vaapi        \
    --disable-encoder=mp3_mf             \
    --disable-encoder=mpeg2_qsv          \
    --disable-encoder=mpeg2_vaapi        \
    --disable-encoder=mpeg4_omx          \
    --disable-encoder=vp8_vaapi          \
    --disable-encoder=vp9_vaapi          \
    --disable-encoder=vp9_qsv            \
    --disable-encoder=wmv1               \
    --disable-encoder=wmv2               \
    --enable-libxml2                     \
    --disable-libsmbclient               \
    --disable-protocol=libsmbclient      \
"

DEPENDS += " libxml2 "
RDEPENDS:${PN} += " libxml2 "

INSANE_SKIP:${PN} = "dev-so ldflags dev-elf installed-vs-shipped"
INSANE_SKIP:${PN}-dev = "dev-so ldflags dev-elf"

FILES:${PN} += " ${libdir}/*.so ${libdir}/*.so.* "
FILES:${PN}-dev = "${includedir}/* "
