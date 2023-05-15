
include conf/machine/include/amlogic_westeros.inc

PREFERRED_PROVIDER_virtual/mesa = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/mesa-gl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgbm = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/egl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgles1 = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgles2 = "libgles-eabihf-dvalin-wayland-drm"

MACHINE_EXTRA_RRECOMMENDS += " \
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

CHIPSET_NAME = "T950X4"
KERNEL_DEVICETREE = "t5d_t950x4_am311_linux_1g.dtb t5d_reva_t950x4_am311_linux_1g.dtb t5d_t950x4_am311_linux_512m.dtb t5d_reva_t950x4_am311_linux_512m.dtb"
UBOOT_MACHINE = "t5d_am311_v1"
KERNEL_MODULE_AUTOLOAD += " snd-soc-dummy_codec"
KERNEL_MODULE_AUTOLOAD += " snd-soc-aml_t9015"
KERNEL_MODULE_AUTOLOAD += " snd-soc-aml_codec_tl1_acodec"
KERNEL_MODULE_AUTOLOAD += " snd-soc-tas5805"
KERNEL_MODULE_AUTOLOAD += " meson_ir"
KERNEL_MODULE_AUTOLOAD += "aml_aucpu"
KERNEL_MODULE_AUTOLOAD += "dolby_fw"
KERNEL_MODULE_AUTOLOAD += "meson-cpufreq"

DISTRO_FEATURES:append = " drm"
#DISTRO_FEATURES:append = " onepass"
DISTRO_FEATURES:append = " secure-u-boot optee "
DISTRO_FEATURES:append = " widevine "
DISTRO_FEATURES:append = " playready "
DISTRO_FEATURES:append = " amlogic-tv "
DISTRO_FEATURES:append = " amlogic-dvb "
#DISTRO_FEATURES:remove = " alsa "
DISTRO_FEATURES:append = " alsa "
DISTRO_FEATURES:append = " pam"
DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"
DEFAULT_DISTRO_FEATURES += " systemd"
DISTRO_FEATURES:append = " systemd"
DISTRO_FEATURES:remove = " sysvinit"
#DISTRO_FEATURES:append = " opengl"
DISTRO_FEATURES:append = " gstreamer1"
DISTRO_FEATURES:append = " aml-dtv "
DISTRO_FEATURES:append = " recovery "
DISTRO_FEATURES:append = " swupdate "


OVERRIDES .= ":am311:t5d:onepass"
MACHINEOVERRIDES .= ":client"

DISTRO_FEATURES:append = " bluetooth bluez5 "
EXTRA_BLUETOOTH_STUFF := "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', ' bluez5', ' bluez4', d), '', d)}"
DISTRO_FEATURES:append = "${EXTRA_BLUETOOTH_STUFF}"

# For YouTube Cert. Now it's for 2023
DEVICE_PROPERTY_FIRMWARE_VERSION = "1.0.0"
DEVICE_PROPERTY_MODEL_NAME = "NA"
DEVICE_PROPERTY_OPERATOR_NAME = "SDKRelease"
DEVICE_PROPERTY_YOUTUBE_CERT_SCOPE = "NA"
DEVICE_PROPERTY_PERSIST_DIALSERVER_NAME="AmlogicOTT-AM311"
# YouTube define device type
# BDP:   Blu-Ray Player
# GAME:  Game Console
# OTT:   Over-The-Top Device
# STB:   Set-Top-Box, which includes a tuner and demodulator
# TV:    Television Set
DEVICE_PROPERTY_DEVICE_TYPE = "TV"
DEVICE_PROPERTY_CHIPSET = "T921D"
DEVICE_PROPERTY_MODEL_YEAR = "2023"
# End For YouTube

# For amazon
DEVICE_PROPERTY_AMAZON_DTID = "A3EYZI5I5ISCN5"

# For nrdp6
DISTRO_FEATURES:append = " tv-fhd-model"
