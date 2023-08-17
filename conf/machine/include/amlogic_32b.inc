DEFAULTTUNE = "armv7athf-neon"

#PREFERRED_PROVIDER_virtual/closedcaption-hal = "closedcaption-hal-amlogic"
PREFERRED_PROVIDER_virtual/gst-plugins-playersinkbin = "gst-plugins-playersinkbin-rpi"
#PREFERRED_PROVIDER_virtual/iarmmgrs-hal = "iarmmgrs-hal-amlogic"
#PREFERRED_PROVIDER_virtual/mfrlib = "mfrlib-amlogic"
#DISTRO_FEATURES:remove += "opengl gl"
DISTRO_FEATURES:remove += "parodus 3g zeroconf"
VIRTUAL-RUNTIME_alsa-state ?= "alsa-states"

# append drm(wayland drm backend)
DISTRO_FEATURES:append = " drm gles2"
#DISTRO_FEATURES:append = " wpeframework"
DISTRO_FEATURES:append = " clearkey"
#DISTRO_FEATURES:remove = " rdkbrowser2"
#WPELauncher is not used any more.WPEFramework now as a launcher
#DISTRO_FEATURES:append = " wpelauncher"
DISTRO_FEATURES:append = " wifi"
DISTRO_FEATURES:append = " systemd-disable-getty"
DISTRO_FEATURES:append = " dunfell"
MACHINEOVERRIDES .= ":wpe:thunder"
DISTROOVERRIDES .= ":dunfell"
#PREFERRED_VERSION_libopus = "1.1"

include conf/include/bbmasks-amlogic-platform.inc
PREFERRED_VERSION_cmake-native= "3.16.5"
PREFERRED_VERSION_youtube= "23%"