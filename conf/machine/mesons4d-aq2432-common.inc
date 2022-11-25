#@TYPE: Machine
#@NAME: meson

#@DESCRIPTION: Machine configuration for meson systems

include conf/machine/include/mesons4_k5.4_32b.inc
include conf/machine/mesons4-aq222-westeros.inc

CHIPSET_NAME = "S805C3"
UBOOT_MACHINE = "s4_aq2432_config"
#Default is EMMC
KERNEL_DEVICETREE = "s4d_s805c3_aq2432_linux_512m.dtb"

KERNEL_MODULE_AUTOLOAD_remove = " cxd2856_fe_64 r836_fe_64 av2018_fe_64 "
KERNEL_MODULE_AUTOLOAD_remove = " hdr10_tmo_alg_64"
KERNEL_MODULE_AUTOLOAD += " cxd2856_fe_32 r836_fe_32 av2018_fe_32 "
KERNEL_MODULE_AUTOLOAD += " hdr10_tmo_alg_32"

MACHINE_EXTRA_RRECOMMENDS_remove = " wififw-qca6174 qca6174"
KERNEL_MODULE_AUTOLOAD_remove = " btqca "
KERNEL_MODULE_PROBECONF_remove = " btqca "
KERNEL_MODULE_AUTOLOAD_remove = " hci_uart"
KERNEL_MODULE_PROBECONF_remove = "hci_uart"

MACHINE_EXTRA_RRECOMMENDS_append = " aml-wifi-w1"

DISTRO_FEATURES_remove = " bt-qca "
DISTRO_FEATURES_append = " aml-w1 "
DISTRO_FEATURES_remove = " netflix netflix6 amazon-plugin tts mediaplayer playready "
DISTRO_FEATURES_remove = " bluetooth bluez5 "

DISTRO_FEATURES_append = " UI_720P WesterOS_lowmem"
DISTRO_FEATURES_append = " appmanager AsperitasDvb LauncherWebkit aamp"
DISTRO_FEATURES_append = " fota-upgrade"
DISTRO_FEATURES_append = " remove-test-files"

DISTRO_FEATURES_append = " dm-verity"
DISTRO_FEATURES_NATIVE_append = " dm-verity"
#READONLY = "y"

RDEPENDS_packagegroup-amlogic-baserootfs_remove = "libhtmllocal htmllocal-plugin"

OVERRIDES .= ":aq2432"
TDK_VERSION_s4 = "v3.8/dev/S805C3"

# For Fota upgrade
DEVICE_PROPERTY_DEVINFO_OEM = "Amlogic"
DEVICE_PROPERTY_DEVINFO_DEVICETYPE = "smarthome"
DEVICE_PROPERTY_DEVINFO_PLATFORM = "A113D"
DEVICE_PROPERTY_DEVINFO_MODELS = "OTT"
DEVICE_PROPERTY_DEVINFO_PRODUCTID = "1660115992"
DEVICE_PROPERTY_DEVINFO_PRODUCTSECRET = "fa7bfc8d755c4c0b8c52fdb00d431d3f"
# End For Fota upgrade

# For YouTube Cert. Now it's for 2023
DEVICE_PROPERTY_FIRMWARE_VERSION = "1.0.0"
DEVICE_PROPERTY_MODEL_NAME = "ZapperPlus"
DEVICE_PROPERTY_OPERATOR_NAME = "SDKRelease"
DEVICE_PROPERTY_CERT_SCOPE = "amlogic-2023-amlogicstbzapperplus2023"
DEVICE_PROPERTY_DIALSERVER_NAME="ZapperPlus"
# YouTube define device type
# BDP:   Blu-Ray Player
# GAME:  Game Console
# OTT:   Over-The-Top Device
# STB:   Set-Top-Box, which includes a tuner and demodulator
# TV:    Television Set
DEVICE_PROPERTY_YOUTUBE_DEVICE_TYPE = "STB"
DEVICE_PROPERTY_CHIPSET_MODEL_NUM = "S805C3"
DEVICE_PROPERTY_MODEL_YEAR = "2023"
DEVICE_PROPERTY_COBALT_FORCE_SUPPORT_4K = "n"
DEVICE_PROPERTY_COBALT_MICROPHONE = "n"
DEVICE_PROPERTY_COBALT_TOTAL_CPU_MEMORY = "0xAA00000"
DEVICE_PROPERTY_COBALT_GST_APP_SRC_VIDEO_SIZE = "0x400000"
DEVICE_PROPERTY_COBALT_GST_APP_SRC_AUDIO_SIZE = "0x100000"
# End For YouTube
