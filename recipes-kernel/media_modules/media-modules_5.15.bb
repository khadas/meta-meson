inherit module

SUMMARY = "Amlogic media driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

MBRANCH = "amlogic-5.15-dev"
#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/amlogic/media_modules.git;protocol=${AML_GIT_PROTOCOL};branch=${MBRANCH};"

#For common patches
MDIR = "media_modules"
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/hardware/aml-5.15/amlogic/${MDIR}')}"

#SRCREV ?= "${AUTOREV}"
PV ?= "git${SRCPV}"

do_configure[noexec] = "1"

do_install() {
    if [ -f "${S}/firmware/${CHIPSET_NAME}/video_ucode.bin" ];then
        MEDIA_MODULES_UCODE_BIN="${S}/firmware/${CHIPSET_NAME}/video_ucode.bin"
    else
        MEDIA_MODULES_UCODE_BIN="${S}/firmware/video_ucode.bin"
    fi
    MEDIADIR=${D}/lib/modules/${KERNEL_VERSION}/kernel/media
    FIRMWAREDIR=${D}/lib/firmware/video/
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    mkdir -p ${MEDIADIR} ${FIRMWAREDIR}
    install -m 0666 ${MEDIA_MODULES_UCODE_BIN} ${FIRMWAREDIR}

    oe_runmake INSTALL_MOD_PATH=${D} INSTALL_MOD_STRIP=1 modules_install
    ${STAGING_KERNEL_DIR}/extra_modules_install.sh media ${D}/lib/modules/${KERNEL_VERSION}/extra  ${MEDIADIR}
    rm -rf ${D}/lib/modules/${KERNEL_VERSION}/extra
}

FILES:${PN} = " \
        /lib/firmware/video/video_ucode.bin \
        "

# Header file provided by a separate package
DEPENDS += ""

MEDIA_CONFIGS = " \
                 CONFIG_AMLOGIC_MEDIA_VDEC_MPEG12=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_MPEG2_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_MPEG4=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_MPEG4_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_VC1=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_H264=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_H264_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_H264_MVC=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_H265=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_VP9=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_MJPEG=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_MJPEG_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_REAL=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_AVS_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_AVS2=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_AV1=m \
                 CONFIG_AMLOGIC_MEDIA_ENHANCEMENT_DOLBYVISION=y \
                 CONFIG_AMLOGIC_MEDIA_GE2D=y \
                 CONFIG_AMLOGIC_MEDIA_V4L_DEC=y \
                 "

MEDIA_CONFIGS:append:t3 = "\
                 CONFIG_AMLOGIC_MEDIA_VENC_COMMON=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_H264=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_JPEG=m \
                 "
MEDIA_CONFIGS:append:t3x = "\
                 CONFIG_AMLOGIC_MEDIA_VENC_COMMON=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_H264=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_JPEG=m \
                 "
MEDIA_CONFIGS:append:t5m = "\
                 CONFIG_AMLOGIC_MEDIA_VENC_COMMON=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_H264=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_JPEG=m \
                 "
MEDIA_CONFIGS:append:s7d = "\
                 CONFIG_AMLOGIC_MEDIA_VENC_COMMON=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_H264=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_JPEG=m \
                 "
MEDIA_CONFIGS:append:t7 = "\
                 CONFIG_AMLOGIC_MEDIA_VENC_COMMON=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_JPEG=m \
                 "
MEDIA_CONFIGS:append:sc2 = "\
                 CONFIG_AMLOGIC_MEDIA_VENC_COMMON=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_H264=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_H265=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_JPEG=m \
                 "
MEDIA_CONFIGS:append:s5 = "\
                 CONFIG_AMLOGIC_MEDIA_VENC_COMMON=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_MEMALLOC=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_VCENC=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_JPEG=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_H265_FB=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_VP9_FB=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_AV1_FB=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_AVS2_FB=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_AVS3=m \
                 "
MEDIA_CONFIGS:append:t5d = "\
                 CONFIG_AMLOGIC_HW_DEMUX=m \
                 "

MEDIA_CONFIGS:bf201 = " \
                 CONFIG_AMLOGIC_MEDIA_VDEC_MPEG2_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_H264_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_H265=m \
                 CONFIG_AMLOGIC_MEDIA_GE2D=y \
                 "

MEDIA_CONFIGS:bg201 = " \
                 CONFIG_AMLOGIC_MEDIA_VDEC_MPEG2_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_H264_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_H265=m \
                 CONFIG_AMLOGIC_MEDIA_GE2D=y \
                 "

MEDIA_CONFIGS:ap222-zapper = " \
                 CONFIG_AMLOGIC_MEDIA_VDEC_MPEG2_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_H264_MULTI=m \
                 CONFIG_AMLOGIC_MEDIA_VDEC_H265=m \
                 CONFIG_AMLOGIC_MEDIA_GE2D=y \
                 "

MEDIA_CONFIGS:append:g12b = "\
                 CONFIG_AMLOGIC_MEDIA_VENC_COMMON=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_H264=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_H265=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_JPEG=m \
                 "
MEDIA_CONFIGS:append:sm1 = "\
                 CONFIG_AMLOGIC_MEDIA_VENC_COMMON=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_H264=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_H265=m \
                 CONFIG_AMLOGIC_MEDIA_VENC_JPEG=m \
                 "

