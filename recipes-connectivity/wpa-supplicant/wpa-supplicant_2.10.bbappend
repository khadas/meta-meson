include wpa-supplicant.inc

do_configure:append () {
   echo "CONFIG_WEP=y" >> wpa_supplicant/.config
   if ${@bb.utils.contains('DISTRO_FEATURES','miraclecast','true','false',d)}; then
   sed -i "s/^#CONFIG_P2P/CONFIG_P2P/;s/^#CONFIG_WIFI_DISPLAY/CONFIG_WIFI_DISPLAY/" wpa_supplicant/.config
   fi
}

