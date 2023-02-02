#For Raw System
DISTRO_FEATURES:remove = " mesa mesa-gl x11"
DISTRO_FEATURES:remove = " amazon youtube netflix netflix6"

#Remove netflix related
RDEPENDS:packagegroup-amlogic-baserootfs:remove = "netflix-aml wpeframework-plugin-netflix"
#Remove thunder framework related
RDEPENDS:packagegroup-amlogic-baserootfs:remove = "bluetooth-mgr bluetooth-core"
