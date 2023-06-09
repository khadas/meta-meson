#For No GPU
DISTRO_FEATURES:remove = " mesa mesa-gl x11"
DISTRO_FEATURES:append = " no-gpu"

#Remove thunder framework related
RDEPENDS:packagegroup-amlogic-baserootfs:remove = " "
