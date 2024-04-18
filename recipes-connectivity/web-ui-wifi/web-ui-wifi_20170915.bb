SUMMARY = "aml web-ui-wifi"
LICENSE = "CLOSED"

#S = "${WORKDIR}"
S= "${WORKDIR}/git/web-ui-wifi"

#  SRC_URI += " 
#     file://cgi-bin 
#     file://css 
#     file://fonts 
#     file://html 
#     file://images 
#     file://js 
# "

do_install () {
    install -d ${D}/var/www
    cp -rf ${S}/html/* ${D}/var/www
    cp -rf ${S}/css ${D}/var/www
    cp -rf ${S}/fonts ${D}/var/www
    cp -rf ${S}/images ${D}/var/www
    cp -rf ${S}/js ${D}/var/www

    install -d ${D}/var/www/cgi-bin
    cp -rf ${S}/cgi-bin/scripts/* ${D}/var/www/cgi-bin
    install -m 0755 ${S}/cgi-bin/soundbar.cgi ${D}/var/www/cgi-bin
    if [ ${TARGET_ARCH} = arm ]; then
        install -m 0755 ${S}/cgi-bin/main32.cgi ${D}/var/www/cgi-bin/main.cgi
    else
        install -m 0755 ${S}/cgi-bin/main64.cgi ${D}/var/www/cgi-bin/main.cgi
    fi
}

FILES:${PN} += "/var/www/*"
