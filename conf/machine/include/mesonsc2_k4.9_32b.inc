# common configuration
require conf/machine/include/amlogic_common.inc
require conf/machine/include/amlogic_32b.inc

PREFERRED_VERSION_u-boot = "v2019.%"
PREFERRED_VERSION_linux-meson = "4.9.%"
#PREFERRED_PROVIDER_virtual/gpu ?= "dvalin"
OVERRIDES .= ":sc2"