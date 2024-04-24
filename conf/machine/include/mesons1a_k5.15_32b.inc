# common configuration
require conf/machine/include/amlogic_common.inc
require conf/machine/include/amlogic_32b.inc
require conf/machine/include/amlogic_k5.15.inc

PREFERRED_VERSION_u-boot = "v2023.%"
#We can't select revsion in external source mode, but use MACHINE define to control it.
PREFERRED_PROVIDER_virtual/gpu ?= "dvalin"
OVERRIDES .= ":s1a:kernel32"
