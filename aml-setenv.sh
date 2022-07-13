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

DEFCONFIG_ARRAY=($(pushd $MESON_PATH/conf/machine 2>&1 >> /dev/null; find -name '*\.conf' | sed 's@./@@' | sed 's@\.conf@@' | sort))

DEFCONFIG_ARRAY_LEN=${#DEFCONFIG_ARRAY[@]}

function choose_info()
{
	echo
	echo "You're building on Linux"
	echo "Lunch menu...pick a combo:"
	i=1
	for f in "${DEFCONFIG_ARRAY[@]}";
	do
		echo -e "${i}.\t${f}"
		let i++
	done
	echo
}

function check_answer() {
	if [ $# -eq 0 ]; then
		return 1
	fi

	for f in "${DEFCONFIG_ARRAY[@]}";
	do
		if [ $1 = "${f}" ]; then
			return 0
		fi
	done
	return 1
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
				TARGET_MACHINE=${DEFCONFIG_ARRAY[@]:${index}:1}
			else
				echo
				echo "number not in range. Please try again."
				echo
			fi
		else
			if check_answer $ANSWER; then
				TARGET_MACHINE=$ANSWER
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
	fi
    if [ "$OPENLINUX_BUILD" = "1" ];then
        cat >> conf/local.conf <<EOF
#Force OpenLinux Access
AML_GIT_ROOT = "git@openlinux.amlogic.com/yocto"
AML_GIT_PROTOCOL = "ssh"
AML_GIT_ROOT_YOCTO_SUFFIX = ""
AML_GIT_ROOT_PR = "git@openlinux.amlogic.com"
AML_GIT_ROOT_WV = "git@openlinux.amlogic.com/yocto"
AML_GIT_ROOT_PROTOCOL = "ssh"
EOF
    fi

    if [ "$OPENLINUX_BUILD" = "2" ];then
        cat >> conf/local.conf <<EOF
#Force OpenLinux Access
AML_GIT_ROOT = "git@openlinux2.amlogic.com/yocto"
AML_GIT_PROTOCOL = "ssh"
AML_GIT_ROOT_YOCTO_SUFFIX = ""
AML_GIT_ROOT_PR = "git@openlinux2.amlogic.com"
AML_GIT_ROOT_WV = "git@openlinux2.amlogic.com/yocto"
AML_GIT_ROOT_PROTOCOL = "ssh"
EOF
    fi

    # Add meta-aml-netflix only if a machine configuration choosed from different layer
    if [ -d ${MESON_ROOT_PATH}/meta-aml-netflix ] && [ $(grep '^BBLAYERS' conf/bblayers.conf | grep -c 'meta-aml-netflix[^-]') -eq 0 ]; then
      cat >> conf/bblayers.conf <<EOF
BBLAYERS =+ "\${MESON_ROOT_PATH}/meta-aml-netflix"
EOF
    fi

    if [ -d ${MESON_ROOT_PATH}/meta-selinux ] && [ $(grep '^BBLAYERS' conf/bblayers.conf | grep -c 'meta-selinux[^-]') -eq 0 ]; then
            cat >> conf/bblayers.conf <<EOF
BBLAYERS =+ "\${MESON_ROOT_PATH}/meta-selinux"
EOF
    fi

    if [ -d ${MESON_ROOT_PATH}/meta-security ] && [ $(grep '^BBLAYERS' conf/bblayers.conf | grep -c 'meta-security[^-]') -eq 0 ]; then
            cat >> conf/bblayers.conf <<EOF
BBLAYERS =+ "\${MESON_ROOT_PATH}/meta-security"
EOF
    fi

    if [ -d ${MESON_ROOT_PATH}/meta-zapperplus ] && [ $(grep '^BBLAYERS' conf/bblayers.conf | grep -c 'meta-zapperplus[^-]') -eq 0 ]; then
            cat >> conf/bblayers.conf <<EOF
BBLAYERS =+ "\${MESON_ROOT_PATH}/meta-zapperplus"
EOF
    fi

# Add meta-perl only if not already present.
    if [ -d ${MESON_ROOT_PATH}/meta-openembedded/meta-perl ] && [ $(grep '^BBLAYERS' conf/bblayers.conf | grep -c 'meta-perl[^-]') -eq 0 ]; then
        cat >> conf/bblayers.conf <<EOF
BBLAYERS =+ "\${MESON_ROOT_PATH}/meta-openembedded/meta-perl"
EOF
    fi

    unset NEED_A6432_SUPPORT
    if [ -n "$(echo $TARGET_MACHINE | grep -- lib32)" ]; then
      NEED_A6432_SUPPORT=y
    fi

    if [ "${NEED_A6432_SUPPORT+set}" = "set" ] && [ $(grep '^MULTTILIBS' conf/local.conf | grep -c 'multilib:lib32[^-]') -eq 0 ]; then
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

    if [ -e ${MESON_ROOT_PATH}/CCACHE_DIR ] && [ $(grep '^CCACHE_TOP_DIR' conf/local.conf | grep -c 'CCACHE_DIR[^-]') -eq 0 ]; then
        cat >> conf/local.conf << EOF
# Enable ccache
INHERIT += "ccache"
CCACHE_TOP_DIR = "${MESON_ROOT_PATH}/CCACHE_DIR"
EOF
    fi

    export MACHINE=$TARGET_MACHINE
    export AML_PATCH_PATH=${MESON_ROOT_PATH}/aml-patches
    # Secure Boot Sign Tool
    #export AML_SCS_SIGN_TOOL=${MESON_ROOT_PATH}/Aml_Linux_SCS_SignTool/amlogic_scs_sign_whole_pkg.bash
    # Secure Boot config files device-keys and fw_arb.cfg
    #export AML_SCS_SIGN_CONFIG_PATH=${MESON_ROOT_PATH}/aml-comp/prebuilt/hosttools/aml-linux-scs
    export BB_ENV_EXTRAWHITE="${BB_ENV_EXTRAWHITE} AML_PATCH_PATH AML_SCS_SIGN_TOOL AML_SCS_SIGN_CONFIG_PATH"
    echo "==========================================="
    echo
    echo "MACHINE=${TARGET_MACHINE}"
    echo "OUTPUT_DIR=${BUILD_DIR}"
    echo "AML_PATCH_PATH=${AML_PATCH_PATH}"
    echo "AML_SCS_SIGN_TOOL=${AML_SCS_SIGN_TOOL}"
    echo "AML_SCS_SIGN_CONFIG_PATH=${AML_SCS_SIGN_CONFIG_PATH}"
    echo
    echo "==========================================="

	echo "Common targets are:"
	for file in `ls ${MESON_PATH}/recipes-core/images`
	do
	  if  [ "bb" = "${file#*.}" ]; then
	  echo "${file%%.*}"
	  fi
	done

	# Copy <manifest>.conf file to auto.conf for revision lock
	if [ -L ./../.repo/manifest.xml ] ; then
	  MANIFEST="$(basename `readlink -f ./../.repo/manifest.xml ` | cut -d '.' -f 1)"
	else
	  MANIFEST=$(grep include ./../.repo/manifest.xml | cut -d '"' -f 2 | cut -d '.' -f 1)
	fi
	echo " Manifest Name = ${MANIFEST}"
	if [ -f "./../.repo/manifests/${MANIFEST}.conf" ]; then
	  cp ./../.repo/manifests/${MANIFEST}.conf ./conf/auto.conf
	else
	  echo
	  echo -e "\033[31m Missing file $LOCAL_DIR/.repo/manifests/${MANIFEST}.conf !!!  Please check. \033[0m"
	  echo
	fi

    if [ -f "$MESON_ROOT_PATH/yocto-release-info" ]; then
        cat $MESON_ROOT_PATH/yocto-release-info  >> conf/auto.conf
    fi

    #By default, bitbake 4 CPU for parallel build
    sed -i '/^PARALLEL_MAKE = /d' conf/local.conf
    sed -i '/^BB_NUMBER_THREADS = /d' conf/local.conf
    if [ "$BITBAKE_FULLSPEED_BUILD" != "1" ]; then
        sed -i '1i\PARALLEL_MAKE = "-j 4"' conf/local.conf
        sed -i '1i\BB_NUMBER_THREADS = "4"' conf/local.conf
    fi
}

function function_stuff()
{
	choose_type $@
	lunch
}
function_stuff $@
