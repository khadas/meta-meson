inherit dm-verity-img

CONVERSION_DEPENDS_verity += "e2fsprogs-native"

process_verity() {
    local ENV="${DEPLOY_DIR_IMAGE}/${DM_VERITY_IMAGE}.$TYPE.verity.env"
    install -d ${DEPLOY_DIR_IMAGE}
    rm -f $ENV

    # Each line contains a key and a value string delimited by ':'. Read the
    # two parts into separate variables and process them separately. For the
    # key part: convert the names to upper case and replace spaces with
    # underscores to create correct shell variable names. For the value part:
    # just trim all white-spaces.
    IFS=":"
    while read KEY VAL; do
        if [ "$KEY" != "" ] && [ "$VAL" != "" ]; then
            printf '%s=%s\n' \
                "$(echo "$KEY" | tr '[:lower:]' '[:upper:]' | sed 's/ /_/g')" \
                "$(echo "$VAL" | tr -d ' \t')" >> $ENV
        fi
    done

    # Add partition size
    echo "DATA_SIZE=$SIZE" >> $ENV

    # bbwarn "`cat $ENV`"
}

verity_setup() {
    local TYPE=$1
    local INPUT=${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$TYPE
    local SIZE=$(stat --printf="%s" $INPUT)
    local OUTPUT=$INPUT.verity
    VERITYSETUP_LOG=${DEPLOY_DIR_IMAGE}/${INPUT}.veritysetup.log
    local DUMPE2FS_LOG=${DEPLOY_DIR_IMAGE}/${INPUT}.dumpe2fs.log
    local BLOCK_SIZE=0

    # Let's drop the first line of output (doesn't contain any useful info)
    # and feed the rest to another function.
    if [ -f $INPUT ]; then
        # clean first
        rm -f ${DEPLOY_DIR_IMAGE}/*.veritysetup.log
        rm -f ${DEPLOY_DIR_IMAGE}/*.dumpe2fs.log
        # Read block size for types "ext2/3/4"
        if [ "$TYPE" = "ext4" -o "$TYPE" = "ext3" -o "$TYPE" = "ext2" ]; then
            bbnote "`dumpe2fs $INPUT | grep -i "block size"  | tee $DUMPE2FS_LOG`"
            BLOCK_SIZE=`cat $DUMPE2FS_LOG | tr -d ' \t' | awk '{split($0, a, ":"); print a[2]}'`
        fi
        if [ $BLOCK_SIZE = 4096 -o $BLOCK_SIZE = 2048 -o $BLOCK_SIZE = 1024 ]; then
            # If block size is valid, set --data-block-size to it
            bbnote "`veritysetup --debug --data-block-size=$BLOCK_SIZE --hash-offset=$SIZE format $INPUT $INPUT | tee $VERITYSETUP_LOG`"
        else
            # Otherwise, default block size will be 4096
            bbnote "`veritysetup --debug --hash-offset=$SIZE format $INPUT $INPUT | tee $VERITYSETUP_LOG`"
        fi
        ln -s $INPUT $OUTPUT
    else
        bberror "Cannot find $INPUT"
        exit 1
    fi
    cat $VERITYSETUP_LOG | tail -n +2 | process_verity
}
