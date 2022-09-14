#!/bin/sh

WIFI_DIR="/opt/wifi"
WPA_SUP_CONF="${WIFI_DIR}/wpa_supplicant.conf"

if [ ! -d "${WIFI_DIR}"  ]; then
    echo "Creating '${WIFI_DIR}'..."
    mkdir -p ${WIFI_DIR}
fi

if [ ! -f "${WPA_SUP_CONF}"  ]; then
    echo "'${WPA_SUP_CONF}' not found; creating with deafult values..."
    echo "ctrl_interface=/var/run/wpa_supplicant" >> ${WPA_SUP_CONF}
    echo "update_config=1" >> ${WPA_SUP_CONF}
    echo "wowlan_triggers=disconnect magic_pkt gtk_rekey_failure" >> ${WPA_SUP_CONF}
    sync
fi
