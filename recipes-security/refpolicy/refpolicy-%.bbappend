FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
        file://0001-access_vectors-Permission-nlmsg_readpriv-in-class-ne.patch \
        file://0001-policy-modules-update-for-amlogic-yocto.patch \
        file://0002-policy-modules-for-WiFi-BT.patch \
        file://0003-policy-modules-for-mount-vendor-device.patch \
        file://0004-policy-modules-enable-sysadm-read-char-device.patch \
        file://0005-policy-modules-enable-sysadm-watch-journal-files.patch \
        file://0006-policy-modules-add-refpolicy-for-data-directory.patch \
        "
