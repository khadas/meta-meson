include wpa-supplicant.inc

SRC_URI += " file://wpa_supplicant.service "
SRC_URI += " file://createDefaultWPASupplicantConfigFile.sh "
do_configure_append () {
   echo "CONFIG_WEP=y" >> wpa_supplicant/.config
}

