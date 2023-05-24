do_install:append() {
    sed -i '/${setsid:-} ${getty}/ s/$/& -a root --noclear/' ${D}${base_bindir}/start_getty
}
