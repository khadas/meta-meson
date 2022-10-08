
do_configure[noexec] = "1"
do_configure1[depends] += "virtual/kernel:do_shared_workdir openssl-native:do_populate_sysroot"


# when try to call make nod scripts in kernel-5.15. need cross compile objcopy and the value
# set by export is invalid.

do_configure1() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	for t in prepare scripts_basic scripts; do
		oe_runmake CC="${KERNEL_CC}" LD="${KERNEL_LD}" AR="${KERNEL_AR}" OBJCOPY="aarch64-poky-linux-objcopy" \
		-C ${STAGING_KERNEL_DIR} O=${STAGING_KERNEL_BUILDDIR} $t
	done
}

addtask do_configure1 before do_compile
