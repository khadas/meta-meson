SUMMARY="Image updater for Yocto projects"
DESCRIPTION = "Application for automatic software update"
SECTION="swupdate"
DEPENDS += "aml-ubootenv aml-bootloader-message libconfig"

# SWUpdate licensing is described in the following pages:
# https://sbabic.github.io/swupdate/licensing.html
# rst form: file://doc/source/licensing.rst
LICENSE = "GPLv2+ & LGPLv2+ & MIT"
LICENSE_${PN}-lua = "LGPLv2+"
LICENSE_${PN}-www = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/GPL-2.0-only.txt;md5=4ee23c52855c222cba72583d301d2338 \
                    file://LICENSES/LGPL-2.1-or-later.txt;md5=4fbd65380cdd255951079008b364516c \
                    file://LICENSES/MIT.txt;md5=838c366f69b72c5df05c96dff79b35f2 \
                    file://LICENSES/BSD-3-Clause.txt;md5=4a1190eac56a9db675d58ebe86eaf50c"

SRCREV ?= "8ca165e135cff8656dc570fd83ab7fd884f9bece"
PV ?= "2021.04+git${SRCPV}"

inherit cml1 systemd pkgconfig

SRC_URI = "git://github.com/sbabic/swupdate.git;protocol=https \
        file://0001-amlogic-update-based-on-2021.04.patch \
        file://defconfig \
        file://hwrevision \
        file://sw-versions \
        file://swupdate-public.pem \
        file://swupdate.sh \
        file://swupdate.service \
"

LTOEXTRA += "-flto-partition=none"

PACKAGES =+ " \
    ${PN}-client \
    ${PN}-lua \
    ${PN}-progress \
    ${PN}-tools \
    ${PN}-tools-hawkbit \
    ${PN}-www \
"
INSANE_SKIP_${PN}-lua = "dev-so"
wwwdir ?= "/var/www/swupdate"

# tools is now an empty meta package for backward compatibility
ALLOW_EMPTY_${PN}-tools = "1"

FILES_${PN}-client = "${bindir}/swupdate-client"
FILES_${PN}-lua += "${libdir}/lua/"
FILES_${PN}-progress = " \
    ${bindir}/swupdate-progress \
"
FILES_${PN}-tools-hawkbit = " \
    ${bindir}/swupdate-hawkbitcfg \
    ${bindir}/swupdate-sendtohawkbit \
"
FILES_${PN}-www = " \
    ${libdir}/swupdate/conf.d/*mongoose* \
    ${wwwdir}/* \
"

# The tools package is deprecated, it is an empty meta package for backward compatibility
RDEPENDS_${PN}-tools += "${PN}-client ${PN}-progress ${PN}-tools-hawkbit"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

SYSTEMD_AUTO_ENABLE = "enable"

EXTRA_OEMAKE += " O=${B} HOSTCC="${BUILD_CC}" HOSTCXX="${BUILD_CXX}" LD="${CC}" DESTDIR="${D}" LIBDIR="${libdir}" V=1 ARCH=${TARGET_ARCH} CROSS_COMPILE=${TARGET_PREFIX} SKIP_STRIP=y"

DEPENDS += "kern-tools-native"

# returns all the elements from the src uri that are .cfg files
def find_cfgs(d):
    return [s for s in src_patches(d, True) if s.endswith('.cfg')]

