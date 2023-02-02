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
        pq-prebuilt \
        tuner-prebuilt \
        aucpu-fw \
        gpu \
        optee-linux-driver \
        "

CHIPSET_NAME = "S905X4"
KERNEL_DEVICETREE = "sc2_s905x4_ah212_linux.dtb"
UBOOT_MACHINE = "sc2_ah212_config"
KERNEL_MODULE_AUTOLOAD += " cxd2856_fe_64"
KERNEL_MODULE_AUTOLOAD += " hdr10_tmo_alg_64"
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
#DISTRO_FEATURES_append = " absystem"
DISTRO_FEATURES_append = " widevine "
DISTRO_FEATURES_append = " playready "
#DISTRO_FEATURES_append = " amlogic-tv "
DISTRO_FEATURES_append = " amlogic-dvb "
DISTRO_FEATURES_append = " dtvkit "
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
DISTRO_FEATURES_append = " aml-libjpeg "
DISTRO_FEATURES_append = " aml-libvpcodec "
DISTRO_FEATURES_append = " aml-libvphevcodec "
DISTRO_FEATURES_append = " gst-plugin-venc "

OVERRIDES .= ":ah212:sc2:onepass"
MACHINEOVERRIDES .= ":client"

DISTRO_FEATURES_append = " bt-qca "
DISTRO_FEATURES_append = " bluetooth bluez5 "
EXTRA_BLUETOOTH_STUFF := "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', ' bluez5', ' bluez4', d), '', d)}"
DISTRO_FEATURES_append = "${EXTRA_BLUETOOTH_STUFF}"

# For YouTube Cert. Now it's for 2023
DEVICE_PROPERTY_FIRMWARE_VERSION = "1.0.0"
DEVICE_PROPERTY_MODEL_NAME = "OTT-4K-Ref"
DEVICE_PROPERTY_OPERATOR_NAME = "SDKRelease"
DEVICE_PROPERTY_CERT_SCOPE = "amlogic-2023-amlogicottref4k2023"
DEVICE_PROPERTY_DIALSERVER_NAME="AmlogicOTT-AH212"
# YouTube define device type
# BDP:   Blu-Ray Player
# GAME:  Game Console
# OTT:   Over-The-Top Device
# STB:   Set-Top-Box, which includes a tuner and demodulator
# TV:    Television Set
DEVICE_PROPERTY_YOUTUBE_DEVICE_TYPE = "OTT"
DEVICE_PROPERTY_CHIPSET_MODEL_NUM = "S905X4"
DEVICE_PROPERTY_MODEL_YEAR = "2023"
# End For YouTube

# For amazon
DEVICE_PROPERTY_AMAZON_DTID = "A3SJQCMWSDF1EG"

## AVB2 with chained partition support
#DISTRO_FEATURES_append = " AVB "
#DEVICE_PROPERTY_BOOT_PARTITION_SIZE = "0x4000000"
#DEVICE_PROPERTY_VBMETA_ROLLBACK_INDEX = "0"
# For AVB2 chained partition,
# The following informations are needed,
# 1. The rollback index location assigned to each chained partition
# 2. The exact size of the partition
# This is due to that signing and root hash information
# are stored in the AVB2 footer of the partition
# Libavb in U-Boot will look at the end of the partition to look for footer.
#DISTRO_FEATURES_append = " AVB_chained_partition "
#DEVICE_PROPERTY_SYSTEM_ROLLBACK_LOCATION = "1"
#DEVICE_PROPERTY_VENDOR_ROLLBACK_LOCATION = "2"
#DEVICE_PROPERTY_SYSTEM_PARTITION_SIZE = "0x60000000"
#DEVICE_PROPERTY_VENDOR_PARTITION_SIZE = "0x1C000000"
#DISTRO_FEATURES_append = " recovery AVB_recovery_partition "
#DEVICE_PROPERTY_RECOVERY_ROLLBACK_LOCATION = "3"
#DEVICE_PROPERTY_RECOVERY_PARTITION_SIZE = "0x2000000"
# Rollback indexes for system and vendor are supported only if chained
#DEVICE_PROPERTY_SYSTEM_ROLLBACK_INDEX = "0"
#DEVICE_PROPERTY_VENDOR_ROLLBACK_INDEX = "0"
# For recovery, it is always chained if enabled.
#DEVICE_PROPERTY_RECOVERY_ROLLBACK_INDEX = "0"