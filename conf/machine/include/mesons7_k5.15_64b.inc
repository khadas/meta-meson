
# common configuration
require conf/machine/include/amlogic_common.inc
require conf/machine/include/amlogic_64b.inc
require conf/machine/include/amlogic_k5.15.inc

PREFERRED_VERSION_u-boot = "v2023.%"
PREFERRED_PROVIDER_gpu ?= "dvalin"
OVERRIDES .= ":s7"
