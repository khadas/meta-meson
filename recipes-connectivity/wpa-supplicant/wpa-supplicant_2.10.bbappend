include wpa-supplicant.inc

SRC_URI += " \
            file://0002-enlarge-cmd-reply-buf_2.10.patch \
            "

do_configure:append () {
   echo "CONFIG_WEP=y" >> wpa_supplicant/.config
   if ${@bb.utils.contains('DISTRO_FEATURES','miraclecast','true','false',d)}; then
   sed -i "s/^#CONFIG_P2P/CONFIG_P2P/;s/^#CONFIG_WIFI_DISPLAY/CONFIG_WIFI_DISPLAY/" wpa_supplicant/.config
   fi
}

