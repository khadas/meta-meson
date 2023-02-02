#For No GPU
DISTRO_FEATURES:remove = " mesa mesa-gl x11"

#Remove thunder framework related
RDEPENDS:packagegroup-amlogic-baserootfs:remove = " "
