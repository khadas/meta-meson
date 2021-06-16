DEFAULTTUNE = "armv7athf-neon"

#DISTRO_FEATURES_remove += "opengl gl"
DISTRO_FEATURES_remove += "parodus"
VIRTUAL-RUNTIME_alsa-state ?= "alsa-states"

# append drm(wayland drm backend)
DISTRO_FEATURES_append = " drm gles2"
#DISTRO_FEATURES_append = " opencdm"
#DISTRO_FEATURES_append = " clearkey"
#DISTRO_FEATURES_append = " wifi"
DISTRO_FEATURES_append = " systemd-disable-getty"
DISTRO_FEATURES_append = " dunfell"

include conf/include/bbmasks-amlogic-platform.inc
#PREFERRED_VERSION_cmake-native= "3.8.2"
