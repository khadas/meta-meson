FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_append = "\
     file://0019-fix-decoding-DWARF-information-in-the-BFD-library.patch \
"
