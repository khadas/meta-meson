SRC_URI += "file://0001-bluez5_utils-add-qca9377-bt-support-1-3.patch"
SRC_URI += "file://0001-BT-add-qca6174-bt-support-2-3.patch"
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "aml-wifi", \
            "file://0001-BT-add-amlbt-w1-5-5.patch \
            file://0001-BT-when-iperf-BT-play-caton-1-2.patch", "", d)}"
SRC_URI += "file://0001-RDK-fix-issue-in-bluez5.55-1-1.patch"
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "bt-qca", "file://0001-BT-add-qca-bt-wakeup-1-3.patch", "", d)}"
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "bt-qca", "file://0001-BT-qca-fix-adv-auto-wakeup.patch", "", d)}"
SRC_URI += "file://0001-BT-fix-repeatedly-connect-disconnect.patch"
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "aml-wifi", "file://0001-BT-add-amlbt-w1-wakeup.patch", "", d)}"
SRC_URI += "file://0001-bluez5-fix-rcu-reconnect-1-1.patch"
SRC_URI += "file://0001-BT-fix-pair-inturrpt-error.patch"
SRC_URI += "file://0001-BT-fix-connect-failed-remove-device.patch"
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "aml-wifi", "file://0002-BT-W1-fw-use-bin-format.patch", "", d)}"
SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "aml-w2", "file://0002-add-w2-support.patch", "", d)}"
