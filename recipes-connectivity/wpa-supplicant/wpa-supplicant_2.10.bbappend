include wpa-supplicant.inc

do_configure_append () {
   echo "CONFIG_WEP=y" >> wpa_supplicant/.config
}

