UMMARY = "amlogic mediactrl source reference"
LICENSE = "CLOSED"

#SRCREV ?="${AUTOREV}"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES','mediactrlsrc-cam','isp','',d)}"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES','mediactrlsrc-hdmi','aml-tvserver','',d)}"

EXTRA_OEMAKE = "OUT_DIR=${B} TARGET_DIR=${D} STAGING_DIR=${STAGING_DIR_TARGET} DESTDIR=${D} EXTRA_CFLAGS+='-Wno-unused-result -Wno-unused-variable -Wno-unused-function -Os'"

CAMERA_ENABLE = "${@bb.utils.contains('DISTRO_FEATURES','mediactrlsrc-cam','true','false',d)}"
HDMIRX_ENABLE = "${@bb.utils.contains('DISTRO_FEATURES','mediactrlsrc-hdmi','true','false',d)}"

do_configure[noexec] = "1"
do_package_qa[noexec] = "1"

do_compile() {
	echo "CAMERA_ENABLE is ${CAMERA_ENABLE}"
	echo "HDMIRX_ENABLE is ${HDMIRX_ENABLE}"
	if [ ${CAMERA_ENABLE} = true ]; then
		echo "mediactrlsrc-cam is enabled $(date)"
	fi

	if [ ${HDMIRX_ENABLE} = true ]; then
		echo "mediactrlsrc-hdmi is enabled $(date)"
	fi

    oe_runmake --eval='AMLSRC_MEDIACTRL_CAM=${CAMERA_ENABLE}' --eval='AMLSRC_MEDIACTRL_HDMI=${HDMIRX_ENABLE}' -C ${S} ${EXTRA_OEMAKE}
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${includedir}

    install -D -m 0644 ${B}/libamlv4l2src.so ${D}${libdir}/libamlv4l2src.so
    install -D -m 0644 ${B}/libamlsrc.so ${D}${libdir}/libamlsrc.so

	if [ ${CAMERA_ENABLE} = true ]; then
		install -D -m 0755 ${B}/camctrl ${D}${bindir}/camctrl
		install -D -m 0644 ${B}/libcamsrc.so ${D}${libdir}/libcamsrc.so
	fi

	if [ ${HDMIRX_ENABLE} = true ]; then
		install -D -m 0644 ${B}/libhdmisrc.so ${D}${libdir}/libhdmisrc.so
		install -D -m 0755 ${B}/hdmictrl ${D}${bindir}/hdmictrl
	fi

    install -D -m 0644 ${S}/amlv4l2src/amlv4l2src.h ${D}${includedir}
    install -D -m 0644 ${S}/include/*.h ${D}${includedir}
}

do_cleanup() {
    oe_runmake -C ${S} clean
}

addtask do_cleanup before do_cleansstate after do_clean


FILES:${PN} = "${bindir}/* ${libdir}/*"
FILES:${PN}-dev = "${includedir}/*"
