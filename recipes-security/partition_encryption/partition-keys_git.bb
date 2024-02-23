DESCRIPTION = "Partition Encryption Keys"
LICENSE = "AMLOGIC"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-meson/license/AMLOGIC;md5=6c70138441c57c9e1edb9fde685bd3c8"

# Add your partition keys and PWEK here. Here are demo keys.
# Partition keys should be named as <partition name>.bin.
# For local encryption mode, partition key is used as SEED.
SRC_URI += "file://pwek.bin"
SRC_URI += "file://casecure.bin"
SRC_URI += "file://recovery.bin"
SRC_URI += "file://boot.bin"

BBCLASSEXTEND += "native nativesdk"

S = "${WORKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
SRCREV ?= "${AUTOREV}"
DEPENDS = "openssl-native xxd-native"

do_install() {
    TARGET_DIR="${DEPLOY_DIR_IMAGE}/partition_enc_data"
    # clean first
    rm -rf ${TARGET_DIR}

    if [ ! -z "${ENCRYPTED_PARTITIONS}" ]; then
        install -d ${TARGET_DIR}

        if [ "${@bb.utils.contains('DISTRO_FEATURES', 'partition-enc-local', 'true', 'false', d)}" = "true" ]; then
            for part in ${ENCRYPTED_PARTITIONS}; do
                SEED="${part}.bin"
                if [ -f ${S}/${SEED} ]; then
                    # For local encryption mode, partition key is used as SEED
                    cp ${S}/${SEED} ${TARGET_DIR}/${SEED}
                else
                    bbfatal "Cannot find ${SEED}"
                fi
            done
        elif [ "${@bb.utils.contains('DISTRO_FEATURES', 'partition-enc', 'true', 'false', d)}" = "true" ]; then
            PWEK="pwek.bin"
            if [ ! -f ${S}/${PWEK} ]; then
                bbfatal "Cannot find ${PWEK}"
            fi
            for part in ${ENCRYPTED_PARTITIONS}; do
                if [ -f ${S}/${part}.bin ]; then
                    # For pre-encryption mode, partition key is wrapped by PWEK
                    openssl enc -e -aes-128-ecb -in ${S}/${part}.bin \
                        -K `xxd -p ${PWEK}` -nopad -nosalt \
                        -out ${TARGET_DIR}/${part}.wrapped.bin
                else
                    bbfatal "Partition: ${part} is encrypted, but there is no partition key for it."
                fi
            done
        else
            bbfatal "It's likely wrong here."
        fi
    fi
}

do_install_class-native() {
    install -d ${D}${sysconfdir}
    for key in `ls ${S}/*.bin`; do
        BASENAME=`basename $key`
        install -m 0755 ${key} ${D}${sysconfdir}/${BASENAME}
        bbwarn "${BASENAME}"
    done
}
