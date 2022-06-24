#DEFAULTTUNE = "armv7athf-neon"

#PREFERRED_PROVIDER_virtual/closedcaption-hal = "closedcaption-hal-amlogic"
PREFERRED_PROVIDER_virtual/gst-plugins-playersinkbin = "gst-plugins-playersinkbin-rpi"
#PREFERRED_PROVIDER_virtual/iarmmgrs-hal = "iarmmgrs-hal-amlogic"
#PREFERRED_PROVIDER_virtual/mfrlib = "mfrlib-amlogic"
#DISTRO_FEATURES_remove += "opengl gl"
DISTRO_FEATURES_remove += "parodus 3g zeroconf"
VIRTUAL-RUNTIME_alsa-state ?= "alsa-states"

# append drm(wayland drm backend)
DISTRO_FEATURES_append = " drm gles2"
#DISTRO_FEATURES_append = " wpeframework"
DISTRO_FEATURES_append = " clearkey"
#DISTRO_FEATURES_remove = " rdkbrowser2"
#WPELauncher is not used any more.WPEFramework now as a launcher
#DISTRO_FEATURES_append = " wpelauncher"
DISTRO_FEATURES_append = " wifi"
DISTRO_FEATURES_append = " systemd-disable-getty"
DISTRO_FEATURES_append = " dunfell"
DISTROOVERRIDES .= ":dunfell"
#PREFERRED_VERSION_libopus = "1.1"

include conf/include/bbmasks-amlogic-platform.inc
PREFERRED_VERSION_cmake-native= "3.16.5"

