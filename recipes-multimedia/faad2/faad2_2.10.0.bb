SUMMARY = "An open source MPEG-4 and MPEG-2 AAC decoding library"
HOMEPAGE = "http://www.audiocoding.com/faad2.html"
SECTION = "libs"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=381c8cbe277a7bc1ee2ae6083a04c958"

LICENSE_FLAGS = "commercial"

SRC_URI = "https://github.com/knik0/faad2/archive/refs/tags/2_10_0.tar.gz"
SRC_URI[md5sum] = "f948925a6763e30c53078f5af339d6cc"
SRC_URI[sha256sum] = "0c6d9636c96f95c7d736f097d418829ced8ec6dbd899cc6cc82b728480a84bfb"

S = "${WORKDIR}/faad2-2_10_0"

inherit autotools lib_package
