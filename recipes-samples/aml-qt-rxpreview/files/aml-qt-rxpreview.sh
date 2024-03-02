#!/bin/sh

case "$1" in
    stop)
	killall aml-qt-rxpreview
	;;

    restart)
	killall aml-qt-rxpreview
	export XDG_RUNTIME_DIR=/run/user/0
	/usr/bin/aml-qt-rxpreview --platform wayland &
	;;

    start)
    export XDG_RUNTIME_DIR=/run/user/0
	/usr/bin/aml-qt-rxpreview --platform wayland &
    ;;
esac

exit $?
