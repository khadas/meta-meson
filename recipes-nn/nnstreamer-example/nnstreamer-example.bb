SUMMARY = "NNStreamer-Examlple"
DESCRIPTION = "Example applications of nnstreamer. Note that we have to enable the 'apptest' CI module in the near future."
SECTION = "AI"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "\
                file://LICENSE;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                file://debian/copyright;md5=0462ef8fa89a1f53f2e65e74940519ef \
                "

DEPENDS = "\
            orc-native \
            glib-2.0 \
            gstreamer1.0 \
            gstreamer1.0-plugins-base \
            python3 \
            python3-numpy \
            gtest \
            json-glib \
            "
DEPENDS += "\
        ${@bb.utils.contains('DISTRO_FEATURES','tensorflow-lite','tensorflow-lite','',d)} \
        "
SRC_URI = "\
        git://github.com/nnstreamer/nnstreamer-example.git;branch=main;protocol=https \
        "

SRCREV = "${AUTOREV}" 

S = "${WORKDIR}/git"

inherit meson pkgconfig


PACKAGECONFIG ??= "\
                ${@bb.utils.contains('DISTRO_FEATURES','opencv','opencv','',d)} \
                ${@bb.utils.contains('DISTRO_FEATURES','tensorflow','tensorflow','',d)} \
                "


INSANE_SKIP:${PN} += "dev-so"

FILES:${PN} += "\
            ${libdir}/*.so \
            ${libdir}/gstreamer-1.0/*.so \
            "



PACKAGES =+ "\
            ${@bb.utils.contains('DISTRO_FEATURES','tensorflow-lite', \
                    '${PN}-tensorflow-lite', \
                    '', d)} \
            "


FILES:${PN}-tensorflow-lite += "\
                                ${@bb.utils.contains('DISTRO_FEATURES','tensorflow-lite', \
                                    '${libdir}/nnstreamer/filters/libnnstreamer_filter_tensorflow2-lite.so', \
                                    '', d)} \
                                "
RPROVIDES:${PN}-tensorflow-lite = "${libdir}/nnstreamer/filters/libnnstreamer_filter_tensorflow2-lite.so"



RDEPENDS:${PN} = "\
                glib-2.0 \
                json-glib \
                gstreamer1.0 \
                gstreamer1.0-plugins-base \
                python3 \
                python3-numpy \
                python3-math \
                "

                
