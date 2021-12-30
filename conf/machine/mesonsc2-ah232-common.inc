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

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += " \
        wififw-qca6174 \
        qca6174 \
        tuner-prebuilt \
        aucpu-fw \
        gpu \
        optee-linux-driver \
        "

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

DISTRO_FEATURES_append = " drm"
#DISTRO_FEATURES_append = " onepass"
DISTRO_FEATURES_append = " secure-u-boot optee "
DISTRO_FEATURES_append = " widevine "
DISTRO_FEATURES_append = " playready "
#DISTRO_FEATURES_append = " amlogic-tv "
DISTRO_FEATURES_append = " amlogic-dvb "
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

OVERRIDES .= ":ah212:ah232:sc2:onepass"
MACHINEOVERRIDES .= ":client"
TDK_VERSION_sc2 = "v3.8/dev/S905C2"

DISTRO_FEATURES_append = " bluetooth bluez5 "
EXTRA_BLUETOOTH_STUFF := "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', ' bluez5', ' bluez4', d), '', d)}"
DISTRO_FEATURES_append = "${EXTRA_BLUETOOTH_STUFF}"

# For YouTube Cert. Now it's for 2022
DEVICE_PROPERTY_FIRMWARE_VERSION = "1.0.0"
DEVICE_PROPERTY_MODEL_NAME = "OTT-4K-Ref"
DEVICE_PROPERTY_OPERATOR_NAME = "SDKRelease"
DEVICE_PROPERTY_CERT_SCOPE = "amlogic-2022-amlogicottref4k2022"
DEVICE_PROPERTY_DIALSERVER_NAME="AmlogicOTT-AH212"
# YouTube define device type
# BDP:   Blu-Ray Player
# GAME:  Game Console
# OTT:   Over-The-Top Device
# STB:   Set-Top-Box, which includes a tuner and demodulator
# TV:    Television Set
DEVICE_PROPERTY_YOUTUBE_DEVICE_TYPE = "OTT"
DEVICE_PROPERTY_CHIPSET_MODEL_NUM = "S905X4"
DEVICE_PROPERTY_MODEL_YEAR = "2022"
# End For YouTube