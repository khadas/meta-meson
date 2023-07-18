PACKAGECONFIG::remove = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper-2k', ' libkms intel radeon amdgpu nouveau omap freedreno etnaviv', '', d)}"
