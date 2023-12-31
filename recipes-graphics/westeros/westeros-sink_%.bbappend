DEPENDS +="${@bb.utils.contains('DISTRO_FEATURES', 'appmanager', 'aml-appmanager', '', d)}"
