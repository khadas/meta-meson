#!/bin/sh

PQ_DEFAULT_PATH=/vendor/etc/tvconfig/pq
PQ_DEFAULT_CONF=${PQ_DEFAULT_PATH}/pq_default.ini
PQ_DEFAULT_DB=${PQ_DEFAULT_PATH}/pq.db
PQ_DEFAULT_OVERSCAN_DB=${PQ_DEFAULT_PATH}/overscan.db

if [ ! -f ${PQ_DEFAULT_CONF} ]; then
    echo "${PQ_DEFAULT_CONF} not found."
    exit 1
fi

if [ ! -f ${PQ_DEFAULT_DB} ]; then
    echo "${PQ_DEFAULT_DB} not found."
    exit 1
fi

PQ_RW_PATH=$(cat ${PQ_DEFAULT_CONF} | grep -m1 'pq_.*_path' | awk -F= '{print $2}' | xargs)

if [ -z "${PQ_RW_PATH}" ]; then
  echo "PQ DB Path not found"
  exit 0
fi

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
    if [ -f ${PQ_DEFAULT_OVERSCAN_DB} ]; then
      cp -af ${PQ_DEFAULT_OVERSCAN_DB} ${PQ_RW_PATH}
    fi
    echo "Check done, database copied."
fi
