PACKAGECONFIG:append = "asfdemux realmedia"

PACKAGECONFIG[asfdemux] = "-Dasfdemux=enabled,-Dasfdemux=disabled,libasfdemux"
PACKAGECONFIG[realmedia] = "-Drealmedia=enabled,-Drealmedia=disabled,librealmedia"

PACKAGECONFIG:remove ="a52dec asfdemux realmedia"
