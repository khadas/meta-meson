FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG:remove = "gnutls"
PACKAGECONFIG:append = " openssl"

inherit systemd

SRC_URI += " file://wpa_supplicant.service "

do_configure:append () {
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

do_install:append () {
   if ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "true", "false", d)}
   then
       install -d ${D}${systemd_unitdir}/system
       install -D -m 0644 ${WORKDIR}/wpa_supplicant.service ${D}${systemd_unitdir}/system/wpa_supplicant.service
   fi
   install -d ${D}${libdir}
   install -m 0644 ${S}/wpa_supplicant/libwpa_client.so ${D}${libdir}

   #when connect wifi AP, wpa-supplicant need rename /etc/wpa_supplicant.conf, can't write /etc partition on read-only rootfs
   #let wpa-supplicant to find wpa_supplicant under /data instead of /etc
   if ${@bb.utils.contains("DISTRO_FEATURES", "OverlayFS", "false", "true", d)}; then
     WPA_PATH="/data/persistent"
     sed -i "s#/etc/wpa_supplicant.conf#$WPA_PATH/wpa_supplicant.conf#g" ${D}${systemd_unitdir}/system/wpa_supplicant.service
     sed -i "/^ExecStart=\/usr\/sbin\/wpa_supplicant/i \ExecStart=/bin/sh -c 'if [ ! -e $WPA_PATH/wpa_supplicant.conf ]; then mkdir -p $WPA_PATH;cp --preserve=context /etc/wpa_supplicant.conf $WPA_PATH/; fi'" ${D}${systemd_unitdir}/system/wpa_supplicant.service
   fi
}

FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/libwpa_client.so"
SYSTEMD_SERVICE:${PN} = "wpa_supplicant.service"
