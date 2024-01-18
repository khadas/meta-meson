#@TYPE: Machine
#@NAME: meson

#@DESCRIPTION: Machine configuration for meson systems

include conf/machine/include/mesonsc2_k5.4_64b.inc
include conf/machine/include/amlogic_westeros.inc

PREFERRED_PROVIDER_virtual/mesa = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/mesa-gl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgbm = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/egl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgles1 = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgles2 = "libgles-eabihf-dvalin-wayland-drm"

MACHINE_EXTRA_RRECOMMENDS += " \
        wififw-qca6174 \
        qca6174 \
        tuner-prebuilt \
        aucpu-fw \
        gpu \
        "

CHIPSET_NAME = "S905C2"
KERNEL_DEVICETREE = "sc2_s905x4_ah212_linux.dtb"
UBOOT_MACHINE = "sc2_ah232_config"
KERNEL_MODULE_AUTOLOAD += " cxd2856_fe_64"
KERNEL_MODULE_AUTOLOAD += " snd-soc-dummy_codec"
KERNEL_MODULE_AUTOLOAD += " snd-soc-aml_t9015"
KERNEL_MODULE_AUTOLOAD += " meson_ir"
KERNEL_MODULE_AUTOLOAD += " btqca"
KERNEL_MODULE_PROBECONF += "btqca"
KERNEL_MODULE_AUTOLOAD += " hci_uart"
KERNEL_MODULE_PROBECONF += "hci_uart"
KERNEL_MODULE_AUTOLOAD += "aml_aucpu"
KERNEL_MODULE_AUTOLOAD += "dolby_fw"
KERNEL_MODULE_AUTOLOAD += "meson-cpufreq"

DISTRO_FEATURES:append = " drm"
#DISTRO_FEATURES:append = " onepass"
DISTRO_FEATURES:append = " secure-u-boot optee "
DISTRO_FEATURES:append = " widevine "
DISTRO_FEATURES:append = " playready "
DISTRO_FEATURES:append = " wlcdmi "
#DISTRO_FEATURES:append = " amlogic-tv "
DISTRO_FEATURES:append = " amlogic-dvb "
DISTRO_FEATURES:append = " dtvkit "
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

OVERRIDES .= ":ah212:ah232:sc2:onepass"
MACHINEOVERRIDES .= ":client"
TDK_VERSION:sc2 = "v3.8/dev/S905C2"

DISTRO_FEATURES:append = " bt-qca "
DISTRO_FEATURES:append = " bluetooth bluez5 "
EXTRA_BLUETOOTH_STUFF := "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', ' bluez5', ' bluez4', d), '', d)}"
DISTRO_FEATURES:append = "${EXTRA_BLUETOOTH_STUFF}"

# For YouTube Cert. Now it's for 2023
DEVICE_PROPERTY_FIRMWARE_VERSION = "1.0.0"
DEVICE_PROPERTY_MODEL_NAME = "OTT-4K-Ref"
DEVICE_PROPERTY_OPERATOR_NAME = "SDKRelease"
DEVICE_PROPERTY_YOUTUBE_CERT_SCOPE = "amlogic-2023-amlogicottref4k2023"
DEVICE_PROPERTY_PERSIST_DIALSERVER_NAME="AmlogicOTT-AH212"
# YouTube define device type
# BDP:   Blu-Ray Player
# GAME:  Game Console
# OTT:   Over-The-Top Device
# STB:   Set-Top-Box, which includes a tuner and demodulator
# TV:    Television Set
DEVICE_PROPERTY_DEVICE_TYPE = "OTT"
DEVICE_PROPERTY_CHIPSET = "S905X4"
DEVICE_PROPERTY_MODEL_YEAR = "2023"
# End For YouTube

