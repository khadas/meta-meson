#For Weston
DISTRO_FEATURES_append = " weston wayland weston-init"
PREFERRED_VERSION_weston = "8.0.0"
PREFERRED_VERSION_wayland = "1.17.0"
PREFERRED_VERSION_wayland-protocol = "1.18.0"
DISTRO_FEATURES_remove = " mesa mesa-gl x11"

DISTRO_FEATURES_remove = " amazon youtube netflix netflix6"

#Remove netflix related
RDEPENDS_packagegroup-amlogic-baserootfs_remove = "netflix-aml wpeframework-plugin-netflix"
#Remove thunder framework related
RDEPENDS_packagegroup-amlogic-baserootfs_remove = "bluetooth-mgr bluetooth-core"