S = "${WORKDIR}/git"
EXTRA_OEMAKE='-C ${STAGING_KERNEL_DIR} M="${S}/drivers" EXTRA_CFLAGS=${MEDIA_CONFIGS} modules'


KERNEL_MODULE_AUTOLOAD += "amvdec_avs2_v4l"
KERNEL_MODULE_AUTOLOAD += "amvdec_h265_v4l"
KERNEL_MODULE_AUTOLOAD += "amvdec_mh264_v4l"
KERNEL_MODULE_AUTOLOAD += "amvdec_mmjpeg_v4l"
KERNEL_MODULE_AUTOLOAD += "amvdec_mmpeg12_v4l"
KERNEL_MODULE_AUTOLOAD += "amvdec_mmpeg4_v4l"
KERNEL_MODULE_AUTOLOAD += "amvdec_vp9_v4l"
KERNEL_MODULE_AUTOLOAD += "amvdec_av1_v4l"
KERNEL_MODULE_AUTOLOAD += "amvdec_av1"
KERNEL_MODULE_AUTOLOAD += "amvdec_mavs"
KERNEL_MODULE_AUTOLOAD += "amvdec_h264"
KERNEL_MODULE_AUTOLOAD += "amvdec_h264mvc"
KERNEL_MODULE_AUTOLOAD += "amvdec_h265"
KERNEL_MODULE_AUTOLOAD += "amvdec_mh264"
KERNEL_MODULE_AUTOLOAD += "amvdec_mjpeg"
KERNEL_MODULE_AUTOLOAD += "amvdec_mmjpeg"
KERNEL_MODULE_AUTOLOAD += "amvdec_mmpeg4"
KERNEL_MODULE_AUTOLOAD += "amvdec_mpeg12"
KERNEL_MODULE_AUTOLOAD += "amvdec_mmpeg12"
KERNEL_MODULE_AUTOLOAD += "amvdec_mpeg4"
KERNEL_MODULE_AUTOLOAD += "amvdec_real"
KERNEL_MODULE_AUTOLOAD += "amvdec_vc1"
KERNEL_MODULE_AUTOLOAD += "amvdec_vp9"
KERNEL_MODULE_AUTOLOAD += "decoder_common"
KERNEL_MODULE_AUTOLOAD += "firmware"
KERNEL_MODULE_AUTOLOAD += "media_clock"
KERNEL_MODULE_AUTOLOAD += "media_sync"
KERNEL_MODULE_AUTOLOAD += "stream_input"
KERNEL_MODULE_AUTOLOAD += "amvdec_ports"
#KERNEL_MODULE_AUTOLOAD += "aml_hardware_dmx"
#KERNEL_MODULE_AUTOLOAD += "vpu"
KERNEL_MODULE_AUTOLOAD:append:s5 = " amvdec_h265_fb_v4l amvdec_vp9_fb_v4l \
  amvdec_av1_fb_v4l amvdec_avs2_fb_v4l \
  amvdec_avs3 amvdec_avs3_v4l "

KERNEL_MODULE_AUTOLOAD:append:t3 = " encoder_common encoder jpegenc"
KERNEL_MODULE_AUTOLOAD:append:t3x = " encoder_common encoder jpegenc"
KERNEL_MODULE_AUTOLOAD:append:t5m = " encoder_common encoder jpegenc"
KERNEL_MODULE_AUTOLOAD:append:t7 = " encoder_common jpegenc amvenc_multi"
KERNEL_MODULE_AUTOLOAD:append:sc2 = " encoder_common encoder jpegenc vpu"
KERNEL_MODULE_AUTOLOAD:append:s5 = " encoder_common jpegenc memalloc vc8000"
KERNEL_MODULE_AUTOLOAD:append:g12b = " encoder_common encoder jpegenc vpu"
KERNEL_MODULE_AUTOLOAD:append:sm1 = " encoder_common encoder jpegenc vpu"
KERNEL_MODULE_AUTOLOAD:append:s7d = " encoder_common encoder jpegenc"

# Skip auto loading for Zapper project
MODULE_AUTOLOAD_ZAPPER_SKIP = "\
  amvdec_avs2_v4l \
  amvdec_h265_v4l \
  amvdec_mh264_v4l \
  amvdec_mmjpeg_v4l \
  amvdec_mmpeg12_v4l \
  amvdec_mmpeg4_v4l \
  amvdec_vp9_v4l \
  amvdec_av1_v4l \
  amvdec_av1 \
  amvdec_mavs \
  amvdec_h264 \
  amvdec_h264mvc \
  amvdec_mjpeg \
  amvdec_mmjpeg \
  amvdec_mmpeg4 \
  amvdec_mpeg12 \
  amvdec_mpeg4 \
  amvdec_real \
  amvdec_vc1 \
  amvdec_vp9 \
  decoder_common \
  firmware \
  media_clock \
  stream_input \
  amvdec_ports \
"

KERNEL_MODULE_AUTOLOAD:remove = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', '${MODULE_AUTOLOAD_ZAPPER_SKIP}', '', d)}"

KERNEL_MODULE_PROBECONF += "amvdec_ports amvdec_mh264"
module_conf_amvdec_ports = "options amvdec_ports multiplanar=1 vp9_need_prefix=1 av1_need_prefix=1"
module_conf_amvdec_mh264 = "options amvdec_mh264 error_proc_policy=8376310"
INSANE_SKIP:${PN} = "installed-vs-shipped"
