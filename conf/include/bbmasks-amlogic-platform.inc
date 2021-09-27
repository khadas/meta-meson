BBMASK += "meta-meson/recipes-graphics/waffle/waffle_1.5.2.bbappend"
BBMASK += "meta-meson/recipes-multimedia/gstreamer/gst-player_git.bbappend"
# MASK out this for error "not a BitBake file"
BBMASK .= "|meta-security/recipes-core/busybox"
# Don't need kernel patch for dm-verity(enabled by default)
BBMASK .= "|meta-security/recipes-kernel/linux/"
