
include conf/machine/include/amlogic_westeros.inc

PREFERRED_PROVIDER_virtual/mesa = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/mesa-gl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgbm = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/egl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgles1 = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgles2 = "libgles-eabihf-dvalin-wayland-drm"

MACHINE_EXTRA_RRECOMMENDS += " \
        rtk8822cu \
        realtek-btusb \
        realtek-btuart \
        realtek-hciattach \
        pq-prebuilt \
        tuner-prebuilt \
        aucpu-fw \
        gpu \
        "

CHIPSET_NAME = "T962D4"
KERNEL_DEVICETREE = "t5w_t962d4_at301_linux.dtb t5w_t962d4_at301_linux_1g.dtb t5w_t962d4_at301_linux_1.5g.dtb"
UBOOT_MACHINE = "t5w_at301_v1"
KERNEL_MODULE_AUTOLOAD += " snd-soc-dummy_codec"
KERNEL_MODULE_AUTOLOAD += " 88x2cu"
KERNEL_MODULE_AUTOLOAD += " rtk_btuart"
KERNEL_MODULE_AUTOLOAD += " rtk_btusb"
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
#DISTRO_FEATURES:append = " absystem"
DISTRO_FEATURES:append = " widevine "
DISTRO_FEATURES:append = " playready "
DISTRO_FEATURES:append = " amlogic-tv "
DISTRO_FEATURES:append = " amlogic-dvb "
DISTRO_FEATURES:append = " dtvkit"
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
DISTRO_FEATURES:append = " AVB AVB_recovery_partition "
DEVICE_PROPERTY_BOOT_PARTITION_SIZE = "0x4000000"
DEVICE_PROPERTY_VBMETA_ROLLBACK_INDEX = "0"
DEVICE_PROPERTY_RECOVERY_PARTITION_SIZE = "0x2000000"
DEVICE_PROPERTY_RECOVERY_ROLLBACK_INDEX = "0"
DEVICE_PROPERTY_RECOVERY_ROLLBACK_LOCATION = "31"


OVERRIDES .= ":at301:t5w:onepass"
MACHINEOVERRIDES .= ":client"

DISTRO_FEATURES:append = " bluetooth bluez5 "
EXTRA_BLUETOOTH_STUFF := "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', ' bluez5', ' bluez4', d), '', d)}"
DISTRO_FEATURES:append = "${EXTRA_BLUETOOTH_STUFF}"

# For YouTube Cert. Now it's for 2024
DEVICE_PROPERTY_FIRMWARE_VERSION = "1.0.0"
DEVICE_PROPERTY_MODEL_NAME = "TV-4K-Ref"
DEVICE_PROPERTY_OPERATOR_NAME = "SDKRelease"
DEVICE_PROPERTY_YOUTUBE_CERT_SCOPE = "amlogic-2024-amlogictv4kref2024v2"
DEVICE_PROPERTY_PERSIST_DIALSERVER_NAME="AmlogicTV-AT301"
# YouTube define device type
# BDP:   Blu-Ray Player
# GAME:  Game Console
# OTT:   Over-The-Top Device
# STB:   Set-Top-Box, which includes a tuner and demodulator
# TV:    Television Set
DEVICE_PROPERTY_DEVICE_TYPE = "TV"
DEVICE_PROPERTY_CHIPSET = "T962D4"
DEVICE_PROPERTY_MODEL_YEAR = "2024"
DEVICE_PROPERTY_TV_PANEL_SIZE = "43"
# End For YouTube

