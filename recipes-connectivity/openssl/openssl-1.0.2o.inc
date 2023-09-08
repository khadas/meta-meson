SUMMARY = "Secure Socket Layer"
DESCRIPTION = "Secure Socket Layer (SSL) binary and related cryptographic tools."
HOMEPAGE = "http://www.openssl.org/"
BUGTRACKER = "http://www.openssl.org/news/vulnerabilities.html"
SECTION = "libs/network"

# "openssl | SSLeay" dual license
LICENSE = "OpenSSL"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f9a8f968107345e0b75aa8c2ecaa7ec8"

DEPENDS = "makedepend-native hostperl-runtime-native"

DEPENDS:remove_daisy = "hostperl-runtime-native"
DEPENDS:append_daisy = " perl-native-runtime"

DEPENDS:append_class-target = " openssl-native"

PROVIDES += "openssl10"

SRC_URI = "http://www.openssl.org/source/openssl-${PV}.tar.gz \
          "
S = "${WORKDIR}/openssl-${PV}"

PACKAGECONFIG[perl] = ",,,"

EXTRA_OECONF += "no-camellia"
EXTRA_OECONF += "no-seed"
EXTRA_OECONF += "no-rc5"
EXTRA_OECONF += "no-md2"
EXTRA_OECONF += "no-md4"
EXTRA_OECONF += "no-ripemd"
EXTRA_OECONF += "no-mdc2"
EXTRA_OECONF += "no-ssl2"
EXTRA_OECONF += "no-ssl3"
EXTRA_OECONF += "no-err"
EXTRA_OECONF += "no-krb5"
EXTRA_OECONF += "no-hw"
EXTRA_OECONF += "no-srp"
EXTRA_OECONF += "no-idea"
EXTRA_OECONF += "no-rc4"
EXTRA_OECONF:remove += "--disable-static"

AR:append = " r"
TERMIO_libc-musl = "-DTERMIOS"
TERMIO ?= "-DTERMIO"
# Avoid binaries being marked as requiring an executable stack since it
# doesn't(which causes and this causes issues with SELinux
CFLAG = "${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', '-DL_ENDIAN', '-DB_ENDIAN', d)} \
	 ${TERMIO} ${CFLAGS} -Wall -Wa,--noexecstack"

export DIRS = "crypto ssl apps"
export EX_LIBS = "-lgcc -ldl"
export AS = "${CC} -c"
EXTRA_OEMAKE = "-e MAKEFLAGS="

inherit pkgconfig siteinfo multilib_header ptest

# ----------------------------------------------------------------------------
# BEGIN : Backport of "relative_symlinks" class from OE 2.3
# ----------------------------------------------------------------------------
# Postprocess ${D} to replace absolute symlinks with relative symlinks. Avoids
# triggering "absolute path symlink" build time errors when building the
# meta-rdk openssl recipe with OE 2.3. In OE 2.3 the same postprocessing is
# done by inheritting the "relative_symlinks" class, however since that class
# is only available from OE 2.3 onwards, provide the same logic directly in the
# openssl recipe to be compatible with OE 1.6, OE 2.1 and OE 2.2.
# ----------------------------------------------------------------------------

def openssl_replace_absolute_symlinks(basedir, d):
    """
    Walk basedir looking for absolute symlinks and replacing them with relative ones.
    The absolute links are assumed to be relative to basedir
    (compared to make_relative_symlink above which tries to compute common ancestors
    using pattern matching instead)
    """
    for walkroot, dirs, files in os.walk(basedir):
        for file in files + dirs:
            path = os.path.join(walkroot, file)
            if not os.path.islink(path):
                continue
            link = os.readlink(path)
            if not os.path.isabs(link):
                continue
            walkdir = os.path.dirname(path.rpartition(basedir)[2])
            base = os.path.relpath(link, walkdir)
            bb.debug(2, "Replacing absolute path %s with relative path %s" % (link, base))
            os.remove(path)
            os.symlink(base, path)

python install_relative_symlinks () {
    openssl_replace_absolute_symlinks(d.getVar('D', True), d)
}

