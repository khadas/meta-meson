SUMMARY = "Amlogic Yocto packgegroup"

LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-amlogic-baserootfs \
    "

RDEPENDS:packagegroup-amlogic-baserootfs = "\
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
    ${@bb.utils.contains('DISTRO_FEATURES', 'adb', 'android-tools', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'dropbear', 'dropbear', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aamp', 'aamp', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'fota-upgrade', 'aml-utils-fota-upgrade', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'swupdate', 'cpio update-swfirmware', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'system-user', 'sandbox-setup', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', 'vmx-sdk-rel vmx-release-binaries vmx-plugin', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', 'drmplayer-bin', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-cas', 'aml-cas-hal aml-secdmx', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'aml-tvserver', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'dtvkit', 'dtvkit-release-prebuilt', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'dtvkit-src', 'android-rpcservice', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-dtv', 'aml-dtvdemod aml-afd', '', d)} \
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
        'gstreamer1.0-plugins-base \
        gstreamer1.0-plugins-good \
        gstreamer1.0-plugins-bad \
        gstreamer1.0-plugins-ugly \
        gst-plugin-aml-asink \
        gst-plugin-video-sink \
        gst-plugin-aml-v4l2dec \
        gst-plugin-aml-demux \
        gst-plugin-aml-subtitlesink \
        gst-aml-drm-plugins \
        gstreamer1.0-libav \
        ', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'weston', \
        'wayland \
        weston-init \
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
    gst-agmplayer \
    libvideorender \
    libopus \
    playscripts \
    ${@bb.utils.contains('DISTRO_FEATURES', 'westeros', \
        'westeros westeros-soc-drm westeros-sink', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-thunder', \
        'wpeframework wpeframework-ui thunder-services', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'youtube', \
        'youtube-plugin', '', d)} \
    dolby-ms12 \
    aml-audio-hal \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'aml-audio-effect', '', d)} \
    aml-provision \
    aml-efuse \
    tinyalsa-tools \
    aml-audio-service aml-audio-service-testapps \
    ${@bb.utils.contains('DISTRO_FEATURES', 'netflix64b', \
        'wpeframework-plugin-netflix netflix-aml aml-netflix-esn', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'netflix', \
        'netflix netflix-plugin aml-netflix-esn', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', \
        'bluez-alsa bluez5-obex', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amazon', \
        'amazon-prime-plugin amazon-prime-video', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amazon-uits', \
        'amazon-prime-video-uits', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-launcher', \
        'aml-launcher libhtmllocal htmllocal-plugin', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-thunder', \
        'aml-dial', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'disney', \
        'disney-src disney-plugin', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'disney-test-suite', \
        'disney-basic-tests disney-shield-agent disney-shield-extensions', '', d)} \
    tzdata \
    tzcode \
    format-partitions \
    meson-display \
    "

RDEPENDS:packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amazon-prebuilt-pkg', 'amazon-prebuilt-pkg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'youtube-prebuilt-pkg', 'youtube-prebuilt-pkg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'netflix-prebuilt-pkg', 'netflix-prebuilt-pkg', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'dab-adapter-prebuilt-pkg', 'dab-adapter-prebuilt-pkg', '', d)} \
    "

#For Nagra CAS
RDEPENDS:packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'nagra', 'nagra-sdk nagra-cashal-rel nagra-cert-prebuilt', '', d)} \
    "

#For Irdeto CAS
RDEPENDS:packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'irdeto', 'irdeto-sdk irdeto-cashal-rel', '', d)} \
    "

#VENC related
RDEPENDS:packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'gst-plugin-venc', bb.utils.contains('DISTRO_FEATURES', 'aml-libjpeg', 'gst-plugin-venc-jpeg', '', d), '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'gst-plugin-venc', bb.utils.contains('DISTRO_FEATURES', 'aml-libvpcodec', 'gst-plugin-venc-h264', '', d), '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'gst-plugin-venc', bb.utils.contains('DISTRO_FEATURES', 'aml-libvphevcodec', 'gst-plugin-venc-h265', '', d), '', d)} \
   "

#For AsperitasDvb
RDEPENDS:packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'AsperitasDvb', 'webkitbrowser-plugin lighttpd asperitas-dvb', '', d)} \
    "

#Add ubifs tools
RDEPENDS:packagegroup-amlogic-baserootfs += "${@bb.utils.contains('DISTRO_FEATURES', 'nand', 'mtd-utils-ubifs', '',d)}"

#Handle no widevine case
RDEPENDS:packagegroup-amlogic-baserootfs:remove = "${@bb.utils.contains('DISTRO_FEATURES', 'widevine', '', 'gst-aml-drm-plugins', d)}"

#for miraclecast
RDEPENDS:packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'miraclecast', 'miraclecast wpa-supplicant wfd-hdcp gst-aml-drm-plugins', '', d)} \
    "

# For FBE
RDEPENDS:packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'FBE', 'keyutils fscryptctl', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'FBE', 'trusted-key-tee', '', d)} \
    "

#Add wlcdmi
RDEPENDS_packagegroup-amlogic-baserootfs += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wlcdmi', 'wlcdmi-bin gst-plugin-aml-wlcdmi', '', d)} \
    "
