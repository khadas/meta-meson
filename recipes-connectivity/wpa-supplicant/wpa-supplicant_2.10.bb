SUMMARY = "Client for Wi-Fi Protected Access (WPA)"
HOMEPAGE = "http://w1.fi/wpa_supplicant/"
DESCRIPTION = "wpa_supplicant is a WPA Supplicant for Linux, BSD, Mac OS X, and Windows with support for WPA and WPA2 (IEEE 802.11i / RSN). Supplicant is the IEEE 802.1X/WPA component that is used in the client stations. It implements key negotiation with a WPA Authenticator and it controls the roaming and IEEE 802.11 authentication/association of the wlan driver."
BUGTRACKER = "http://w1.fi/security/"
SECTION = "network"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=5ebcb90236d1ad640558c3d3cd3035df \
                    file://README;beginline=1;endline=56;md5=e3d2f6c2948991e37c1ca4960de84747 \
                    file://wpa_supplicant/wpa_supplicant.c;beginline=1;endline=12;md5=76306a95306fee9a976b0ac1be70f705"

DEPENDS = "dbus libnl"
RRECOMMENDS_${PN} = "wpa-supplicant-passphrase wpa-supplicant-cli"

PACKAGECONFIG ??= "gnutls"
PACKAGECONFIG[gnutls] = ",,gnutls libgcrypt"
PACKAGECONFIG[openssl] = ",,openssl"
inherit pkgconfig systemd
SRC_URI = "http://w1.fi/releases/wpa_supplicant-${PV}.tar.gz \
           file://wpa-supplicant.sh \
           file://wpa_supplicant.conf \
           file://wpa_supplicant.conf-sane \
           file://99_wpa_supplicant \
           file://0001-build-Re-enable-options-for-libwpa_client.so-and-wpa.patch \
           file://0002-Fix-removal-of-wpa_passphrase-on-make-clean.patch \
           file://0001-Install-wpa_passphrase-when-not-disabled.patch \
           "
SRC_URI[sha256sum] = "20df7ae5154b3830355f8ab4269123a87affdea59fe74fe9292a91d0d7e17b2f"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG_remove = "gnutls"
PACKAGECONFIG_append = " openssl"


inherit systemd

SRC_URI += " file://createDefaultWPASupplicantConfigFile.sh"
SRC_URI += " file://wpa_supplicant.service"

CVE_PRODUCT = "wpa_supplicant"
S = "${WORKDIR}/wpa_supplicant-${PV}"







EXTRA_OEMAKE = "'LIBDIR=${libdir}' 'INCDIR=${includedir}' 'BINDIR=${sbindir}'"

PACKAGES_prepend = "wpa-supplicant-passphrase wpa-supplicant-cli "
FILES:${PN}-passphrase = "${sbindir}/wpa_passphrase"
FILES:${PN}-cli = "${sbindir}/wpa_cli"
FILES:${PN}-lib = "${libdir}/libwpa_client*${SOLIBSDEV}"
FILES:${PN} += "${datadir}/dbus-1/system-services/* ${systemd_system_unitdir}/*"
FILES:${PN}-dbg += "${sbindir}/.debug ${libdir}/.debug"
do_configure () {
	${MAKE} -C wpa_supplicant clean
	sed -e '/^CONFIG_TLS=/d' <wpa_supplicant/defconfig >wpa_supplicant/.config

	if ${@ bb.utils.contains('PACKAGECONFIG', 'openssl', 'true', 'false', d) }; then
		echo 'CONFIG_TLS=openssl' >>wpa_supplicant/.config
	elif ${@ bb.utils.contains('PACKAGECONFIG', 'gnutls', 'true', 'false', d) }; then
		echo 'CONFIG_TLS=gnutls' >>wpa_supplicant/.config
        sed -i -e 's/\(^CONFIG_DPP=\)/#\1/' \
               -e 's/\(^CONFIG_EAP_PWD=\)/#\1/' \
               -e 's/\(^CONFIG_SAE=\)/#\1/' wpa_supplicant/.config
	fi

	# For rebuild
	rm -f wpa_supplicant/*.d wpa_supplicant/dbus/*.d

    # Add the "-fPIC" option to CFLAGS to allow the Pace WiFi HAL module to link against wpa-supplicant
    echo "CFLAGS += -fPIC" >> wpa_supplicant/.config

    echo "CONFIG_BUILD_WPA_CLIENT_SO=y" >> wpa_supplicant/.config

    if grep -q '\bCONFIG_DEBUG_FILE\b' wpa_supplicant/.config; then
       sed -i -e '/\bCONFIG_DEBUG_FILE\b/s/.*/CONFIG_DEBUG_FILE=y/' wpa_supplicant/.config
    else
       echo "CONFIG_DEBUG_FILE=y" >> wpa_supplicant/.config
    fi

    sed -i -- 's/CONFIG_AP=y/\#CONFIG_AP=y/' wpa_supplicant/.config
    sed -i -- 's/CONFIG_DRIVER_HOSTAP=y/\#CONFIG_DRIVER_HOSTAPAP=y/' wpa_supplicant/.config
}

