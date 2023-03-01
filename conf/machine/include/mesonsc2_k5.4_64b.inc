# common configuration
require conf/machine/include/amlogic_common.inc
require conf/machine/include/amlogic_64b.inc
require conf/machine/include/amlogic_k5.4.inc

PREFERRED_VERSION_u-boot = "v2019.%"
PREFERRED_PROVIDER_gpu ?= "dvalin"
OVERRIDES .= ":sc2"
