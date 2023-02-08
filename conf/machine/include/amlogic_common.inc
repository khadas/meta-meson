require conf/machine/include/arm/arch-armv8a.inc
include conf/machine/include/amlogic_externalsrc.inc
include conf/machine/include/amlogic_patches.inc
include conf/machine/include/device-properties.inc

MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += " \
        media-modules "

MACHINE_EXTRA_RRECOMMENDS = " kernel-modules kernel-devicetree"
EXTRA_IMAGEDEPENDS += "u-boot"
IMAGE_FSTYPES += "tar.gz ext4"
ROOTFS_TYPE = "ext4"

IMAGE_GEN_DEBUGFS = "1"
IMAGE_FSTYPES_DEBUGFS = ""
SERIAL_CONSOLES = "921600;ttyS0"
SERIAL_CONSOLES_t5w = "115200;ttyS0"
SERIAL_CONSOLES_t3 = "115200;ttyS0"
SERIAL_CONSOLES_tm2 = "115200;ttyS0"
SERIAL_CONSOLES_a213y = "115200;ttyS0"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-meson"
KERNEL_IMAGETYPE_aarch64 = "Image.gz"
KERNEL_IMAGETYPE_armv7a = "uImage"
UBOOT_SUFFIX = "bin"
MACHINE_FEATURES = "usbgadget usbhost vfat "
IMAGE_BOOT_FILES ?= "u-boot.${UBOOT_SUFFIX}"
INITRAMFS_FSTYPES = "cpio.gz"

#KERNEL_MODULE_AUTOLOAD += "mali"
TDK_VERSION = "v3.8"
TDK_VERSION_t5d = "v2.4"
TDK_VERSION_sc2 = "v3.8/dev/S905X4"
TDK_VERSION_t7 = "v3.8/dev/A311D2"
TDK_VERSION_s4 = "v3.8/dev/S905Y4"
TDK_VERSION_t3 = "v3.8/dev/T965D4"

#Widevine Version
WIDEVINE_VERSION = "16"

AML_GIT_ROOT ?= "git.myamlogic.com"
AML_GIT_PROTOCOL ?= "git"
AML_GIT_ROOT_YOCTO_SUFFIX ?= "/yocto"
AML_GIT_ROOT_PR ?= "git.myamlogic.com"
AML_GIT_ROOT_WV ?= "git.myamlogic.com"
AML_GIT_ROOT_PROTOCOL ?= "git"

#check connectivity using bing
#CONNECTIVITY_CHECK_URIS = "https://www.bing.com/"

LINUXLIBCVERSION ?= "5.4%"
PREFERRED_VERSION_libdrm = "2.4.100"
PREFERRED_VERSION_tinyalsa = "1.1.1"

#Only support amlogic widevine
PREFERRED_PROVIDER_widevine = "aml-mediadrm-widevine"

PREFERRED_VERSION_libgles-eabihf-dvalin-wayland-drm = "r37p0"
PREFERRED_VERSION_libgles-eabihf-gondul-wayland-drm = "r37p0"

#for vulkan
PREFERRED_VERSION_vulkan-headers = "1.2.196"
PREFERRED_VERSION_vulkan-loader = "1.2.196"

#for cairo
PREFERRED_VERSION_cairo_dunfell = "1.14.6"

WARN_QA_append = "ldflags installed-vs-shipped already-stripped pkgvarcheck file-rdeps"
ERROR_QA_remove = "ldflags installed-vs-shipped already-stripped pkgvarcheck file-rdeps"

DEBUG_PREFIX_MAP = "-fdebug-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR} \
                    -fdebug-prefix-map=${STAGING_DIR_HOST}= \
                    -fdebug-prefix-map=${STAGING_DIR_NATIVE}= \
                    "

HOSTTOOLS_NONFATAL += "bc cc c++ bison repo python"

DISABLE_STATIC_pn-libevent = ""

AML_RDK_PREBUILT_SITE ?= "http://10.18.11.6/shanghai/image/linux-distro/Public/YOCTO/prebuilt/"
AML_TOOLS_SITE ?= "http://10.18.11.120:8000/Tools"

# For dm-verity
IMAGE_CLASSES += "${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'image_types aml-dm-verity-img', '', d)}"

# Sytem partition
DM_VERITY_IMAGE = "${@bb.utils.contains('MULTTILIBS', 'multilib:lib32', 'lib32-amlogic-yocto', 'amlogic-yocto', d)}"
# vendor partition
VENDOR_DM_VERITY_IMAGE = "vendor-image"

DM_VERITY_IMAGE_TYPE = "ext4"
STAGING_VERITY_DIR = "${DEPLOY_DIR_IMAGE}"

XZ_COMPRESSION_LEVEL ?= "-e -M 50% -9"

VIRTUAL-RUNTIME_init_manager ?= "systemd"
VIRTUAL-RUNTIME_initscripts ?= "systemd-compat-units"

KERNEL_MODULE_AUTOLOAD += "gpio_keypad adc_keypad reg_access"

# set bootup splash
SPLASH ?= "psplash"

#wpeframework system uer mode
DISTRO_FEATURES_append = " system-user"

# enable selinux
DISTRO_FEATURES_append = " acl xattr pam selinux"
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
DISTRO_FEATURES_append = " ${@bb.utils.contains('RELEASE_MODE', 'PROD', '', 'OverlayFS', d)}"

# dm-verity
DISTRO_FEATURES_append = " ${@bb.utils.contains('RELEASE_MODE', 'PROD', 'dm-verity', '', d)}"

DISTRO_FEATURES_append = " adb dropbear"
