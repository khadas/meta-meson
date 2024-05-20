if ENABLE_AML
bin_PROGRAMS = bt-halplay
bt_halplay_SOURCES = \
       ../../src/shared/a2dp-codecs.c \
       ../../src/shared/dbus-client.c \
       ../../src/shared/ffb.c \
       ../../src/shared/log.c \
       ../aplay/alsa-mixer.c \
       ../aplay/alsa-pcm.c \
       ../aplay/dbus.c \
       bt-halplay.c

bt_halplay_CFLAGS = \
       -I$(top_srcdir)/src \
       -laudio_client \
       @ALSA_CFLAGS@ \
       @BLUEZ_CFLAGS@ \
       @DBUS1_CFLAGS@ \
       @LIBUNWIND_CFLAGS@

bt_halplay_LDADD = \
       @ALSA_LIBS@ \
       @BLUEZ_LIBS@ \
       @DBUS1_LIBS@ \
       @LIBUNWIND_LIBS@


lib_LTLIBRARIES = liba2dp_ctl.la

liba2dp_ctl_la_SOURCES = \
       ../../src/shared/a2dp-codecs.c \
       ../../src/shared/dbus-client.c \
       ../../src/shared/hex.c \
       ../../src/shared/log.c \
       lib_a2dp_ctl.c

liba2dp_ctl_la_CFLAGS = \
       -I$(top_srcdir)/src \
       @ALSA_CFLAGS@ \
       @DBUS1_CFLAGS@ \
       @BLUEZ_CFLAGS@ \
       @GIO2_CFLAGS@ \
       @GLIB2_CFLAGS@ \
       @LIBUNWIND_CFLAGS@

#AM_LDFLAGS = -module -avoid-version

liba2dp_ctl_la_LIBADD = \
       @ALSA_LIBS@ \
       @DBUS1_LIBS@ \
       @GIO2_LIBS@ \
       @GLIB2_LIBS@ \
       @LIBUNWIND_CFLAGS@

liba2dp_ctldir = ${includedir}
liba2dp_ctl_HEADERS = a2dp_ctl.h

endif
