FILESEXTRAPATHS:prepend := "${THISDIR}/refpolicy_aml:"

SRC_URI += " \
        file://0001-access_vectors-Permission-nlmsg_readpriv-in-class-ne.patch \
        file://0001-policy-modules-update-for-amlogic-yocto.patch \
        file://0002-policy-modules-for-WiFi-BT.patch \
        file://0003-policy-modules-for-mount-vendor-device.patch \
        file://0004-policy-modules-enable-sysadm-read-char-device.patch \
        file://0005-policy-modules-enable-sysadm-watch-journal-files.patch \
        file://0006-policy-modules-add-refpolicy-for-data-directory.patch \
        file://0007-policy-modules-add-support-ubifs.patch \
        file://0008-policy-modules-for-lighttpd.patch \
        file://0009-policy-modules-for-miracast.patch \
        file://0010-policy-modules-for-network.patch \
        "