python () {
    import re

    try:
        defconfig = bb.fetch2.localpath('file://defconfig', d)
    except bb.fetch2.FetchError:
        return

    try:
        configfile = open(defconfig)
    except IOError:
        return

    features = configfile.read()
    configfile.close()

    for current_fragment in find_cfgs(d):
        try:
            fragment_fd = open(current_fragment)
        except IOError:
            continue

        fragment = fragment_fd.read()
        fragment_fd.close()

        fragment_search = re.findall('^(?:# )?(CONFIG_[a-zA-Z0-9_]*)[= ].*\n?', fragment, re.MULTILINE)

        for feature in fragment_search:
            features = re.sub("^(?:# )?" + feature + "[= ].*\n?", "", features, flags=re.MULTILINE)

        features = features + fragment

    features = features.splitlines(True)


    depends = d.getVar('DEPENDS', False)

    if 'CONFIG_REMOTE_HANDLER=y\n' in features:
        depends += ' zeromq'

    if 'CONFIG_SSL_IMPL_OPENSSL=y\n' in features:
        depends += ' openssl'
    elif 'CONFIG_SSL_IMPL_MBEDTLS=y\n' in features:
        depends += ' mbedtls'
    elif 'CONFIG_SSL_IMPL_WOLFSSL=y\n' in features:
        depends += ' wolfssl'

    if 'CONFIG_JSON=y\n' in features:
        depends += ' json-c'

    if 'CONFIG_SYSTEMD=y\n' in features:
        depends += ' systemd'

    if 'CONFIG_ARCHIVE=y\n' in features:
        depends += ' libarchive'

    if 'CONFIG_LUA=y\n' in features:
        depends += ' lua'

    if 'CONFIG_GUNZIP=y\n' in features:
        depends += ' gzip'

    if 'CONFIG_DOWNLOAD=y\n' in features or 'CONFIG_SURICATTA=y\n' in features:
        depends += ' curl'

    if 'CONFIG_MTD=y\n' in features or 'CONFIG_CFI=y\n' in features or 'CONFIG_UBIVOL=y\n' in features:
        depends += ' mtd-utils'

    if 'CONFIG_UCFWHANDLER=y\n' in features:
        depends += ' libgpiod'

    if 'CONFIG_SWUFORWARDER_HANDLER=y\n' in features:
        depends += ' curl libwebsockets uriparser'

    if 'CONFIG_RDIFFHANDLER=y\n' in features:
        depends += ' librsync'

    if 'CONFIG_BOOTLOADER_EBG=y\n' in features:
        depends += ' efibootguard'

    if 'CONFIG_ZSTD=y\n' in features:
        depends += ' zstd'

    if 'CONFIG_DISKPART=y\n' in features:
        depends += ' util-linux e2fsprogs'

    d.setVar('DEPENDS', depends)

    if 'CONFIG_MONGOOSE=y\n' in features:
        d.setVar('SWUPDATE_MONGOOSE', 'true')
    else:
        d.setVar('SWUPDATE_MONGOOSE', 'false')

    if 'CONFIG_MONGOOSE_WEB_API_V2=y\n' in features:
        d.setVar('SWUPDATE_WWW', 'webapp')

    # Values not used here might be used in a bbappend
    d.setVar('SWUPDATE_SOCKET_CTRL_PATH', '/tmp/sockinstctrl')
    d.setVar('SWUPDATE_SOCKET_PROGRESS_PATH', '/tmp/swupdateprog')
    d.setVar('SWUPDATE_HW_COMPATIBILITY_FILE', '/etc/hwrevision')
    d.setVar('SWUPDATE_SW_VERSIONS_FILE', '/etc/sw-versions')
    for feature in features:
        if feature.startswith('CONFIG_SOCKET_CTRL_PATH='):
            ctrl_path = feature.split('=')[1].strip()
            d.setVar('SWUPDATE_SOCKET_CTRL_PATH', ctrl_path)
        elif feature.startswith('CONFIG_SOCKET_PROGRESS_PATH='):
            prog_path = feature.split('=')[1].strip()
            d.setVar('SWUPDATE_SOCKET_PROGRESS_PATH', prog_path)
        elif feature.startswith('CONFIG_HW_COMPATIBILITY_FILE='):
            hwrev_file = feature.split('=')[1].strip()
            d.setVar('SWUPDATE_HW_COMPATIBILITY_FILE', hwrev_file)
        elif feature.startswith('CONFIG_SW_VERSIONS_FILE='):
            swver_file = feature.split('=')[1].strip()
            d.setVar('SWUPDATE_SW_VERSIONS_FILE', swver_file)
}

do_configure () {
    cat > ${WORKDIR}/.config <<HEREDOC
CONFIG_EXTRA_CFLAGS="${CFLAGS}"
CONFIG_EXTRA_LDFLAGS="${LDFLAGS}"
HEREDOC
    cat ${WORKDIR}/defconfig >> ${WORKDIR}/.config

    merge_config.sh -O ${B} -m ${WORKDIR}/.config ${@" ".join(find_cfgs(d))}
    (cd ${S} && cml1_do_configure)
}

do_compile() {
    unset LDFLAGS
    (cd ${S} && oe_runmake)
}

do_install () {
    (cd ${S} && oe_runmake install)

    install -m 0755 -d ${D}${wwwdir}
    if [ -d ${S}/web-app ];then
        cp -R --no-dereference --preserve=mode,links -v ${S}/examples/www/v2/* ${D}${wwwdir}
    else
        install -m 0755 ${S}/www/* ${D}${wwwdir}
    fi

    install -d ${D}/etc
    install -m 0644 ${WORKDIR}/hwrevision ${D}/etc
    install -m 0644 ${WORKDIR}/sw-versions ${D}/etc
    install -m 0644 ${WORKDIR}/swupdate-public.pem ${D}/etc

    mkdir -p ${D}${bindir}
    install -m 0755 ${WORKDIR}/swupdate.sh ${D}/${bindir}

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/swupdate.service ${D}/${systemd_unitdir}/system
}

SYSTEMD_PACKAGES = "${PN} ${PN}-progress"
SYSTEMD_SERVICE_${PN} = "swupdate.service"