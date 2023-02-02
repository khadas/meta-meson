#@TYPE: Machine
#@NAME: meson

#@DESCRIPTION: Machine configuration for meson systems

include conf/machine/include/mesons4_k5.4_32b.inc

include conf/machine/mesons4-ap222-common.inc

KERNEL_MODULE_AUTOLOAD:remove = " cxd2856_fe_64 r836_fe_64 av2018_fe_64 "
KERNEL_MODULE_AUTOLOAD:remove = " hdr10_tmo_alg_64"
KERNEL_MODULE_AUTOLOAD += " cxd2856_fe_32 r836_fe_32 av2018_fe_32 "
KERNEL_MODULE_AUTOLOAD += " hdr10_tmo_alg_32"


