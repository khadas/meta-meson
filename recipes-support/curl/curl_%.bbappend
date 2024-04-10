PACKAGECONFIG:remove = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', ' gnutls', '', d)}"
PACKAGECONFIG:append = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', ' openssl', '', d)}"

