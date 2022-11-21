PACKAGECONFIG_append = "asfdemux realmedia"

PACKAGECONFIG[asfdemux] = "-Dasfdemux=enabled,-Dasfdemux=disabled,libasfdemux"
PACKAGECONFIG[realmedia] = "-Drealmedia=enabled,-Drealmedia=disabled,librealmedia"

PACKAGECONFIG_remove ="a52dec asfdemux realmedia"
