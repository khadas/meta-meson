#!/bin/bash

# Get the RFKILL_STATE from the environment variable
RFKILL_STATE="${RFKILL_STATE}"

# Check the state
case "${RFKILL_STATE}" in
  0)
    echo "WLAN RFKILL switch is now OFF"
    echo "Nothing to do"
    ;;
  1)
    echo "WLAN RFKILL switch is now ON"
    /bin/systemctl start wpa_supplicant &
    ;;
  *)
    echo "Unknown RFKILL switch state: ${RFKILL_STATE}"
    ;;
esac
