require conf/machine/include/arm/arch-armv8a.inc
include conf/machine/include/amlogic_externalsrc.inc
include conf/machine/include/amlogic_patches.inc
include conf/machine/include/device-properties.inc

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += " \
        media-modules "

RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""
MACHINE_EXTRA_RRECOMMENDS = " kernel-modules kernel-devicetree"
EXTRA_IMAGEDEPENDS += "u-boot"
IMAGE_FSTYPES += "tar.gz ext4"
ROOTFS_TYPE = "ext4"

IMAGE_GEN_DEBUGFS = "1"
IMAGE_FSTYPES_DEBUGFS = ""
SERIAL_CONSOLES = "921600;ttyS0"
SERIAL_CONSOLES:t5w = "115200;ttyS0"
SERIAL_CONSOLES:t3 = "115200;ttyS0"
SERIAL_CONSOLES:g12b = "115200;ttyS0"
SERIAL_CONSOLES:sm1 = "115200;ttyS0"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-meson"
KERNEL_IMAGETYPE:aarch64 = "Image.gz"
KERNEL_IMAGETYPE:armv7a = "uImage"
UBOOT_SUFFIX = "bin"
MACHINE_FEATURES = "usbgadget usbhost vfat "
IMAGE_BOOT_FILES ?= "u-boot.${UBOOT_SUFFIX}"
INITRAMFS_FSTYPES = "cpio.gz"

#KERNEL_MODULE_AUTOLOAD += "mali"
TDK_VERSION = "v3.8"
TDK_VERSION:t5d = "v2.4"
TDK_VERSION:sc2 = "v3.8/dev/S905X4"
TDK_VERSION:t7 = "v3.8/dev/A311D2"
TDK_VERSION:s4 = "v3.8/dev/S905Y4"
TDK_VERSION:s7 = "v3.8/dev/S905Y5"
TDK_VERSION:t3 = "v3.8/dev/T965D4"
TDK_VERSION:t5w = "v3.8/dev/T962D4"

PLATFORM_TDK_VERSION = "v3.8.0"
PLATFORM_TDK_VERSION:s7 = "v3.18.0"

#Widevine Version
WIDEVINE_VERSION = "16"

#Playready Version
PLAYREADY_VERSION = "4.4"

AML_GIT_ROOT ?= "git.myamlogic.com"
AML_GIT_PROTOCOL ?= "git"
AML_GIT_ROOT_YOCTO_SUFFIX ?= "/yocto"
AML_GIT_ROOT_PR ?= "git.myamlogic.com"
AML_GIT_ROOT_WV ?= "git.myamlogic.com"
AML_GIT_ROOT_PROTOCOL ?= "git"

#check connectivity using bing
#CONNECTIVITY_CHECK_URIS = "https://www.bing.com/"

PREFERRED_VERSION_libdrm = "2.4.100"
PREFERRED_VERSION_libdrm-native = "2.4.110"
PREFERRED_VERSION_tinyalsa = "1.1.1"

#Only support amlogic widevine
PREFERRED_PROVIDER_widevine = "aml-mediadrm-widevine"

PREFERRED_VERSION_libgles-eabihf-dvalin-wayland-drm = "r44p0"
PREFERRED_VERSION_libgles-eabihf-gondul-wayland-drm = "r44p0"

#for vulkan
PREFERRED_VERSION_vulkan-headers = "1.2.196"
PREFERRED_VERSION_vulkan-loader = "1.2.196"

#for cairo
PREFERRED_VERSION_cairo_dunfell = "1.14.6"

WARN_QA:append = "ldflags installed-vs-shipped already-stripped pkgvarcheck file-rdeps"
ERROR_QA:remove = "ldflags installed-vs-shipped already-stripped pkgvarcheck file-rdeps"

DEBUG_PREFIX_MAP = "-fdebug-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR} \
                    -fdebug-prefix-map=${STAGING_DIR_HOST}= \
                    -fdebug-prefix-map=${STAGING_DIR_NATIVE}= \
                    "

