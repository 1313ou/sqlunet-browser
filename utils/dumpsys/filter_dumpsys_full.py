#!/usr/bin/python2

import sys
import re

class colors:
    MAGENTA = '\033[95m'
    BLUE = '\033[94m'
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'
    Z = '\033[0m'

target='org.sqlunet.browser.wn/'
after=3
lines=sys.__stdin__.readlines()
n=len(lines)
mark=[False for i in range(n)]
for i in range(n):
	if re.match('^\s*Stack.*$',lines[i]):
		mark[i]=True
		lines[i]=colors.CYAN + lines[i] + colors.Z
		if re.search('type=standard',lines[i]):
			j=i+1;
			while j < n:
				if re.match('^\s*$',lines[j]):
					break
				mark[j]=True
				if re.match('^\s*\* Task.*$',lines[j]):
					lines[j]=colors.GREEN + lines[j] + colors.Z
				if re.match('^\s*Activities=.*$',lines[j]):
					lines[j]=colors.YELLOW + lines[j] + colors.Z
				if re.match('^\s*\* Hist.*$',lines[j]):
					lines[j]=colors.BLUE + lines[j] + colors.Z
				if re.search(target,lines[j]):
					lines[j]=colors.BOLD + lines[j] + colors.Z

				j=j+1
			i=j

lines2=[lines[i] for i in range(n) if mark[i]]
sys.__stdout__.writelines(lines2)
