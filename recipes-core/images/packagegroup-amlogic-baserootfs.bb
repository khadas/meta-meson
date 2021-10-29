SUMMARY = "Amlogic Yocto packgegroup"

LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-amlogic-baserootfs \
    "

RDEPENDS_packagegroup-amlogic-baserootfs = "\
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
    android-tools-adbd \
    aml-hdcp \
    aml-hdcp-load-firmware \
    liblog \
    android-tools-logcat \
    iw \
    wpa-supplicant \
    wifi-amlogic \
    procrank \
    zram \
    modules-load \
    system-config \
    libamavutils \
    aml-libdvr \
    aml-mp-sdk \
    aml-pqserver \
    aml-ubootenv \
    ${@bb.utils.contains('DISTRO_FEATURES', 'verimatrix', 'vmx-sdk-rel', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-cas', 'drmplayer-bin ffmpeg-vendor', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', '', 'aml-hdmicec', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'aml-tvserver', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-dtv', 'aml-dtvdemod', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'playready', 'playready', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wfd-hdcp', 'wfd-hdcp', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-userspace', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'tee-supplicant', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-video-firmware', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'widevine', 'aml-mediadrm-widevine', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'gstreamer1', \
        'gstreamer1.0-plugins-good \
        gstreamer1.0-plugins-bad \
        gst-plugin-aml-asink \
        gst-plugin-aml-vsink \
        gst-aml-drm-plugins \
        gstreamer1.0-libav \
        gst-player \
        ', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'weston', \
        'wayland \
        weston-init \
        meson-display \
        dvalin-dmaexport \
        fbscripts \
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
        'cobalt-plugin aml-cobalt-starboard  dolby-ms12 aml-audio-hal', '', d)} \
    aml-provision \
    tinyalsa-tools \
    aml-audio-service aml-audio-service-testapps \
    ${@bb.utils.contains('DISTRO_FEATURES', 'tts', 'wpeframework-plugin-amltts', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'netflix', \
        'wpeframework-plugin-netflix netflix-aml aml-netflix-esn', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', \
        'bluetooth-mgr bluetooth-core', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'amazon-plugin', \
        'amazon-prime-plugin amazon-prime-src', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-launcher', \
        'aml-launcher libhtmllocal htmllocal-plugin', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-thunder', \
        'device-properties', '', d)} \
    "
