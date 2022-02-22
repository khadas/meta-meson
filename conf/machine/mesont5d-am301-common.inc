
include conf/machine/include/amlogic_westeros.inc

PREFERRED_PROVIDER_virtual/mesa = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/mesa-gl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgbm = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/egl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgles1 = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgles2 = "libgles-eabihf-dvalin-wayland-drm"

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += " \
        rtk8723bu \
        rtk8723du \
        realtek-btusb \
        realtek-btuart \
        realtek-hciattach \
        pq-prebuilt \
        tuner-prebuilt \
        aucpu-fw \
        gpu \
        optee-linux-driver \
        "

KERNEL_DEVICETREE = "t5d_t950d4_am301_linux_1.5g.dtb t5d_reva_t950d4_am301_linux_1.5g.dtb t5d_t950d4_am301_linux_1g.dtb t5d_reva_t950d4_am301_linux_1g.dtb"
UBOOT_MACHINE = "t5d_am301_v1"
KERNEL_MODULE_AUTOLOAD += " snd-soc-dummy_codec"
KERNEL_MODULE_AUTOLOAD += " snd-soc-aml_t9015"
KERNEL_MODULE_AUTOLOAD += " snd-soc-aml_codec_tl1_acodec"
KERNEL_MODULE_AUTOLOAD += " snd-soc-tas5805"
KERNEL_MODULE_AUTOLOAD += " meson_ir"
KERNEL_MODULE_AUTOLOAD += "aml_aucpu"
KERNEL_MODULE_AUTOLOAD += "dolby_fw"
KERNEL_MODULE_AUTOLOAD += "meson-cpufreq"

DISTRO_FEATURES_append = " drm"
#DISTRO_FEATURES_append = " onepass"
DISTRO_FEATURES_append = " secure-u-boot optee "
DISTRO_FEATURES_append = " widevine "
DISTRO_FEATURES_append = " playready "
DISTRO_FEATURES_append = " amlogic-tv "
DISTRO_FEATURES_append = " amlogic-dvb "
DISTRO_FEATURES_append = " dtvkit"
#DISTRO_FEATURES_remove = " alsa "
DISTRO_FEATURES_append = " alsa "
DISTRO_FEATURES_append = " pam"
DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"
DEFAULT_DISTRO_FEATURES += " systemd"
DISTRO_FEATURES_append = " systemd"
DISTRO_FEATURES_remove = " sysvinit"
#DISTRO_FEATURES_append = " opengl"
DISTRO_FEATURES_append = " gstreamer1"
DISTRO_FEATURES_append = " aml-dtv "
DISTRO_FEATURES_append = " recovery "
DISTRO_FEATURES_append = " swupdate "


OVERRIDES .= ":am301:t5d:onepass"
MACHINEOVERRIDES .= ":client"

DISTRO_FEATURES_append = " bluetooth bluez5 "
EXTRA_BLUETOOTH_STUFF := "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', ' bluez5', ' bluez4', d), '', d)}"
DISTRO_FEATURES_append = "${EXTRA_BLUETOOTH_STUFF}"

# For YouTube Cert. Now it's for 2022
DEVICE_PROPERTY_FIRMWARE_VERSION = "1.0.0"
DEVICE_PROPERTY_MODEL_NAME = "FHD"
DEVICE_PROPERTY_OPERATOR_NAME = "SDKRelease"
DEVICE_PROPERTY_CERT_SCOPE = "amlogic-2022-amlogictvref"
DEVICE_PROPERTY_DIALSERVER_NAME="AmlogicOTT-AM301"
# YouTube define device type
# BDP:   Blu-Ray Player
# GAME:  Game Console
# OTT:   Over-The-Top Device
# STB:   Set-Top-Box, which includes a tuner and demodulator
# TV:    Television Set
DEVICE_PROPERTY_YOUTUBE_DEVICE_TYPE = "TV"
DEVICE_PROPERTY_CHIPSET_MODEL_NUM = "T950D4"
DEVICE_PROPERTY_MODEL_YEAR = "2022"
# End For YouTube

# For amazon
DEVICE_PROPERTY_AMAZON_DTID = "A3SJQCMWSDF1EG"
