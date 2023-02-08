SUMMARY = "Graphics abstraction library for the Linux Framebuffer Device"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "\
            file://0006-add-amlgfx-for-directfb.patch \
            file://0007-fix-amlgfx-blend-op-error-issue.patch \
            file://0008-fix-osd-pan-display-wait-vsync-too-long-issue.patch \
            file://0009-add-amlgfx-DSBLIT_BLEND_COLORALPHA.patch \
            file://0010-fix-amlgfx-dfdok-show.patch \
            file://0011-fix-sizeof-config_para_ex-is-not-equal-to-driver.patch \
            file://directfbrc \
            "

PACKAGECONFIG += "drmkms"

EXTRA_OECONF_remove ="--with-gfxdrivers=none"

EXTRA_OECONF += "--with-gfxdrivers=amlgfx \
                 --with-inputdrivers=linuxinput,ps2mouse,serialmouse \
                 --enable-debug-support \
                 --enable-debug \
                 --enable-trace \
                 --with-tests \
                 "

do_install_append() {
    install -d ${D}/etc/
    install -D -m 0644 ${WORKDIR}/directfbrc ${D}/etc/directfbrc
    if ${@bb.utils.contains('DISTRO_FEATURES', 'zapper-reference', 'true', 'false', d)}
    then
        sed -i "s/1920x1080/1280x720/g" ${D}/etc/directfbrc
        sed -i "s/#no-cursor/no-cursor/g" ${D}/etc/directfbrc
        sed -i '$a\no-vt-switch\nvt-num=2' ${D}/etc/directfbrc
    fi

}


FILES_${PN}-dev += "${libdir}/directfb-${RV}/gfxdrivers/*.la"
FILES_${PN} += "${libdir}/directfb-${RV}/gfxdrivers/*.so /etc/*"
