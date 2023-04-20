DESCRIPTION = "ffmpeg-ctc in yocto"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

#PATCHTOOL = "git"

DEPENDS += " liblog openssl"
RDEPENDS:${PN} += " liblog openssl"

EXTRA_OEMAKE = "STAGING_DIR=${STAGING_DIR_TARGET} \
		  TARGET_DIR=${D} \
            "

inherit autotools pkgconfig

DISABLE_STATIC = ""

SRCREV = "${AUTOREV}"
S = "${WORKDIR}/git"

#FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
#SRC_URI:append = " file://ffmpeg_ctc/0001-do-not-install-pkgconfig.patch;patch=1;"
#SRC_URI:append = " file://ffmpeg_ctc/0002-do-remove-config-headfile.patch;patch=2;"
#SRC_URI:append = " file://ffmpeg_ctc/0003-add-execute-permission-for-configure-files.patch"

EXTRA_FFCONF = " \
	--disable-ffmpeg \
	--disable-ffprobe \
	--disable-postproc \
	--disable-symver \
	--disable-doc \
	--disable-small \
	--disable-shared \
    --disable-encoder=flac \
    --disable-encoders \
    --disable-decoders \
    --enable-decoder='aac' \
    --disable-muxer='ac3,eac3,dts,dtshd,spdif,dv' \
    --disable-demuxer='dv' \
    --disable-parser='mlp' \
    --disable-bsf=dca_core \
	--disable-extra-warnings \
    --enable-nonfree \
    --enable-openssl \
    --disable-zlib \
    --disable-lzma \
    --disable-bzlib \
	--enable-optimizations \
	--enable-static \
	--enable-pic \
"

EXTRA_OECONF = " \
    \
    --cross-prefix=${TARGET_PREFIX} \
    \
    --ld="${CCLD}" \
    --cc="${CC}" \
    --cxx="${CXX}" \
    --arch=${TARGET_ARCH} \
    --target-os="linux" \
    --enable-cross-compile \
    --extra-cflags="${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}" \
    --extra-ldflags="" \
    --sysroot="${STAGING_DIR_TARGET}" \
    ${EXTRA_FFCONF} \
    --libdir=${libdir}/ffmpeg_ctc \
    --shlibdir=${libdir}/ffmpeg_ctc \
	--incdir=${includedir}/ffmpeg_ctc \
    --datadir=${datadir}/ffmpeg_ctc \
    --pkg-config=pkg-config \
"

do_configure() {
	${S}/configure ${EXTRA_OECONF}
}

TARGET_CFLAGS  += "-I${S}/libavutil/aml/utils/OS_android/ \
    -I${S}/libavutil/aml/utils/OS_linux/ \
    -I${S}/libavutil/aml/utils/ \
    -I${S}/libavformat/aml/tcpproto \
    -I${S}/libavformat/aml/hls \
    -fPIC \
"

do_compile () {
	oe_runmake all
    ${CC} -shared -fPIC -Wl,-Bsymbolic -Wl,--whole-archive \
		libavutil/libavutil.a \
		libavformat/libavformat.a \
		libavcodec/libavcodec.a \
        libswresample/libswresample.a \
        libswscale/libswscale.a \
		-Wl,--no-whole-archive -lpthread \
        -lssl -lcrypto \
        -llog \
		-o libffmpeg_ctc.so

}

do_install() {
    make DESTDIR=${D} install-headers
    install -D -m 0644 config.h ${D}${includedir}/ffmpeg_ctc
    install -d -m 0755 ${D}${includedir}/ffmpeg_ctc/libavformat/aml
    install -d -m 0755 ${D}${includedir}/ffmpeg_ctc/libavformat/aml/hls
    install -d -m 0755 ${D}${includedir}/ffmpeg_ctc/libavformat/aml/tcpproto
    install -d -m 0755 ${D}${includedir}/ffmpeg_ctc/libavformat/aml/utils
    install -D -m 0644 ${S}/libavformat/aml/utils/ffmpegObMsgheader.h ${D}${includedir}/ffmpeg_ctc/libavformat/aml/utils
    install -D -m 0644 ${S}/libavformat/aml/utils/utils.h  ${D}${includedir}/ffmpeg_ctc/libavformat/aml/utils
    install -D -m 0644 ${S}/libavformat/aml/hls/hls_bandwidth.h   ${D}${includedir}/ffmpeg_ctc/libavformat/aml/hls
    install -D -m 0644 ${S}/libavformat/aml/hls/ffhls.h             ${D}${includedir}/ffmpeg_ctc/libavformat/aml/hls
    install -D -m 0644 ${S}/libavformat/aml/tcpproto/dns_cache.h   ${D}${includedir}/ffmpeg_ctc/libavformat/aml/tcpproto
    install -D -m 0644 ${S}/libavformat/aml/tcpproto/getaddrinfo_nonblock.h ${D}${includedir}/ffmpeg_ctc/libavformat/aml/tcpproto
    install -D -m 0644 ${S}/libavformat/aml/tcpproto/fftcp.h               ${D}${includedir}/ffmpeg_ctc/libavformat/aml/tcpproto
    install -D -m 0644 ${S}/libavformat/aml/tcpproto/addrinfo_reorder.h  ${D}${includedir}/ffmpeg_ctc/libavformat/aml/tcpproto
    install -d -m 0755 ${D}${includedir}/ffmpeg_ctc/libavutil/aarch64
    install -D -m 0644 ${S}/libavutil/aarch64/timer.h  ${D}${includedir}/ffmpeg_ctc/libavutil/aarch64
    install -d -m 0755 ${D}${includedir}/ffmpeg_ctc/libavutil/arm
    install -D -m 0644 ${S}/libavutil/arm/timer.h  ${D}${includedir}/ffmpeg_ctc/libavutil/arm
    install -d -m 0755 ${D}${includedir}/ffmpeg_ctc/libavutil/aml/utils/OS_linux
    install -D -m 0644 ${S}/libavutil/aml/utils/OS_linux/amconfigutils.h  ${D}${includedir}/ffmpeg_ctc/libavutil/aml/utils/OS_linux
    install -D -m 0644 ${S}/libavutil/aml/utils/OS_linux/Amsysfsutils.h  ${D}${includedir}/ffmpeg_ctc/libavutil/aml/utils/OS_linux

    mkdir -p ${D}${libdir}/
	install -d -m 0755 ${D}${libdir}/
    install -D -m 0644 libffmpeg_ctc.so ${D}${libdir}/
    ${STRIP} -s ${D}${libdir}/libffmpeg_ctc.so
}

FILES:${PN} = "${bindir}/*"
FILES:${PN} += " ${bindir}/* ${libdir}/*.so"
FILES:${PN}-dev = "${includedir}/* "
INSANE_SKIP:${PN} += " ldflags already-stripped"
INSANE_SKIP:${PN}-dev += "dev-elf"


