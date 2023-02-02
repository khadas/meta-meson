do_install:prepend() {
	if [ -z `fgrep '\-\-autologin root' ${WORKDIR}/serial-getty\@.service` ]
	then
		sed -i -e '/^ExecStart=/ s/$/ --autologin root/' ${WORKDIR}/serial-getty\@.service
	fi
}
