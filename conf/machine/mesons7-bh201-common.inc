#@TYPE: Machine
#@NAME: meson

#@DESCRIPTION: Machine configuration for meson systems

include conf/machine/include/amlogic_westeros.inc

PREFERRED_PROVIDER_virtual/mesa = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/mesa-gl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgbm = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/egl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgl = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgles1 = "libgles-eabihf-dvalin-wayland-drm"
PREFERRED_PROVIDER_virtual/libgles2 = "libgles-eabihf-dvalin-wayland-drm"

MACHINE_EXTRA_RRECOMMENDS += " \
        aucpu-fw \
        gpu \
        "

CHIPSET_NAME = "S905Y5"
MACHINE_EXTRA_RRECOMMENDS:append = " aml-wifi-w2 "
DISTRO_FEATURES:append = " aml-w2 aml-wifi "
KERNEL_DEVICETREE = "s7_s905y5_bh201_linux.dtb"
UBOOT_MACHINE = "s7_bh201_config"
KERNEL_MODULE_AUTOLOAD += " rfkill amlogic-wireless "
#KERNEL_MODULE_AUTOLOAD += " w2 w2-common "
KERNEL_MODULE_AUTOLOAD += " cxd2856_fe_64"
KERNEL_MODULE_AUTOLOAD += " hdr10_tmo_alg_64"
KERNEL_MODULE_AUTOLOAD += " snd-soc-dummy_codec"
KERNEL_MODULE_AUTOLOAD += " snd-soc-aml_t9015"
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
DISTRO_FEATURES:append = " miraclecast "
DISTRO_FEATURES:append = " recovery "
DISTRO_FEATURES:append = " swupdate "
DISTRO_FEATURES:append = " aml-libjpeg "
DISTRO_FEATURES:append = " aml-libvpcodec "
DISTRO_FEATURES:append = " aml-libvphevcodec "
DISTRO_FEATURES:append = " gst-plugin-venc "

OVERRIDES .= ":bh201:s7:k5.15-u:onepass"
MACHINEOVERRIDES .= ":client"

DISTRO_FEATURES:append = " bluetooth bluez5 "
EXTRA_BLUETOOTH_STUFF := "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', ' bluez5', ' bluez4', d), '', d)}"
DISTRO_FEATURES:append = "${EXTRA_BLUETOOTH_STUFF}"

# For YouTube Cert. Now it's for 2024
DEVICE_PROPERTY_FIRMWARE_VERSION = "1.0.0"
DEVICE_PROPERTY_MODEL_NAME = "OTT-4K-Ref"
DEVICE_PROPERTY_OPERATOR_NAME = "SDKRelease"
DEVICE_PROPERTY_YOUTUBE_CERT_SCOPE = "amlogic-2024-amlogicott4kref2024v2"
DEVICE_PROPERTY_PERSIST_DIALSERVER_NAME="AmlogicOTT-AH212"
# YouTube define device type
# BDP:   Blu-Ray Player
# GAME:  Game Console
# OTT:   Over-The-Top Device
# STB:   Set-Top-Box, which includes a tuner and demodulator
# TV:    Television Set
DEVICE_PROPERTY_DEVICE_TYPE = "OTT"
DEVICE_PROPERTY_CHIPSET = "S905Y5"
DEVICE_PROPERTY_MODEL_YEAR = "2024"
# End For YouTube


## FBE
#DISTRO_FEATURES:append = " FBE "

## AVB2 with chained partition support
#DISTRO_FEATURES:append = " AVB "
DEVICE_PROPERTY_BOOT_PARTITION_SIZE = "0x4000000"
DEVICE_PROPERTY_VBMETA_ROLLBACK_INDEX = "0"
# For AVB2 chained partition,
# The following informations are needed,
# 1. The rollback index location assigned to each chained partition
# 2. The exact size of the partition
# This is due to that signing and root hash information
# are stored in the AVB2 footer of the partition
# Libavb in U-Boot will look at the end of the partition to look for footer.
#DISTRO_FEATURES:append = " AVB_chained_partition "
#DEVICE_PROPERTY_SYSTEM_ROLLBACK_LOCATION = "1"
#DEVICE_PROPERTY_VENDOR_ROLLBACK_LOCATION = "2"
#DEVICE_PROPERTY_SYSTEM_PARTITION_SIZE = "0x60000000"
#DEVICE_PROPERTY_VENDOR_PARTITION_SIZE = "0x1C000000"
#DISTRO_FEATURES:append = " recovery AVB_recovery_partition "
DEVICE_PROPERTY_RECOVERY_ROLLBACK_LOCATION = "31"
DEVICE_PROPERTY_RECOVERY_PARTITION_SIZE = "0x2000000"
# Rollback indexes for system and vendor are supported only if chained
#DEVICE_PROPERTY_SYSTEM_ROLLBACK_INDEX = "0"
#DEVICE_PROPERTY_VENDOR_ROLLBACK_INDEX = "0"
# For recovery, it is always chained if enabled.
DEVICE_PROPERTY_RECOVERY_ROLLBACK_INDEX = "0"

# Misc
DEVICE_PROPERTY_PERSIST_PERSIST_VDEC_FORCE_DI_NR = "1"
