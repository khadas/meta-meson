#For Raw System
DISTRO_FEATURES_remove = " mesa mesa-gl x11"
DISTRO_FEATURES_remove = " amazon-plugin cobalt netflix netflix6"

#Remove netflix related
RDEPENDS_packagegroup-amlogic-baserootfs_remove = "netflix-aml wpeframework-plugin-netflix"
#Remove thunder framework related
RDEPENDS_packagegroup-amlogic-baserootfs_remove = "bluetooth-mgr bluetooth-core"
