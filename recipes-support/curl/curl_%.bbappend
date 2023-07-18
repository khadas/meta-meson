PACKAGECONFIG:remove = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-2k', ' gnutls', '', d)}"
PACKAGECONFIG:append = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-2k', ' openssl', '', d)}"

