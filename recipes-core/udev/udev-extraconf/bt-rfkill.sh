#!/bin/bash

# Get the RFKILL_STATE from the environment variable
RFKILL_STATE="${RFKILL_STATE}"

# Check the state
case "${RFKILL_STATE}" in
  0)
    echo "BT RFKILL switch is now OFF"
    /bin/systemctl stop bluez
    ;;
  1)
    echo "BT RFKILL switch is now ON"
    /bin/systemctl start bluez
    ;;
  *)
    echo "Unknown RFKILL switch state: ${RFKILL_STATE}"
    ;;
esac
