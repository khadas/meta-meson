#@TYPE: Machine
#@NAME: meson

#@DESCRIPTION: Machine configuration for meson systems

include conf/machine/include/mesons4_k5.4_32b.inc
include conf/machine/mesons4-aq222-westeros.inc

UBOOT_MACHINE = "s4_aq2432_config"
#Default is EMMC
KERNEL_DEVICETREE = "s4d_s805c3_aq2432_linux_512m.dtb"

KERNEL_MODULE_AUTOLOAD_remove = " cxd2856_fe_64 r836_fe_64 av2018_fe_64 "
KERNEL_MODULE_AUTOLOAD_remove = " hdr10_tmo_alg_64"
KERNEL_MODULE_AUTOLOAD += " cxd2856_fe_32 r836_fe_32 av2018_fe_32 "
KERNEL_MODULE_AUTOLOAD += " hdr10_tmo_alg_32"

MACHINE_EXTRA_RRECOMMENDS_remove = " wififw-qca6174 qca6174"
KERNEL_MODULE_AUTOLOAD_remove = " btqca "
KERNEL_MODULE_PROBECONF_remove = " btqca "

MACHINE_EXTRA_RRECOMMENDS_append = " aml-wifi-w1 aml-bt-w1"

DISTRO_FEATURES_remove = " bt-qca "
DISTRO_FEATURES_append = " aml-w1 "
DISTRO_FEATURES_remove = " netflix amazon-plugin tts mediaplayer "

DISTRO_FEATURES_append = " UI_720P WesterOS_lowmem"
DISTRO_FEATURES_append = " appmanager"
DISTRO_FEATURES_remove = " selinux"

OVERRIDES .= ":aq2432"
TDK_VERSION_s4 = "v3.8/dev/S805C3"
