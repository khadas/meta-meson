# common configuration
require conf/machine/include/amlogic_common.inc
require conf/machine/include/amlogic_32b.inc

PREFERRED_PROVIDER_virtual/gpu ?= "dvalin"
PREFERRED_VERSION_u-boot = "m805%"
PREFERRED_VERSION_linux-meson = "4.9.%"
OVERRIDES .= ":a213y"
