# common configuration
require conf/machine/include/amlogic_common.inc
require conf/machine/include/amlogic_64b.inc
require conf/machine/include/amlogic_k5.15.inc

PREFERRED_VERSION_u-boot = "v2019.%"
PREFERRED_PROVIDER_virtual/gpu ?= "valhall"
PREFERRED_PROVIDER_virtual/lib32-gpu ?= "valhall"
OVERRIDES .= ":s5"