do_install[postfuncs] += "install_relative_symlinks"

# ----------------------------------------------------------------------------
# END : Backport of "relative_symlinks" class from OE 2.3
# ----------------------------------------------------------------------------

PACKAGES =+ "libcrypto-1.0.2o libssl-1.0.2o ${PN}-misc openssl-conf-1.0.2o"
FILES:libcrypto = "${libdir}/libcrypto${SOLIBS}"
FILES:libssl = "${libdir}/libssl${SOLIBS}"
FILES:${PN} =+ " ${libdir}/ssl/*"
FILES:${PN}-misc = "${libdir}/ssl/misc"
RDEPENDS:${PN}-misc = "${@bb.utils.contains('PACKAGECONFIG', 'perl', 'perl', '', d)}"
FILES:${PN}-dev += "${libdir}/libcrypto${SOLIBSDEV}"

# Add the openssl.cnf file to the openssl-conf package.  Make the libcrypto
# package RRECOMMENDS on this package.  This will enable the configuration
# file to be installed for both the base openssl package and the libcrypto
# package since the base openssl package depends on the libcrypto package.
FILES:openssl-conf = "${sysconfdir}/ssl/openssl.cnf"
CONFFILES_openssl-conf = "${sysconfdir}/ssl/openssl.cnf"
RRECOMMENDS_libcrypto += "openssl-conf"
RDEPENDS:${PN}-ptest += "${PN}-misc make perl perl-module-filehandle bc"

do_configure:prepend:darwin () {
	sed -i -e '/version-script=openssl\.ld/d' Configure
}

do_configure () {
	cd util
	perl perlpath.pl ${STAGING_BINDIR_NATIVE}
	cd ..
	ln -sf apps/openssl.pod crypto/crypto.pod ssl/ssl.pod doc/

	os=${HOST_OS}
	case $os in
	linux-uclibc |\
	linux-uclibceabi |\
	linux-gnueabi |\
	linux-uclibcspe |\
	linux-gnuspe |\
	linux-musl*)
		os=linux
		;;
		*)
		;;
	esac
	target="$os-${HOST_ARCH}"
	case $target in
	linux-arm)
		target=linux-armv4
		;;
	linux-armeb)
		target=linux-elf-armeb
		;;
	linux-aarch64*)
		target=linux-generic64
		;;
	linux-sh3)
		target=debian-sh3
		;;
	linux-sh4)
		target=debian-sh4
		;;
	linux-i486)
		target=debian-i386-i486
		;;
	linux-i586 | linux-viac3)
		target=debian-i386-i586
		;;
	linux-i686)
		target=debian-i386-i686/cmov
		;;
	linux-gnux32-x86_64)
		target=linux-x32
		;;
	linux-gnu64-x86_64)
		target=linux-x86_64
		;;
	linux-mips)
		target=debian-mips
		;;
	linux-mipsel)
		target=debian-mipsel
		;;
        linux-*-mips64 | linux-mips64)
               target=linux-mips
                ;;
	linux-microblaze*|linux-nios2*)
		target=linux-generic32
		;;
	linux-powerpc)
		target=linux-ppc
		;;
	linux-powerpc64)
		target=linux-ppc64
		;;
	linux-supersparc)
		target=linux-sparcv8
		;;
	linux-sparc)
		target=linux-sparcv8
		;;
	darwin-i386)
		target=darwin-i386-cc
		;;
	esac
	# inject machine-specific flags
	sed -i -e "s|^\(\"$target\",\s*\"[^:]\+\):\([^:]\+\)|\1:${CFLAG}|g" Configure
        useprefix=${prefix}
        if [ "x$useprefix" = "x" ]; then
                useprefix=/
        fi        
	perl ./Configure ${EXTRA_OECONF} shared --prefix=$useprefix --openssldir=${libdir}/ssl --libdir=`basename ${libdir}` $target
	make depend
}

do_compile:prepend_class-target () {
    sed -i 's/\((OPENSSL=\)".*"/\1"openssl"/' Makefile
}

do_compile () {
	oe_runmake
}

do_compile_ptest () {
	oe_runmake buildtest
}

