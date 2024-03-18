SUMMARY = "amlogic playready"
LICENSE = "CLOSED"
DEPENDS = "optee-userspace bzip2 libxml2 aml-secmem aml-mediahal-sdk curl"
RDEPENDS:${PN} = "libbz2 curl"

FILESEXTRAPATHS:preppend := "${THISDIR}/files/:"

#SRC_URI = "git://${AML_GIT_ROOT_PR}/vendor/playready.git;protocol=${AML_GIT_ROOT_PROTOCOL};branch=linux-3.x-amlogic"
#SRC_URI += " file://0001-playready-add-headers-for-build-1-1.patch;patchdir=${WORKDIR}/git"

#For common patches
SRC_URI:append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/multimedia/libmediadrm/playready-bin')}"

#SRCREV="bb62070629f62c580b32cdfe2cfaa3928611d6f3"
#use head version, ?= conditonal operator can be control revision in external rdk-next.conf like configuration file
#SRCREV ?= "${AUTOREV}"

ARM_TARGET = "arm.aapcs-linux.hard"
ARM_TARGET:aarch64 = "aarch64.lp64."

def get_playready_version(datastore):
    return datastore.getVar("PLAYREADY_VERSION", True)

def get_playready_short_version(datastore):
    return get_playready_version(datastore).replace('.','')

PLAYREADY_VER = "${@get_playready_version(d)}"
PLAYREADY_SHORT_VER = "${@get_playready_short_version(d)}"

PV = "${PLAYREADY_VER}"
S = "${WORKDIR}/git"

PATCHTOOL="git"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_lic[noexec] = "1"
#do_compile () {
#}
do_install () {
    mkdir -p ${D}/usr/bin
    mkdir -p ${D}${libdir}/pkgconfig
    mkdir -p ${D}/lib/optee_armtz
    mkdir -p ${D}/usr/include/playready

    install -m 0644 ${S}/prebuilt-v${PLAYREADY_VER}/noarch/ta/${TDK_VERSION}/*.ta ${D}/lib/optee_armtz
    install -m 0644 ${S}/prebuilt-v${PLAYREADY_VER}/noarch/pkgconfig/playready.pc ${D}${libdir}/pkgconfig
    install -m 0644 ${S}/prebuilt-v${PLAYREADY_VER}/${PLATFORM_TDK_VERSION}/${ARM_TARGET}/libplayready-${PLAYREADY_VER}.so ${D}${libdir}
    install -m 0755 ${S}/prebuilt-v${PLAYREADY_VER}/${PLATFORM_TDK_VERSION}/${ARM_TARGET}/prtest ${D}/usr/bin
    install -m 0755 ${S}/prebuilt-v${PLAYREADY_VER}/${PLATFORM_TDK_VERSION}/${ARM_TARGET}/pritee_test ${D}/usr/bin
    ln -s libplayready-${PLAYREADY_VER}.so ${D}${libdir}/libplayready.so
    ln -s libplayready.so ${D}${libdir}/libplayready${PLAYREADY_SHORT_VER}pk.so
    ln -s libplayready.so ${D}${libdir}/libplayready${PLAYREADY_SHORT_VER}pritee.so
    cp -rf ${S}/prebuilt-v${PLAYREADY_VER}/noarch/include/* ${D}/usr/include/playready/
    ln -s playready ${D}/usr/include/playready${PLAYREADY_VER}
}

FILES:${PN} += "/lib/optee_armtz/*"
FILES:${PN} += "/video/*"
FILES:${PN} += "${libdir}/*"

FILES_SOLIBSDEV = ""

INSANE_SKIP:${PN} = "ldflags dev-so already-stripped"
