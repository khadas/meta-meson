# For YouTube
DEVICE_PROPERTY_FIRMWARE_VERSION ?= "Unknown"
DEVICE_PROPERTY_BRAND_NAME ?= "Amlogic"
DEVICE_PROPERTY_MODEL_NAME ?= "Unknown"
DEVICE_PROPERTY_OPERATOR_NAME ?= "Unknown"
DEVICE_PROPERTY_YOUTUBE_CERT_SCOPE ?= "Unknown"
# YouTube define device type
# BDP:   Blu-Ray Player
# GAME:  Game Console
# OTT:   Over-The-Top Device
# STB:   Set-Top-Box, which includes a tuner and demodulator
# TV:    Television Set
DEVICE_PROPERTY_DEVICE_TYPE ?= "Unknown"
DEVICE_PROPERTY_CHIPSET ?= "Unknown"
DEVICE_PROPERTY_MODEL_YEAR ?= "Unknown"
DEVICE_PROPERTY_PLATFORM_NAME ?= "Linux"
DEVICE_PROPERTY_SYSINTEGRATE_NAME ?= "Amlogic"
# End For YouTube

# For Amazon
DEVICE_PROPERTY_AMAZON_DTID ?= "${@bb.utils.contains('DISTRO_FEATURES', 'amazon', bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'AEKBK0TQX6QYF', 'A3SJQCMWSDF1EG', d), '', d)}"
# End For Amazon

DEVICE_PROPERTY_MANUFACTURER ?= "Amlogic"

DEVICE_PROPERTY_WIFI_INTERFACE ?= "wlan0"
DEVICE_PROPERTY_ETHERNET_INTERFACE ?= "eth0"
DEVICE_PROPERTY_VOICEINPUT_SUPPORT ?= "${@bb.utils.contains('DISTRO_FEATURES', 'voiceinput', 'true', 'false', d)}"
DEVICE_PROPERTY_CEC_SUPPORT ?= "${@bb.utils.contains('DISTRO_FEATURES', 'cec', 'true', 'false', d)}"
DEVICE_PROPERTY_WIFI_SUPPORT ?= "${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'true', 'false', d)}"
DEVICE_PROPERTY_BLUETOOTH_SUPPORT ?= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'true', 'false', d)}"
DEVICE_PROPERTY_MIRACLECAST_SUPPORT ?= "${@bb.utils.contains('DISTRO_FEATURES', 'miraclecast', 'true', 'false', d)}"
DEVICE_PROPERTY_TTS_SUPPORT ?= "${@bb.utils.contains('DISTRO_FEATURES', 'tts', 'true', 'false', d)}"
DEVICE_PROPERTY_TV_PLATFORM ?= "${@bb.utils.contains('DISTRO_FEATURES', 'amlogic-tv', 'true', 'false', d)}"
DEVICE_PROPERTY_USE_APPMANAGER ?= "${@bb.utils.contains('DISTRO_FEATURES', 'appmanager', 'true', 'false', d)}"
DEVICE_PROPERTY_LAUNCHER_APPS ?= "${@bb.utils.contains('DISTRO_FEATURES', 'mediaplayer', 'localplayer,', '', d)}"
DEVICE_PROPERTY_LAUNCHER_APPS += "${@bb.utils.contains('DISTRO_FEATURES', 'youtube', 'youtube, youtubetv, youtubekids,', '', d)}"
DEVICE_PROPERTY_LAUNCHER_APPS += "${@bb.utils.contains('DISTRO_FEATURES', 'netflix', 'netflix,', '', d)}"
DEVICE_PROPERTY_LAUNCHER_APPS += "${@bb.utils.contains('DISTRO_FEATURES', 'netflix64b', 'netflix,', '', d)}"
DEVICE_PROPERTY_LAUNCHER_APPS += "${@bb.utils.contains('DISTRO_FEATURES', 'amazon', 'amazon,', '', d)}"
DEVICE_PROPERTY_LAUNCHER_APPS += "${@bb.utils.contains('DISTRO_FEATURES', 'disney', 'disney,', '', d)}"
DEVICE_PROPERTY_STARTUP_APP ?= ""
DEVICE_PROPERTY_LAUNCH_IN_BACKGROUND ?= "${@bb.utils.contains('DISTRO_FEATURES', 'low-memory', '', 'amazon, netflix', d)}"
