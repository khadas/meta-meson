
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

CHIPSET_NAME = "S805X2"
KERNEL_DEVICETREE = "s4_s805x2_aq222_linux_1g.dtb"
UBOOT_MACHINE = "s4_aq222_config"
KERNEL_MODULE_AUTOLOAD += " cxd2856_fe_64 r836_fe_64 av2018_fe_64 "
KERNEL_MODULE_AUTOLOAD += " hdr10_tmo_alg_64"
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

DISTRO_FEATURES:append = " drm"
#DISTRO_FEATURES:append = " onepass"
DISTRO_FEATURES:append = " secure-u-boot optee "
DISTRO_FEATURES:append = " widevine "
DISTRO_FEATURES:append = " playready "
DISTRO_FEATURES:append = " wfd-hdcp "
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
#DISTRO_FEATURES:append = " AVB "
DISTRO_FEATURES:append = " recovery "
DISTRO_FEATURES:append = " swupdate "

OVERRIDES .= ":aq222:s4:onepass"
MACHINEOVERRIDES .= ":client"

DISTRO_FEATURES:append = " bt-qca "
DISTRO_FEATURES:append = " bluetooth bluez5 "
EXTRA_BLUETOOTH_STUFF := "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', ' bluez5', ' bluez4', d), '', d)}"
DISTRO_FEATURES:append = "${EXTRA_BLUETOOTH_STUFF}"

TDK_VERSION:s4 = "v3.8/dev/S805X2"

# For YouTube Cert. Now it's for 2023
DEVICE_PROPERTY_FIRMWARE_VERSION = "1.0.0"
DEVICE_PROPERTY_MODEL_NAME = "NA"
DEVICE_PROPERTY_OPERATOR_NAME = "SDKRelease"
DEVICE_PROPERTY_YOUTUBE_CERT_SCOPE = "NA"
DEVICE_PROPERTY_PERSIST_DIALSERVER_NAME="AmlogicOTT-AP222"
# YouTube define device type
# BDP:   Blu-Ray Player
# GAME:  Game Console
# OTT:   Over-The-Top Device
# STB:   Set-Top-Box, which includes a tuner and demodulator
# TV:    Television Set
DEVICE_PROPERTY_DEVICE_TYPE = "OTT"
DEVICE_PROPERTY_CHIPSET = "S905Y4"
DEVICE_PROPERTY_MODEL_YEAR = "2023"
DEVICE_PROPERTY_PERSIST_YOUTUBE_FORCE_4K = "n"
# End For YouTube

# For amazon
DEVICE_PROPERTY_AMAZON_DTID = "A3SJQCMWSDF1EG"
