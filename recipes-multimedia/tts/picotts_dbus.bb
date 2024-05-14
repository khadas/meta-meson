SUMMARY = "Pico TTS"
DESCRIPTION = "Text to speech voice sinthesizer from SVox, included in Android AOSP."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../pico_resources/NOTICE;md5=506fbea94b9d051d6478776c50a4c66b"

DEPENDS += "popt"
CFLAGS += "-I${S}/lib"

inherit autotools pkgconfig

SRC_URI = "git://github.com/naggety/picotts.git;protocol=https;branch=master"
SRCREV = "${AUTOREV}"
FILES:${PN} += "${datadir}/*"

S = "${WORKDIR}/git/pico"

