#DEFAULTTUNE = "armv7athf-neon"
#PREFERRED_PROVIDER_virtual/westeros-soc = "westeros-soc-drm"

#PREFERRED_PROVIDER_virtual/devicesettings-hal = "devicesettings-hal-amlogic"
#PREFERRED_PROVIDER_virtual/closedcaption-hal = "closedcaption-hal-amlogic"
PREFERRED_PROVIDER_virtual/gst-plugins-playersinkbin = "gst-plugins-playersinkbin-rpi"
#PREFERRED_PROVIDER_virtual/iarmmgrs-hal = "iarmmgrs-hal-amlogic"
#PREFERRED_PROVIDER_virtual/mfrlib = "mfrlib-amlogic"
#DISTRO_FEATURES_remove += "opengl gl"
DISTRO_FEATURES_remove += "parodus"
VIRTUAL-RUNTIME_alsa-state ?= "alsa-states"

# append drm(wayland drm backend)
DISTRO_FEATURES_append = " drm gles2"
#DISTRO_FEATURES_append = " wpeframework"
DISTRO_FEATURES_append = " opencdm"
DISTRO_FEATURES_append = " clearkey"
#DISTRO_FEATURES_remove = " rdkbrowser2"
#WPELauncher is not used any more.WPEFramework now as a launcher
#DISTRO_FEATURES_append = " wpelauncher"
DISTRO_FEATURES_append = " wifi"
DISTRO_FEATURES_append = " systemd-disable-getty"
DISTRO_FEATURES_append = " dunfell"
DISTROOVERRIDES .= ":dunfell"
#PREFERRED_VERSION_libopus = "1.1"
#PREFERRED_PROVIDER_faad2 = "libplayer"

include conf/include/bbmasks-amlogic-platform.inc
PREFERRED_VERSION_cmake-native= "3.8.2"

#DISTRO_FEATURES_append = " amazon-plugin-prebuilt amazon-plugin"

DISTRO_FEATURES_append = " amazon-plugin"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'amazon-plugin', '','|meta-rdk-amazon|amazon-prime-.*', d)}"
