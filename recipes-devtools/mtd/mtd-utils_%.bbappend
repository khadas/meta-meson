FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

FILES:${PN}-staticdev += "ubi-utils/libubi.a ${libdir}/*.a"

ALTERNATIVE_${PN}-ubifs = "ubiattach ubidetach ubimkvol"
FILES_mtd-utils-ubifs = "${sbindir}/ubiattach ${sbindir}/ubidetach ${sbindir}/ubiformat ${sbindir}/ubimkvol ${sbindir}/ubiblock"

do_install:append () {
	install -d ${D}${includedir}/mtd/
	install -d ${D}${libdir}/
	install -m 0644 ${S}/include/libubi.h ${D}${includedir}
	install -m 0644 ${S}/include/libmtd.h ${D}${includedir}
	install -m 0644 ${S}/include/libscan.h ${D}${includedir}
	install -m 0644 ${S}/include/libubigen.h ${D}${includedir}
	ln -s ../libubi.h ${D}${includedir}/mtd/libubi.h
	ln -s ../libmtd.h ${D}${includedir}/mtd/libmtd.h
	ln -s ../libscan.h ${D}${includedir}/mtd/libscan.h
	ln -s ../libubigen.h ${D}${includedir}/mtd/libubigen.h
	oe_libinstall -a libubi ${D}${libdir}/
	oe_libinstall -a libmtd ${D}${libdir}/
}

BBCLASSEXTEND += "native"
