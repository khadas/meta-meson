SUMMARY = "gsensor application"
SECTION = "examples"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit cmake

SRC_URI = " \ 
    file://src/gsensor_sample_demo.c \
    file://CMakeLists.txt \
    "

S = "${WORKDIR}"




