
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
        optee-linux-driver \
        "

KERNEL_DEVICETREE = "s4_s905y4_ap222_linux.dtb s4d_s905y4_ap222_linux.dtb"
UBOOT_MACHINE = "s4_ap222_config"
KERNEL_MODULE_AUTOLOAD += " cxd2856_fe_64 r836_fe_64 av2018_fe_64 "
KERNEL_MODULE_AUTOLOAD += " snd-soc-dummy_codec"
KERNEL_MODULE_AUTOLOAD += " snd-soc-aml_t9015"
KERNEL_MODULE_AUTOLOAD += " snd_soc"
KERNEL_MODULE_AUTOLOAD += " snd_soc_notify"
KERNEL_MODULE_AUTOLOAD += " meson_ir"
KERNEL_MODULE_AUTOLOAD += " btqca"
KERNEL_MODULE_PROBECONF += "btqca"
KERNEL_MODULE_AUTOLOAD += " hci_uart"
KERNEL_MODULE_PROBECONF += "hci_uart"
KERNEL_MODULE_AUTOLOAD += "aml_aucpu"
KERNEL_MODULE_AUTOLOAD += "dolby_fw"
KERNEL_MODULE_AUTOLOAD += "meson-cpufreq"
KERNEL_MODULE_AUTOLOAD += " media_sync"

DISTRO_FEATURES_append = " drm"
#DISTRO_FEATURES_append = " onepass"
DISTRO_FEATURES_append = " secure-u-boot optee "
DISTRO_FEATURES_append = " widevine "
DISTRO_FEATURES_append = " playready "
DISTRO_FEATURES_append = " wfd-hdcp "
DISTRO_FEATURES_append = " aml-iptv "
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
DISTRO_FEATURES_append = " aml-cas "
#DISTRO_FEATURES_append = " AVB "
DISTRO_FEATURES_append = " recovery "
DISTRO_FEATURES_append = " swupdate "

# enable selinux
DISTRO_FEATURES_append = " acl xattr pam selinux"
# set the policy
PREFERRED_PROVIDER_virtual/refpolicy ?= "refpolicy-standard"
DEFAULT_ENFORCING = "permissive"

OVERRIDES .= ":ap222:s4:onepass"
MACHINEOVERRIDES .= ":client"

DISTRO_FEATURES_append = " bluetooth bluez5 "
EXTRA_BLUETOOTH_STUFF := "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', ' bluez5', ' bluez4', d), '', d)}"
DISTRO_FEATURES_append = "${EXTRA_BLUETOOTH_STUFF}"

# dm-verity
#DISTRO_FEATURES_append = " dm-verity"
#DISTRO_FEATURES_NATIVE_append = " dm-verity"

# For YouTube Cert. Now it's for 2022
DEVICE_PROPERTY_FIRMWARE_VERSION = "1.0.0"
DEVICE_PROPERTY_MODEL_NAME = "OTT-4K-Ref"
DEVICE_PROPERTY_OPERATOR_NAME = "SDKRelease"
DEVICE_PROPERTY_CERT_SCOPE = "amlogic-2022-amlogicottref4k2022"
DEVICE_PROPERTY_DIALSERVER_NAME="AmlogicOTT-AP222"
# YouTube define device type
# BDP:   Blu-Ray Player
# GAME:  Game Console
# OTT:   Over-The-Top Device
# STB:   Set-Top-Box, which includes a tuner and demodulator
# TV:    Television Set
DEVICE_PROPERTY_YOUTUBE_DEVICE_TYPE = "OTT"
DEVICE_PROPERTY_CHIPSET_MODEL_NUM = "S905Y4"
DEVICE_PROPERTY_MODEL_YEAR = "2022"
# End For YouTube

# For amazon
DEVICE_PROPERTY_AMAZON_DTID = "A3SJQCMWSDF1EG"
