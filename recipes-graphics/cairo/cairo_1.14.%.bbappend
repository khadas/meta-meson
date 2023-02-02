FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Only apply this workaround for dvalin and gondul GPUs
# Once arm has fixed shader garbage issue in mali side, this patch can be removed
WORK_AROUND = "${@bb.utils.contains('DISTRO_FEATURES', 'zapper', '', ' file://0006-Let-Cairo-not-use-GL_BGRA-on-cairogl_surface_map_to_.patch', d)}"
SRC_URI += "${WORK_AROUND}"