do_compile () {
	oe_runmake -C wpa_supplicant
	if [ -z "${DISABLE_STATIC}" ]; then
		oe_runmake -C wpa_supplicant libwpa_client.a
	fi
}

do_install () {
	oe_runmake -C wpa_supplicant DESTDIR="${D}" install

	install -d ${D}${docdir}/wpa_supplicant
	install -m 644 wpa_supplicant/README ${WORKDIR}/wpa_supplicant.conf ${D}${docdir}/wpa_supplicant

	install -d ${D}${sysconfdir}
	install -m 600 ${WORKDIR}/wpa_supplicant.conf-sane ${D}${sysconfdir}/wpa_supplicant.conf

	install -d ${D}${sysconfdir}/network/if-pre-up.d/
	install -d ${D}${sysconfdir}/network/if-post-down.d/
	install -d ${D}${sysconfdir}/network/if-down.d/
	install -m 755 ${WORKDIR}/wpa-supplicant.sh ${D}${sysconfdir}/network/if-pre-up.d/wpa-supplicant
	ln -sf ../if-pre-up.d/wpa-supplicant ${D}${sysconfdir}/network/if-post-down.d/wpa-supplicant

	install -d ${D}/${sysconfdir}/dbus-1/system.d
	install -m 644 ${S}/wpa_supplicant/dbus/dbus-wpa_supplicant.conf ${D}/${sysconfdir}/dbus-1/system.d
	install -d ${D}/${datadir}/dbus-1/system-services
	install -m 644 ${S}/wpa_supplicant/dbus/*.service ${D}/${datadir}/dbus-1/system-services

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}/${systemd_system_unitdir}
		install -m 644 ${S}/wpa_supplicant/systemd/*.service ${D}/${systemd_system_unitdir}
	fi

	install -d ${D}/etc/default/volatiles
	install -m 0644 ${WORKDIR}/99_wpa_supplicant ${D}/etc/default/volatiles

	install -d ${D}${includedir}
	install -m 0644 ${S}/src/common/wpa_ctrl.h ${D}${includedir}

	if [ -z "${DISABLE_STATIC}" ]; then
		install -d ${D}${libdir}
		install -m 0644 wpa_supplicant/libwpa_client.a ${D}${libdir}
	fi
}

pkg_postinst:${PN} () {
	# If we're offline, we don't need to do this.
	if [ "x$D" = "x" ]; then
		killall -q -HUP dbus-daemon || true
	fi
}


NOAUTOPACKAGEDEBUG = "1"



CONFFILES:${PN} += "${sysconfdir}/wpa_supplicant.conf"


SYSTEMD_SERVICE:${PN} = "wpa_supplicant.service"
SYSTEMD_AUTO_ENABLE = "disable"

python split_wpa_supplicant_libs () {
    libdir = d.expand('${libdir}/wpa_supplicant')
    dbglibdir = os.path.join(libdir, '.debug')

    split_packages = do_split_packages(d, libdir, r'^(.*)\.so', '${PN}-plugin-%s', 'wpa_supplicant %s plugin', prepend=True)
    split_dbg_packages = do_split_packages(d, dbglibdir, r'^(.*)\.so', '${PN}-plugin-%s-dbg', 'wpa_supplicant %s plugin - Debugging files', prepend=True, extra_depends='${PN}-dbg')

    if split_packages:
        pn = d.getVar('PN')
        d.appendVar('RRECOMMENDS:' + pn + '-dbg', ' ' + ' '.join(split_dbg_packages))
}
