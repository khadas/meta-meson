FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG_remove = "gnutls"
PACKAGECONFIG_append = " openssl"

inherit systemd

SRC_URI += " file://wpa_supplicant.service "
SRC_URI += " file://createDefaultWPASupplicantConfigFile.sh "

do_configure_append () {
   # Add the "-fPIC" option to CFLAGS to allow the Pace WiFi HAL module to link against wpa-supplicant
   echo "CFLAGS += -fPIC" >> wpa_supplicant/.config

   echo "CONFIG_BUILD_WPA_CLIENT_SO=y" >> wpa_supplicant/.config

   if grep -q '\bCONFIG_DEBUG_FILE\b' wpa_supplicant/.config; then
      sed -i -e '/\bCONFIG_DEBUG_FILE\b/s/.*/CONFIG_DEBUG_FILE=y/' wpa_supplicant/.config
   else
      echo "CONFIG_DEBUG_FILE=y" >> wpa_supplicant/.config
   fi

   sed -i -- 's/CONFIG_AP=y/\#CONFIG_AP=y/' wpa_supplicant/.config
   sed -i -- 's/CONFIG_DRIVER_HOSTAP=y/\#CONFIG_DRIVER_HOSTAPAP=y/' wpa_supplicant/.config
}

do_install_append () {
   install -d ${D}${bindir}
   install -m 755 ${WORKDIR}/createDefaultWPASupplicantConfigFile.sh ${D}${bindir}
   if ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "true", "false", d)}
   then
       install -d ${D}${systemd_unitdir}/system
       install -D -m 0644 ${WORKDIR}/wpa_supplicant.service ${D}${systemd_unitdir}/system/wpa_supplicant.service
   fi
   install -d ${D}${libdir}
   install -m 0644 ${S}/wpa_supplicant/libwpa_client.so ${D}${libdir}
}

FILES_SOLIBSDEV = ""
FILES_${PN} += "${libdir}/libwpa_client.so"
SYSTEMD_SERVICE_${PN} = "wpa_supplicant.service"