HOSTTOOLS_NONFATAL += "bc cc c++ bison repo python crc32"

DISABLE_STATIC:pn-libevent = ""

AML_RDK_PREBUILT_SITE ?= "http://10.18.11.6/shanghai/image/linux-distro/Public/YOCTO/prebuilt/"
AML_TOOLS_SITE ?= "http://10.18.11.120:8000/Tools"
# define sign tool
#AML_SCS_SIGN_TOOL = "${COREBASE}/../aml-comp/prebuilt/hosttools/aml-linux-scs/Aml_Linux_SCS_SignTool/amlogic_scs_sign_whole_pkg.bash"
# define config files device-keys and fw_arb.cfg
#AML_SCS_SIGN_CONFIG_PATH = "${COREBASE}/../aml-comp/prebuilt/hosttools/aml-linux-scs"
# define sign v3 tool
#AML_SB_SIGN_TOOL = "${COREBASE}/../aml-comp/prebuilt/hosttools/aml-linux-sb/Aml_Linux_SecureBootV3_SignTool/amlogic_secureboot_sign_whole_pkg.bash"
# define config files keys
#AML_SB_SIGN_CONFIG_PATH = "${COREBASE}/../aml-comp/prebuilt/hosttools/aml-linux-sb"

# For dm-verity
# System partition ( This should be overwrited by platform conf if the target is not amlogic-yocto )
SYSTEM_DM_VERITY_IMAGE ??= "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', \
                             bb.utils.contains('MULTILIBS', 'multilib:lib32', 'lib32-amlogic-yocto', 'amlogic-yocto', d), '', d)}"
SYSTEM_DM_VERITY_IMAGE_TYPE ??= "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', '${ROOTFS_TYPE}', '', d)}"
# vendor partition
VENDOR_DM_VERITY_IMAGE ??= "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', \
                             bb.utils.contains('DISTRO_FEATURES', 'vendor-partition', 'vendor-image', '' ,d), '', d)}"
VENDOR_DM_VERITY_IMAGE_TYPE ??= "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', '${ROOTFS_TYPE}', '', d)}"

XZ_COMPRESSION_LEVEL ?= "-e -M 50% -9"

VIRTUAL-RUNTIME_init_manager ?= "systemd"
VIRTUAL-RUNTIME_initscripts ?= "systemd-compat-units"

KERNEL_MODULE_AUTOLOAD += "gpio_keypad adc_keypad reg_access"
KERNEL_MODULE_AUTOLOAD += " ${@bb.utils.contains('DISTRO_FEATURES', 'aml-dtv', 'afd_module', '', d)}"

# set bootup splash
SPLASH ?= "psplash"

# default has recovery and swupdate
DISTRO_FEATURES:append = " recovery"
DISTRO_FEATURES:append = " swupdate swupdate-enc"

# default has vendor partition
DISTRO_FEATURES:append = " vendor-partition"

#wpeframework system uer mode
DISTRO_FEATURES:append = " system-user"

# enable selinux
DISTRO_FEATURES:append = " acl xattr pam selinux"
# set the policy
PREFERRED_PROVIDER_virtual/refpolicy ?= "refpolicy-standard"
DEFAULT_ENFORCING = "permissive"

#Production Mode
#Disable overlay readonly rootfs
#Use dm-verity to verify rootfs
#Disable tty console
#Disable adb
#Disable thunder UI
#Disable ssh, telnet and related ports
#RELEASE_MODE = " PROD"

#Overlay Filesystem
DISTRO_FEATURES:append = " ${@bb.utils.contains('RELEASE_MODE', 'PROD', '', 'OverlayFS', d)}"

# dm-verity
DISTRO_FEATURES:append = " ${@bb.utils.contains('RELEASE_MODE', 'PROD', 'dm-verity', '', d)}"

DISTRO_FEATURES:append = " adb dropbear"
DISTRO_FEATURES:append = " cec voiceinput "
DISTRO_FEATURES:append = " screencapture "

# SWUpdate UI config graphic library
SWUPDATE_UI_LIB = "lvgl"
