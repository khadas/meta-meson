#!/bin/bash

if [ -n "$BASH_SOURCE" ]; then
  THIS_SCRIPT=$BASH_SOURCE
elif [ -n "$ZSH_NAME" ]; then
  THIS_SCRIPT=$0
else
  THIS_SCRIPT="$(pwd)/oe-init-build-env"
fi
MESON_PATH=$(cd `dirname $(realpath -P $THIS_SCRIPT)`; pwd)

LOCAL_DIR=$(pwd)
if [ -z $BUILD_DIR ]; then
	BUILD_DIR="build"
fi
if [ -z $LOCAL_BUILD ]; then
    LOCAL_BUILD=0
else
    LOCAL_BUILD=1
fi

DEFCONFIG_ARRAY=($(pushd $MESON_PATH/conf/machine 2>&1 >> /dev/null; find -name '*\.conf' | sed 's@./@@' | sed 's@\.conf@@' | sort))

DEFCONFIG_ARRAY_LEN=${#DEFCONFIG_ARRAY[@]}

i=0
while [[ $i -lt $DEFCONFIG_ARRAY_LEN ]]
do
	let i++
done

function choose_info()
{
	echo
	echo "You're building on Linux"
	echo "Lunch menu...pick a combo:"
	i=0
	while [[ $i -lt $DEFCONFIG_ARRAY_LEN ]]
	do
		echo -e "$((${i}+1)).\t${DEFCONFIG_ARRAY[$i]}"
		let i++
	done
	echo
}

function get_index() {
	if [ $# -eq 0 ]; then
		return 0
	fi

	i=0
	while [[ $i -lt $DEFCONFIG_ARRAY_LEN ]]
	do
		if [ $1 = "${DEFCONFIG_ARRAY[$i]}" ]; then
			let i++
			return ${i}
		fi
		let i++
	done
	return 0
}

function choose_type()
{
	choose_info
	local DEFAULT_NUM DEFAULT_VALUE
	DEFAULT_NUM=2
	DEFAULT_VALUE="mesong12a_u212.conf"

	export TARGET_MACHINE=
	local ANSWER
	while [ -z $TARGET_MACHINE ]
	do
		echo -n "Which would you like? ["$DEFAULT_NUM"] "
		if [ -z "$1" ]; then
			read ANSWER
		else
			echo $1
			ANSWER=$1
		fi

		if [ -z "$ANSWER" ]; then
			ANSWER="$DEFAULT_NUM"
		fi

		if [ -n "`echo $ANSWER | sed -n '/^[0-9][0-9]*$/p'`" ]; then
			if [ $ANSWER -le $DEFCONFIG_ARRAY_LEN ] && [ $ANSWER -gt 0 ]; then
				index=$((${ANSWER}-1))
				TARGET_MACHINE=${DEFCONFIG_ARRAY[$index]}
			else
				echo
				echo "number not in range. Please try again."
				echo
			fi
		else
			get_index $ANSWER
			ANSWER=$?
			if [ $ANSWER -gt 0 ]; then
				index=$((${ANSWER}-1))
				TARGET_MACHINE=${DEFCONFIG_ARRAY[$index]}
			else
				echo
				echo "I didn't understand your response.  Please try again."
				echo
			fi
		fi
		if [ -n "$1" ]; then
			break
		fi
	done
}

function lunch()
{
	if [ -n "$TARGET_MACHINE" ]; then
		MACHINE=$TARGET_MACHINE source $MESON_PATH/oe-init-build-env-meson $BUILD_DIR
    if [ $LOCAL_BUILD == "1" ];then
        cat >> conf/local.conf <<EOF

AML_GIT_ROOT = "git.myamlogic.com"
AML_GIT_PROTOCOL = "git"
AML_GIT_ROOT_YOCTO_SUFFIX = "/yocto"
EOF
    fi

    if [ -n "$(echo $TARGET_MACHINE | grep -- lib32)" ]; then
      NEED_A6432_SUPPORT=y
    fi

    if [ "${NEED_A6432_SUPPORT+set}" = "set" ]; then
      cat >> conf/local.conf <<EOF
#Added for A6432 support
require conf/multilib.conf
MULTTILIBS = "multilib:lib32"
DEFAULTTUNE_virtclass-multilib-lib32 = "armv7athf-neon"
EOF
    fi

    if [ "${SETUP_SOURCE_MIRROR}" = "1" ]; then
      cat >> conf/local.conf <<EOF
#Setup source mirror, package will be generated under DL_DIR folder
BB_GENERATE_MIRROR_TARBALLS = "1"
EOF
    fi

    if [ "${USE_SOURCE_MIRROR}" = "1" ]; then
      cat >> conf/local.conf <<EOF
#Utilize source mirror
SOURCE_MIRROR_URL ?= "file://$(realpath ../downloads)"
INHERIT += "own-mirrors"
BB_NO_NETWORK = "1"
EOF
    fi
        export MACHINE=$TARGET_MACHINE
        export AML_PATCH_PATH=${MESON_ROOT_PATH}/aml-patches
        export BB_ENV_EXTRAWHITE="${BB_ENV_EXTRAWHITE} AML_PATCH_PATH"
		echo "==========================================="
		echo
		echo "MACHINE=${TARGET_MACHINE}"
		echo "OUTPUT_DIR=${BUILD_DIR}"
		echo "LOCAL_BUILD=${LOCAL_BUILD}"
        echo "AML_PATCH_PATH=${AML_PATCH_PATH}"
		echo
		echo "==========================================="

	echo "Common targets are:"
	for file in `ls ${MESON_PATH}/recipes-core/images`
	do
	  if  [ "bb" = "${file#*.}" ]; then
	  echo "${file%%.*}"
	  fi
	done
	fi
#	# Copy <manifest>.conf file to auto.conf for revision lock
#	if [ -L ./../.repo/manifest.xml ] ; then
#	  MANIFEST="$(basename `readlink -f ./../.repo/manifest.xml ` | cut -d '.' -f 1)"
#	else
#	  MANIFEST=$(grep include ./../.repo/manifest.xml | cut -d '"' -f 2 | cut -d '.' -f 1)
#	fi
#	echo " Manifest Name = ${MANIFEST}"
#	if [ -f "./../.repo/manifests/${MANIFEST}.conf" ]; then
#	  cp ./../.repo/manifests/${MANIFEST}.conf ./conf/auto.conf
#	fi
}

function function_stuff()
{
	choose_type $@
	lunch
}
function_stuff $@
