# common configuration
require conf/machine/include/amlogic_common.inc
require conf/machine/include/amlogic_64b.inc

PREFERRED_VERSION_u-boot = "v2015.%"
PREFERRED_VERSION_linux-meson = "5.4%"
PREFERRED_VERSION_media-modules = "5.4%"
PREFERRED_PROVIDER_gpu ?= "dvalin"
OVERRIDES .= ":tm2"
