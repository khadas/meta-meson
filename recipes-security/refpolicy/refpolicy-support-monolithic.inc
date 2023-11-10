# FUN-1579 - override do_install() from
#       meta-meson/recipes-security/refpolicy/refpolicy_common.inc
#       The patch can be removed once the patch in CEVD-854 is accepted

EXTRA_OEMAKE += "tc_sbindir=${STAGING_DIR_NATIVE}/sbin"

prepare_policy_store:prepend () {
    if [ "${POLICY_MONOLITHIC}" = "y" ]; then
        oe_runmake 'DESTDIR=${D}' 'prefix=${D}${prefix}' install
        return 0
    fi
}

rebuild_policy:prepend() {
  [ "${POLICY_MONOLITHIC}" = "y" ] && return 0
}
