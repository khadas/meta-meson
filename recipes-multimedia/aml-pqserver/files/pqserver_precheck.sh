#!/bin/sh

PQ_DEFAULT_PATH=/vendor/etc/tvconfig/pq
PQ_DEFAULT_CONF=${PQ_DEFAULT_PATH}/pq_default.ini
PQ_DEFAULT_DB=${PQ_DEFAULT_PATH}/pq.db

if [ ! -f ${PQ_DEFAULT_CONF} ]; then
    echo "${PQ_DEFAULT_CONF} not found."
    exit 1
fi

if [ ! -f ${PQ_DEFAULT_DB} ]; then
    echo "${PQ_DEFAULT_DB} not found."
    exit 1
fi

PQ_RW_PATH=$(cat ${PQ_DEFAULT_CONF} | grep pq_uiSettingDataFile_path | awk -F= '{print $2}' | xargs)

if [ ${PQ_RW_PATH} = ${PQ_DEFAULT_PATH} ]; then
    echo "No RW path defined."
    exit 0
fi

PQ_RW_DB=${PQ_RW_PATH}/pq.db

if [ -f ${PQ_RW_DB} ]; then
    echo "Check done, database already exists."
    exit 0
fi

if mkdir -p ${PQ_RW_PATH}; then
    cp -af ${PQ_DEFAULT_DB} ${PQ_RW_PATH}
    echo "Check done, database copied."
fi
