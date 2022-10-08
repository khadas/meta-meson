#!/bin/bash

if [ $# -lt 3 ]; then
   echo " wrong parameters. should be module_name, src_module_path, dst_module_path"
   exit
fi

NAME=$1
EXTRAMODULEINSTALL=$3/$1_modules_install.sh
touch ${EXTRAMODULEINSTALL}

echo "#!/bin/sh" > ${EXTRAMODULEINSTALL}

echo "" >> ${EXTRAMODULEINSTALL}
echo "echo start insmod ${1} modules" >> ${EXTRAMODULEINSTALL}
mv $2/modules.order.* $2/modules.order
MODULEORDERFILE=$2/modules.order
echo "module file name : ${MODULEORDERFILE}"

echo "file content: $(cat ${MODULEORDERFILE})"

set -x
while read LINE
do
  echo "file name: ${LINE}"
  cp $2"/../"${LINE} ${3}/
  filename=$(basename ${LINE})
  echo "file name 2: ${filename}"
  echo "insmod $filename" >> ${EXTRAMODULEINSTALL}
done < ${MODULEORDERFILE}

echo "echo insmod ${1} modules success" >> ${EXTRAMODULEINSTALL}
mv $2/modules.order $3/

set +x
