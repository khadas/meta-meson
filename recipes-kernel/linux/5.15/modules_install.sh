#!/bin/sh

if [ $# -lt 2 ] ; then
    echo "param number: $#, need to provice output dir path and common driver path"
    exit 1
fi

OUT_AMLOGIC_DIR=$1
MODULES_STAGING_DIR=${OUT_AMLOGIC_DIR}
COMMON_DRIVERS_DIR=$2
KERNEL_VER=$3
NAND_FLAG=$4

if [ $# -gt 4 ]; then
	MACHINE=$5
fi

echo "machine: $MACHINE"

SEQUENCE_FILE_PATH=${COMMON_DRIVERS_DIR}/scripts/amlogic/modules_sequence_list

mod_probe() {
	local ko=$1
	local loop
	for loop in `grep "^$ko:" modules.dep | sed 's/.*://'`; do
		mod_probe $loop
		echo insmod $loop >> __install.sh
	done
}

function mod_probe_recovery() {
	local ko=$1
	local loop
	for loop in `grep "^$ko:" modules_recovery.dep | sed 's/.*://'`; do
		mod_probe_recovery $loop
		echo insmod $loop >> __install_recovery.sh
	done
}

function regenerate_modules_sequence_list()
{
	echo "nand flag: ${NAND_FLAG}"
	cp  ${SEQUENCE_FILE_PATH} ${SEQUENCE_FILE_PATH}.temp
	if ${NAND_FLAG}; then
		echo "add nand ko to list"
		sed -i "/mmc/a\\\tamlogic-mtd-common\n\tamlogic_mtd_nfc" ${SEQUENCE_FILE_PATH}
	fi
}

adjust_sequence_modules_loading() {
    chips=${MACHINE}
    echo " chips list: ${chips}"

    source ${COMMON_DRIVERS_DIR}/scripts/amlogic/modules_sequence_list
    cp modules.dep modules.dep.temp

    soc_module=()
    for chip in ${chips[@]}; do
        chip_module=`ls amlogic-*-soc-${chip}.ko`
        soc_module=(${soc_module[@]} ${chip_module[@]})
    done
    echo soc_module=${soc_module[*]}

    delete_soc_module=()
    if [[ ${#soc_module[@]} == 0 ]]; then
        echo "Use all soc module"
    else
        for module in `ls amlogic-*-soc-*`; do
			if [[ ! "${soc_module[@]}" =~ "${module}" ]] ; then
				echo Delete soc module: ${module}
				sed -n "/${module}:/p" modules.dep.temp
				sed -i "/${module}:/d" modules.dep.temp
				delete_soc_module=(${delete_soc_module[@]} ${module})
			fi
		done
		echo delete_soc_module=${delete_soc_module[*]}
	fi

	delete_module=()
	for module in ${MODULES_LOAD_BLACK_LIST[@]}; do
		modules=`ls ${module}*`
		delete_module=(${delete_module[@]} ${modules[@]})
	done
	if [[ ${#delete_module[@]} == 0 ]]; then
		echo "No delete module, MODULES_LOAD_BLACK_LIST=${MODULES_LOAD_BLACK_LIST[*]}"
	else
		echo delete_module=${delete_module[*]}
		for module in ${delete_module[@]}; do
			echo Delete module: ${module}
			sed -n "/${module}:/p" modules.dep.temp
			sed -i "/${module}:/d" modules.dep.temp
		done
	fi

	cat modules.dep.temp | cut -d ':' -f 2 > modules.dep.temp1
	delete_modules=(${delete_soc_module[@]} ${delete_module[@]})
	for module in ${delete_modules[@]}; do
		match=`sed -n "/${module}/=" modules.dep.temp1`
		for match in ${match[@]}; do
			match_count=(${match_count[@]} $match)
		done
		if [[ ${#match_count[@]} != 0 ]]; then
			echo "Error ${#match_count[@]} modules depend on ${module}, please modify:"
			echo ${COMMON_DRIVERS_DIR}/scripts/amlogic/modules_sequence_list:MODULES_LOAD_BLACK_LIST
			exit
		fi
		rm -f ${module}
	done
	rm -f modules.dep.temp1
	touch modules.dep.temp1

	for module in ${RAMDISK_MODULES_LOAD_LIST[@]}; do
		echo RAMDISK_MODULES_LOAD_LIST: $module
		sed -n "/${module}:/p" modules.dep.temp
		sed -n "/${module}:/p" modules.dep.temp >> modules.dep.temp1
		sed -i "/${module}:/d" modules.dep.temp
		sed -n "/${module}.*\.ko:/p" modules.dep.temp
		sed -n "/${module}.*\.ko:/p" modules.dep.temp >> modules.dep.temp1
		sed -i "/${module}.*\.ko:/d" modules.dep.temp
	done

	for module in ${VENDOR_MODULES_LOAD_FIRST_LIST[@]}; do
		echo VENDOR_MODULES_LOAD_FIRST_LIST: $module
		sed -n "/${module}:/p" modules.dep.temp
		sed -n "/${module}:/p" modules.dep.temp >> modules.dep.temp1
		sed -i "/${module}:/d" modules.dep.temp
		sed -n "/${module}.*\.ko:/p" modules.dep.temp
		sed -n "/${module}.*\.ko:/p" modules.dep.temp >> modules.dep.temp1
		sed -i "/${module}.*\.ko:/d" modules.dep.temp
	done

	if [ -f modules.dep.temp2 ]; then
		rm modules.dep.temp2
	fi
	touch modules.dep.temp2
	for module in ${VENDOR_MODULES_LOAD_LAST_LIST[@]}; do
		echo VENDOR_MODULES_LOAD_FIRST_LIST: $module
		sed -n "/${module}:/p" modules.dep.temp
		sed -n "/${module}:/p" modules.dep.temp >> modules.dep.temp2
		sed -i "/${module}:/d" modules.dep.temp
		sed -n "/${module}.*\.ko:/p" modules.dep.temp
		sed -n "/${module}.*\.ko:/p" modules.dep.temp >> modules.dep.temp2
		sed -i "/${module}.*\.ko:/d" modules.dep.temp
	done

	cat modules.dep.temp >> modules.dep.temp1
	cat modules.dep.temp2 >> modules.dep.temp1

	cp modules.dep.temp1 modules.dep
	rm modules.dep.temp
	rm modules.dep.temp1
	rm modules.dep.temp2
}

create_ramdisk_vendor() {
	install_temp=$1
	source ${COMMON_DRIVERS_DIR}/scripts/amlogic/modules_sequence_list
	ramdisk_module_i=${#RAMDISK_MODULES_LOAD_LIST[@]}
	while [ ${ramdisk_module_i} -gt 0 ]; do
		let ramdisk_module_i--
		echo ramdisk_module_i=$ramdisk_module_i ${RAMDISK_MODULES_LOAD_LIST[${ramdisk_module_i}]}
		if [[ `grep "${RAMDISK_MODULES_LOAD_LIST[${ramdisk_module_i}]}" ${install_temp}` ]]; then
			last_ramdisk_module=${RAMDISK_MODULES_LOAD_LIST[${ramdisk_module_i}]}
			break;
		fi
	done
	# last_ramdisk_module=${RAMDISK_MODULES_LOAD_LIST[${#RAMDISK_MODULES_LOAD_LIST[@]}-1]}
	last_ramdisk_module_line=`sed -n "/${last_ramdisk_module}/=" ${install_temp}`
	for line in ${last_ramdisk_module_line}; do
		ramdisk_last_line=${line}
	done
	export ramdisk_last_line
	head -n ${ramdisk_last_line} ${install_temp} > ramdisk_install.sh
	mkdir ramdisk
	cat ramdisk_install.sh | cut -d ' ' -f 2 > ramdisk/ramdisk_modules.order
	cat ramdisk_install.sh | cut -d ' ' -f 2 | xargs mv -t ramdisk/

	sed -i '1s/^/#!\/bin\/sh\n\nset -x\n/' ramdisk_install.sh
	echo "echo Install ramdisk modules success!" >> ramdisk_install.sh
	chmod 755 ramdisk_install.sh
	mv ramdisk_install.sh ramdisk/

	file_last_line=`sed -n "$=" ${install_temp}`
	let line=${file_last_line}-${ramdisk_last_line}
	tail -n ${line} ${install_temp} > vendor_install.sh
	mkdir vendor
	cat vendor_install.sh | cut -d ' ' -f 2 > vendor/vendor_modules.order
	cat vendor_install.sh | cut -d ' ' -f 2 | xargs mv -t vendor/

	sed -i '1s/^/#!\/bin\/sh\n\nset -x\n/' vendor_install.sh
	echo "echo Install vendor modules success!" >> vendor_install.sh
	chmod 755 vendor_install.sh
	mv vendor_install.sh vendor/
}

modules_install() {
	mkdir -p ${OUT_AMLOGIC_DIR}/modules
	mkdir -p ${OUT_AMLOGIC_DIR}/ext_modules
	regenerate_modules_sequence_list
	cat ${SEQUENCE_LIST_PATH}

	#local MODULES_ROOT_DIR=$(echo ${MODULES_STAGING_DIR}/lib/modules/*)
	local MODULES_ROOT_DIR=${MODULES_STAGING_DIR}/lib/modules/${KERNEL_VER}/
        pushd ${MODULES_ROOT_DIR}
	local common_drivers=${COMMON_DRIVERS_DIR##*/}
	echo "common_drivers: $common_driver"
	local modules_list=$(find -type f -name "*.ko")
	for module in ${modules_list}; do
		if [[ -n ${ANDROID_PROJECT} ]]; then			# copy internal build modules
			if [[ `echo ${module} | grep -E "\.\/kernel\/|\/${common_drivers}\/"` ]]; then
				cp ${module} ${OUT_AMLOGIC_DIR}/modules/
			else
				cp ${module} ${OUT_AMLOGIC_DIR}/ext_modules/
			fi
		else							# copy all modules, include external modules
			cp ${module} ${OUT_AMLOGIC_DIR}/modules/
		fi
	done

	if [[ -n ${ANDROID_PROJECT} ]]; then				# internal build modules
		grep -E "^kernel\/|^${common_drivers}\/" modules.dep > ${OUT_AMLOGIC_DIR}/modules/modules.dep
	else								# all modules, include external modules
		cp modules.dep ${OUT_AMLOGIC_DIR}/modules
	fi
	popd

	pushd ${OUT_AMLOGIC_DIR}/modules
	sed -i 's#[^ ]*/##g' modules.dep

	adjust_sequence_modules_loading "${arg1[*]}"

	touch __install.sh
	touch modules.order
	for loop in `cat modules.dep | sed 's/:.*//'`; do
	        mod_probe $loop
		echo $loop >> modules.order
	        echo insmod $loop >> __install.sh
	done

	cat __install.sh  | awk ' {
		if (!cnt[$2]) {
			print $0;
			cnt[$2]++;
		}
	}' > __install.sh.tmp

	create_ramdisk_vendor __install.sh.tmp

	echo "#!/bin/sh" > install.sh
	echo "cd ramdisk" >> install.sh
	echo "./ramdisk_install.sh" >> install.sh
	echo "cd ../vendor" >> install.sh
	echo "./vendor_install.sh" >> install.sh
	echo "cd ../" >> install.sh
	chmod 755 install.sh

	echo "/modules/: all `wc -l modules.dep | awk '{print $1}'` modules."
	rm __install.sh __install.sh.tmp

	popd
}

modules_install
rm -rf ${OUT_AMLOGIC_DIR}/lib/modules/*/build
rm -rf ${OUT_AMLOGIC_DIR}/lib/modules/*/source
#mv ${SEQUENCE_FILE_PATH}.temp ${SEQUENCE_FILE_PATH}


