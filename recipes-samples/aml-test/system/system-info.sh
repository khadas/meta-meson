#!/bin/bash


CMD_LIST=("/etc/amlbian-release" \
		  "/etc/amlbian-build-time" \
		  /etc/os-release \
		  /proc/version \
		  /etc/issue)

for CMD in ${CMD_LIST[@]};do
	echo "cat "$CMD
	eval "cat "$CMD
done


CMD="uname -a"
echo $CMD
eval $CMD

CMD="lsb_release -a"
echo $CMD
eval $CMD

if [ -f /etc/amldebs_version.txt ]; then
	CMD="cat /etc/amldebs_version.txt"
	echo -e "\n\r"$CMD
	eval $CMD
fi
