SUMMARY = "Amlogic Yocto packgegroup"

LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-amlogic-fullrootfs \
    "

RDEPENDS_packagegroup-amlogic-fullrootfs = "\
    toybox \
    libresample \
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
    android-tools-adbd \
    alsa-utils \
    playscripts \
    iw \
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
    fcgi \
    glib-2.0 \
    gnutls \
    jansson \
    libgcrypt \
    libgpg-error \
    libpcre \
    libsoup-2.4 \
    libxml2 \
    lighttpd \
    log4c \
    logrotate \
    mtd-utils-ubifs \
    neon \
    popt \
    spawn-fcgi \
    yajl \
    procps \
    systemd \
    libpam \
    libnl \
    wififw \
    dbus \
    ${@bb.utils.contains('DISTRO_FEATURES', 'aml-dtv', 'aml-dtvdemod', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-userspace', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'tee-supplicant', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'optee-video-firmware', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'optee', 'aml-provision', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'widevine', 'aml-mediadrm-widevine', '', d)} \
    pulseaudio \
    bluez5 \
    packagegroup-tools-bluetooth \
    pulseaudio-client-conf-sato \
    pulseaudio-server \
    ffmpeg \
    faad2 \
    libopus \
    civetweb \
    libsrtp \
    "

RDEPENDS_packagegroup-amlogic-fullrootfs += " ${@bb.utils.contains('DISTRO_FEATURES', 'gstreamer1', \
                                             'gstreamer1.0-plugins-full \
                                             gstreamer1.0-plugins-good \
                                             gstreamer1.0-plugins-bad \
                                             gst-plugin-aml-asink \
                                             gst-plugin-aml-vsink \
                                             gst-aml-drm-plugins \
                                             gstreamer1.0-libav \
                                             ', '', d)} "

RDEPENDS_packagegroup-amlogic-fullrootfs += " ${@bb.utils.contains('DISTRO_FEATURES', 'weston', \
                                             'weston \
                                             wayland \
                                             weston-init \
                                             meson-display \
                                             dvalin-dmaexport \
                                             ', '', d)} "

#Adding smartmontools only for Hard Disk enabled devices.
RDEPENDS_packagegroup-amlogic-fullrootfs += "${@bb.utils.contains('DISTRO_FEATURES', 'storage_hdd', \
                                             'smartmontools \
                                             ', '',d)}"
