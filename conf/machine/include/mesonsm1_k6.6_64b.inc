# common configuration
require conf/machine/include/amlogic_common.inc
require conf/machine/include/amlogic_64b.inc
require conf/machine/include/amlogic_k6.6.inc

PREFERRED_VERSION_u-boot = "v2015.%"
#We can't select revsion in external source mode, but use MACHINE define to control it.
PREFERRED_PROVIDER_virtual/gpu ?= "dvalin"
#OVERRIDES .= ":ac200:sm1:onepass"
