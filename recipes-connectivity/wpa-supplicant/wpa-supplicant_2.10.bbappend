include wpa-supplicant.inc

do_configure:append () {
   echo "CONFIG_WEP=y" >> wpa_supplicant/.config
}

