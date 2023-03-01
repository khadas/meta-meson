# common configuration
require conf/machine/include/amlogic_common.inc
require conf/machine/include/amlogic_32b.inc
require conf/machine/include/amlogic_k5.4.inc

PREFERRED_VERSION_u-boot = "v2019.%"
#We can't select revsion in external source mode, but use MACHINE define to control it.
PREFERRED_PROVIDER_gpu ?= "dvalin"
OVERRIDES .= ":s4:kernel32"
