
# common configuration
require conf/machine/include/amlogic_common.inc
require conf/machine/include/amlogic_64b.inc
require conf/machine/include/amlogic_k5.15.inc

PREFERRED_VERSION_u-boot = "v2023.%"
PREFERRED_PROVIDER_virtual/gpu ?= "vale"
PREFERRED_PROVIDER_virtual/lib32-gpu ?= "vale"
OVERRIDES .= ":s7d"
KERNEL_MODULE_AUTOLOAD += " cxd2856_fe_64 r836_fe_64 av2018_fe_64 "

