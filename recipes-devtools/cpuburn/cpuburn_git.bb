SUMMARY = "cpuburn lets you use 100% of all available cores, useful when stress-testing."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=89102645b0eb821092d21e630940cd21"

DEPENDS = "go-native"

SRC_URI = "git://github.com/patrickmn/cpuburn.git;protocol=https"
SRCREV = "master"

S = "${WORKDIR}/git"

do_compile() {
    if [ "${TARGET_ARCH}" = "aarch64" ]; then
        export GOARCH=arm64
    elif [ "${TARGET_ARCH}" = "arm" ]; then
        export GOARCH=arm
    fi
    go build cpuburn.go
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/cpuburn ${D}${bindir}/cpuburn
}

FILES_${PN} = "${bindir}/cpuburn"
