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
				if re.search('Activity',lines[j]) :
					mark[j]=True

				if re.match('^\s*\* Task.*$',lines[j]):
					mark[j]=True
					lines[j]=colors.GREEN + lines[j] + colors.Z
					k=j+1;
					while k < n:
						if re.search('Activities=',lines[k]) :
							mark[k]=True
						if re.match('^\s*\* Hist.*$',lines[k]):
							mark[k]=True
							lines[k]=colors.BLUE + lines[k] + colors.Z
						#if re.search(target,lines[k]):
						#	mark[k]=True
						#	lines[k]=colors.BOLD + lines[k] + colors.Z
						k=k+1
					j=k
				j=j+1
			i=j

lines2=[lines[i] for i in range(n) if mark[i]]
sys.__stdout__.writelines(lines2)
