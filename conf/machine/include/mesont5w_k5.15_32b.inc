# common configuration
require conf/machine/include/amlogic_common.inc
require conf/machine/include/amlogic_32b.inc
require conf/machine/include/amlogic_k5.15.inc

PREFERRED_PROVIDER_virtual/gpu ?= "dvalin"
PREFERRED_VERSION_u-boot = "v2015.%"
OVERRIDES .= ":t5w:kernel32"
