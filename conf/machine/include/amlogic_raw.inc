#For Raw System
DISTRO_FEATURES:remove = " mesa mesa-gl x11"
DISTRO_FEATURES:remove = " amazon youtube netflix"

#Remove thunder framework related
RDEPENDS:packagegroup-amlogic-baserootfs:remove = "bluetooth-mgr bluetooth-core"
