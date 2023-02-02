include conf/machine/mesonsc2-ah232-common.inc

CHIPSET_NAME = "S905C2L"
KERNEL_DEVICETREE = "sc2_s905c2l_ah221_linux.dtb"
UBOOT_MACHINE = "sc2_ah221_config"

OVERRIDES .= ":ah221:sc2:onepass"
TDK_VERSION_sc2 = "v3.8/dev/S905C2L"

DISTRO_FEATURES_append = " nagra "
DISTRO_FEATURES_append = " aml-cas "
