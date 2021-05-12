#!/bin/bash

source define_colors.sh

regex="$1"
while IFS= read -r line; do 
	m=$(echo "${line}" | grep -o "${regex}" -)
	#echo -e "${RED}${line}${RESET}"
	#echo -e "${YELLOW}${m}${RESET}"
	if [ -z "${m}" ] ; then
		echo -e "${line}"
	else
		echo -e "${CYAN}${line}${RESET}"
	fi
done

