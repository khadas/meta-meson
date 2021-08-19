
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
#DISTRO_FEATURES_append = " opencdm "
DISTRO_FEATURES_append = " widevine "
DISTRO_FEATURES_append = " playready "
DISTRO_FEATURES_append = " amlogic-tv "
DISTRO_FEATURES_append = " amlogic-dvb "
#DISTRO_FEATURES_remove = " alsa "
DISTRO_FEATURES_append = " alsa "
DISTRO_FEATURES_append = " widevine_hardware"
DISTRO_FEATURES_append = " pam"
VIRTUAL-RUNTIME_init_manager = "systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"
VIRTUAL-RUNTIME_initscripts = ""
DEFAULT_DISTRO_FEATURES += " systemd"
DISTRO_FEATURES_append = " systemd"
DISTRO_FEATURES_remove = " sysvinit"
#DISTRO_FEATURES_append = " opengl"
DISTRO_FEATURES_append = " gstreamer1"
DISTRO_FEATURES_append = " aml-dtv "

#For Weston
DISTRO_FEATURES_append = " weston wayland weston-init"
DISTRO_FEATURES_remove = " mesa mesa-gl x11"
#DISTRO_FEATURES_remove = " cairo"
#DISTRO_FEATURES_NATIVESDK_remove = "x11"

PREFERRED_VERSION_weston = "8.0.0"
PREFERRED_VERSION_wayland = "1.17.0"
PREFERRED_VERSION_wayland-protocol = "1.18.0"

#PREFERRED_VERSION_mesa="19.1.1"
#PREFERRED_VERSION_mesa-gl="19.1.1"

PREFERRED_VERSION_tinyalsa = "1.1.1"

OVERRIDES .= ":am301:t5d:onepass"
MACHINEOVERRIDES .= ":client"
PREFERRED_PROVIDER_widevine = "aml-mediadrm-widevine"

DISTRO_FEATURES_append = " bluetooth bluez5 "

EXTRA_BLUETOOTH_STUFF := "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', ' bluez5', ' bluez4', d), '', d)}"
DISTRO_FEATURES_append = "${EXTRA_BLUETOOTH_STUFF}"

VIRTUAL-RUNTIME_init_manager ?= "systemd"
VIRTUAL-RUNTIME_initscripts ?= "systemd-compat-units"

VOLATILE_BINDS = "/var/volatile/www /www\n"
VOLATILE_BINDS_append = "/var/volatile/resolv.conf /etc/resolv.conf\n"
VOLATILE_BINDS_append = "/var/volatile/asound.conf /etc/asound.conf\n"
VOLATILE_BINDS_append = "/var/volatile/resolv.dnsmasq /etc/resolv.dnsmasq\n"
VOLATILE_BINDS_append = "/var/volatile/hosts /etc/hosts\n"
VOLATILE_BINDS_append = "/var/volatile/hostname /etc/hostname\n"
VOLATILE_BINDS_append = "/var/volatile/dhcp_static_hosts /etc/dhcp_static_hosts\n"
VOLATILE_BINDS_append = "/var/volatile/cron /var/spool/cron\n"
VOLATILE_BINDS_append_hybrid = "/tmp/snmpd.conf /etc/snmp/snmpd.conf\n"
VOLATILE_BINDS_append_client = "/tmp/timesyncd.conf /etc/systemd/timesyncd.conf\n"
VOLATILE_BINDS_append_hybrid = "/tmp/udhcpc.vendor_specific /etc/udhcpc.vendor_specific\n"
VOLATILE_BINDS_append = "/tmp/dibbler /etc/dibbler\n"
VOLATILE_BINDS_append = "/var/volatile/xupnp /etc/xupnp\n"
VOLATILE_BINDS_append = "/tmp/samhain /var/samhain\n"
XZ_COMPRESSION_LEVEL ?= "-e -M 50% -9"
#PREFERRED_PROVIDER_libgudev = "${@base_conditional('PREFERRED_VERSION_systemd', '1:216%', 'systemd', 'libgudev', d)}"

DISTRO_FEATURES_remove = "weston"
DISTRO_FEATURES_append = " westeros enable_icrypto_openssl rdkshell safec one-rdk no-rdk-deps aml-thunder cobalt aml-launcher"
DISTRO_FEATURES_append = " tts"

BBMASK += "meta-meson/recipes-multimedia/gst-aml-plugins/gst-plugin-aml-vsink_1.0.bb"
BBMASK += "meta-aml-netflix/recipes-graphics/rdkresidentapp/rdkresidentapp.bbappend"
RDEPENDS_packagegroup-amlogic-baserootfs_remove = "gst-plugin-aml-vsink"
WIDEVINE_VERSION = "16"