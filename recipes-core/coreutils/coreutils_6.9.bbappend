FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = "file://selinux.patch"

PACKAGECONFIG_append = " ${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[selinux] = "ac_cv_header_selinux_flask_h=yes ac_cv_header_selinux_selinux_h=yes ac_cv_header_selinux_context_h=yes ac_cv_search_setfilecon=-lselinux,ac_cv_header_selinux_flask_h=no ac_cv_header_selinux_selinux_h=no ac_cv_header_selinux_context_h=no ac_cv_search_setfilecon=,libselinux,"

bindir_progs += "chcon runcon"
