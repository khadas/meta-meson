PACKAGECONFIG:remove = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', ' libkms intel radeon amdgpu nouveau omap freedreno etnaviv', '', d)}"
