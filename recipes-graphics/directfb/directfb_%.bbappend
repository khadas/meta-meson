SUMMARY = "Graphics abstraction library for the Linux Framebuffer Device"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "\
            file://0002-Extend-Customized-Key.patch \
            file://0006-add-amlgfx-for-directfb.patch \
            file://0007-fix-amlgfx-blend-op-error-issue.patch \
            file://0009-add-amlgfx-DSBLIT_BLEND_COLORALPHA.patch \
            file://0010-fix-amlgfx-dfdok-show.patch \
            file://0011-fix-sizeof-config_para_ex-is-not-equal-to-driver.patch \
            file://0012-fix-the-issue-that-rectangle-area-is-incorrect.patch \
            file://0013-add-Blit2-support.patch \
            file://0014-fix-the-issue-that-DSPF_LUT8-cannot-work.patch \
            file://directfbrc \
            "

# If disable-debug-support, MODULEDIRNAME will be set to directfb-$BINARY_VERSION-pure in
# directfb configure which will not be installed to the final package, add a patch to sovle this problem
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-2k', ' file://0005-configure-change-MODULEDIRNAME-when-disable-debug-support.patch', '', d)}"

PACKAGECONFIG:remove = "linuxinput"

EXTRA_OECONF:remove ="--with-gfxdrivers=none"

# Disable these configuration for zapper 2k optimization
DISABLED_CONFIG = "--disable-network \
                   --disable-multicore \
                   --disable-multi-kernel \
                   --disable-video4linux \
                   --disable-gif \
                   --disable-debug-support \
                   --without-tests \
                   --without-tools \
                  "

EXTRA_OECONF += "--with-gfxdrivers=amlgfx \
                 --with-inputdrivers=linuxinput,ps2mouse,serialmouse \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'zapper-2k', '${DISABLED_CONFIG}', '', d)} \
                "

do_install:append() {
    install -d ${D}/etc/
    install -D -m 0644 ${WORKDIR}/directfbrc ${D}/etc/directfbrc
    if ${@bb.utils.contains('DISTRO_FEATURES', 'zapper-2k', 'true', 'false', d)}
    then
        sed -i "s/1920x1080/1280x720/g" ${D}/etc/directfbrc
        sed -i "s/#no-cursor/no-cursor/g" ${D}/etc/directfbrc
        sed -i '$a\no-vt-switch\nvt-num=2' ${D}/etc/directfbrc
    fi

}


FILES:${PN}-dev += "${libdir}/directfb-${RV}/gfxdrivers/*.la"
FILES:${PN} += "${libdir}/directfb-${RV}/gfxdrivers/*.so /etc/*"
