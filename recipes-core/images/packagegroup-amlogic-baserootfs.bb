SUMMARY = "Amlogic Yocto packgegroup"

LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-amlogic-baserootfs \
    "

RDEPENDS_packagegroup-amlogic-baserootfs = "\
    cpufrequtils \
    htop \
    toybox \
    sqlite3 \
    icu \
    libarchive \
    libunwind \
    libdaemon \
    libffi \
    libfastjson \
    glibc \
    expat \
    tinyalsa \
    alsa-utils \
    dnsmasq \
    bash \
    dhcpcd \
    systemd \
    bash \
    curl \
    dropbear \
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs \
    e2fsprogs-tune2fs \
    fuse-exfat \
    exfat-utils \
    ntfs-3g \
    glib-2.0 \
    gnutls \
    jansson \
    libgcrypt \
    libgpg-error \
    libpcre \
    libsoup-2.4 \
    libxml2 \
    neon \
    popt \
    spawn-fcgi \
    yajl \
    procps \
    systemd \
    libnl \
    dbus \
    faad2 \
    libopus \
    android-tools \
    aml-hdcp \
    liblog \
    android-tools-logcat \
    iw \
    wpa-supplicant \
    wifi-amlogic \
    procrank \
    zram \
    modules-load \
    system-config \
    aml-libdvr \
    aml-mp-sdk \
    aml-pqserver \
    aml-subtitleserver \
    aml-ubootenv \
    aml-utils-simulate-key \
    vulkan-loader \
    aml-hdmicec \
    ${@bb.utils.contains('DISTRO_FEATURES', 'fota-upgrade', 'aml-utils-fota-upgrade', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'swupdate', 'cpio update-swfirmware', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'system-user', 'sandbox-setup', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', 'vmx-sdk-rel vmx-release-binaries vmx-plugin', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', 'drmplayer-bin', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-cas', 'aml-cas-hal aml-secdmx', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'aml-tvserver', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'dtvkit', 'dtvkit-release-prebuilt', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'dtvkit-src', 'android-rpcservice', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-dtv', 'aml-dtvdemod', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'playready', 'playready', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wfd-hdcp', 'wfd-hdcp', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-userspace', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'tee-supplicant', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-video-firmware', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'widevine', 'aml-mediadrm-widevine', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-libjpeg', 'libjpeg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-libmultienc', 'libmultienc libge2d libion', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-libvpcodec', 'libvpcodec h264bitstream libion', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-libvphevcodec', 'libvphevcodec libge2d libion', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'gstreamer1', \
        'gstreamer1.0-plugins-good \
        gstreamer1.0-plugins-bad \
        gst-plugin-aml-asink \
        gst-plugin-video-sink \
        gst-plugin-aml-v4l2dec \
        gst-aml-drm-plugins \
        gstreamer1.0-libav \
        gst-player \
        ', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'weston', \
        'wayland \
        weston-init \
        meson-display \
        fbscripts \
        ', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'qt5', \
        'qtbase \
        qtwebkit \
        qtwayland \
        qtquickcontrols2 \
        qtdeclarative \
        qtxmlpatterns \
        fontconfig \
        openssl \
        ', '', d)} \
    pulseaudio \
    ffmpeg \
    libopus \
    playscripts \
    ${@bb.utils.contains('DISTRO_FEATURES', 'westeros', \
        'westeros westeros-soc-drm westeros-sink', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-thunder', \
        'wpeframework wpeframework-ui thunder-services', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'cobalt', \
        'cobalt-plugin aml-cobalt-starboard', '', d)} \
    dolby-ms12 \
    aml-audio-hal \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'aml-audio-effect', '', d)} \
    aml-provision \
    aml-efuse \
    tinyalsa-tools \
    aml-audio-service aml-audio-service-testapps \
    ${@bb.utils.contains('DISTRO_FEATURES', 'tts', 'wpeframework-plugin-amltts', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'netflix', \
        'wpeframework-plugin-netflix netflix-aml aml-netflix-esn', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', \
        'bluetooth-mgr bluetooth-core bluez-alsa bluez5-obex', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amazon-plugin', \
        'amazon-prime-plugin amazon-prime-video', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-launcher', \
        'aml-launcher libhtmllocal htmllocal-plugin', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-thunder', \
        'aml-dial', '', d)} \
    tzdata \
    tzcode \
    format-partitions \
    "

RDEPENDS_packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amazon-prebuilt-pkg', 'amazon-prebuilt-pkg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'cobalt-prebuilt-pkg', 'cobalt-prebuilt-pkg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'netflix-prebuilt-pkg', 'netflix-prebuilt-pkg', '', d)} \
    "

#For Nagra CAS
RDEPENDS_packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'nagra', 'nagra-sdk nagra-cashal-rel nagra-cert-prebuilt', '', d)} \
    "

#For Irdeto CAS
RDEPENDS_packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'irdeto', 'irdeto-sdk irdeto-cashal-rel', '', d)} \
    "

#VENC related
RDEPENDS_packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'gst-plugin-venc', bb.utils.contains('DISTRO_FEATURES', 'aml-libjpeg', 'gst-plugin-venc-jpeg', '', d), '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'gst-plugin-venc', bb.utils.contains('DISTRO_FEATURES', 'aml-libvpcodec', 'gst-plugin-venc-h264', '', d), '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'gst-plugin-venc', bb.utils.contains('DISTRO_FEATURES', 'aml-libvphevcodec', 'gst-plugin-venc-h265', '', d), '', d)} \
   "
#Add ubifs tools
RDEPENDS_packagegroup-amlogic-baserootfs += "${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'mtd-utils-ubifs', '',d)}"

#Handle no widevine case
RDEPENDS_packagegroup-amlogic-baserootfs_remove = "${@bb.utils.contains('DISTRO_FEATURES', 'widevine', '', 'gst-aml-drm-plugins', d)}"