do_install () {
        install -d ${D}${libdir}
        install -d ${D}${bindir}
        install -d ${D}${includedir}/openssl-1.0.2o

        oe_runmake INSTALL_PREFIX="${D}" install_sw
        # On x86_64, move lib/* to lib64
        if [ "${libdir}" != "${prefix}/lib" ]
        then
                install -d ${D}${libdir} ${D}${libdir}/pkgconfig
                mv ${D}${prefix}/lib/lib* ${D}${libdir}
                mv ${D}${prefix}/lib/pkgconfig/*.pc ${D}${libdir}/pkgconfig
        fi

        oe_libinstall -so libcrypto ${D}${libdir}
        oe_libinstall -so libssl ${D}${libdir}
        install -m 0664 ${S}/e_os.h ${D}${includedir}/openssl
        sed -i '/^includedir=/ s%$%\/openssl-1.0.2o%' ${D}/${libdir}/pkgconfig/*.pc
        sed -i s%\-lcrypto%\-lcrypto-1.0.2o% ${D}/${libdir}/pkgconfig/*.pc
        sed -i s%\-lssl%\-lssl-1.0.2o% ${D}/${libdir}/pkgconfig/*.pc
        mv ${D}${includedir}/openssl ${D}${includedir}/openssl-1.0.2o
        mv ${D}${bindir}/openssl ${D}${bindir}/openssl-1.0.2o
        mv ${D}${libdir}/libssl.so ${D}${libdir}/libssl-1.0.2o.so
        mv ${D}${libdir}/libcrypto.so ${D}${libdir}/libcrypto-1.0.2o.so
        mv ${D}${libdir}/libssl.a ${D}${libdir}/libssl-1.0.2o.a
        mv ${D}${libdir}/libcrypto.a ${D}${libdir}/libcrypto-1.0.2o.a
        mv ${D}${libdir}/pkgconfig/libcrypto.pc ${D}${libdir}/pkgconfig/libcrypto-1.0.2o.pc
        mv ${D}${libdir}/pkgconfig/libssl.pc ${D}${libdir}/pkgconfig/libssl-1.0.2o.pc
        mv ${D}${libdir}/pkgconfig/openssl.pc ${D}${libdir}/pkgconfig/openssl-1.0.2o.pc
        rm -rf ${D}${libdir}/ssl ${D}${libdir}/engines
}

do_install_ptest () {
	cp -r -L Makefile.org Makefile test ${D}${PTEST_PATH}
	cp Configure config e_os.h ${D}${PTEST_PATH}
	cp -r -L include ${D}${PTEST_PATH}
	ln -sf ${base_libdir}/libcrypto.a ${D}${PTEST_PATH}
	ln -sf ${libdir}/libssl.a ${D}${PTEST_PATH}
	mkdir -p ${D}${PTEST_PATH}/crypto
	cp crypto/constant_time_locl.h ${D}${PTEST_PATH}/crypto
	cp -r certs ${D}${PTEST_PATH}
	mkdir -p ${D}${PTEST_PATH}/apps
	ln -sf ${libdir}/ssl/misc/CA.sh  ${D}${PTEST_PATH}/apps
	ln -sf ${sysconfdir}/ssl/openssl.cnf ${D}${PTEST_PATH}/apps
	ln -sf ${bindir}/openssl         ${D}${PTEST_PATH}/apps
	cp apps/server2.pem             ${D}${PTEST_PATH}/apps
	mkdir -p ${D}${PTEST_PATH}/util
	install util/opensslwrap.sh    ${D}${PTEST_PATH}/util
	install util/shlib_wrap.sh     ${D}${PTEST_PATH}/util
}

do_install:append_class-native() {
	create_wrapper ${D}${bindir}/openssl \
	    OPENSSL_CONF=${libdir}/ssl/openssl.cnf \
	    SSL_CERT_DIR=${libdir}/ssl/certs \
	    SSL_CERT_FILE=${libdir}/ssl/cert.pem \
	    OPENSSL_ENGINES=${libdir}/ssl/engines
}

BBCLASSEXTEND = "native nativesdk"
