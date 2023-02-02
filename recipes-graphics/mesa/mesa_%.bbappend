FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}-${PV}:"

PROVIDES:remove = "virtual/libgl virtual/libgles1 virtual/libgles2 virtual/egl virtual/mesa"
