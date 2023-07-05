#For Weston
DISTRO_FEATURES:append = " weston wayland weston-init"
PREFERRED_VERSION_weston = "10.0.0"
PREFERRED_VERSION_wayland = "1.19.0"
PREFERRED_VERSION_wayland-protocol = "1.24.0"
DISTRO_FEATURES:remove = " mesa mesa-gl x11"

DISTRO_FEATURES:remove = " amazon youtube netflix"

#Remove thunder framework related
RDEPENDS:packagegroup-amlogic-baserootfs:remove = "bluetooth-mgr bluetooth-core"
