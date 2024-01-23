#yocto-bsp want to use sshfs, so add openssh into rootfs.
#But openssh recipe add rng-tools via PACKAGECONFIG by default.
#rng-tools recipe install rngd.service which will launch systemd-udev-settle.service,
#systemd-udev-settle.service is deprecated, if bluez is launched by udev(CL: https://scgit.amlogic.com/#/c/385944/),
#system stuck for about 3 minutes during booting up
#rng-tool is not necessary to sshfs, so remove it
PACKAGECONFIG:remove = "rng-tools"
