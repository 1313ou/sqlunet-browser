#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirres=../src/main/res
dirassets=../src/main/assets
dirapp=..

#declare -A res_icon16
#res_icon16=([mdpi]=16 [hdpi]=24 [xhdpi]=32 [xxhdpi]=48 [xxxhdpi]=64)
#declare -A res_icon24
#res_icon24=([mdpi]=24 [hdpi]=36 [xhdpi]=48 [xxhdpi]=72 [xxxhdpi]=96)
#declare -A res_icon32
#res_icon32=([mdpi]=32 [hdpi]=48 [xhdpi]=64 [xxhdpi]=96 [xxxhdpi]=128)
#declare -A res_icon48
#res_icon48=([mdpi]=48 [hdpi]=72 [xhdpi]=96 [xxhdpi]=144 [xxxhdpi]=192)
#declare -A res_icon64
#res_icon64=([mdpi]=64 [hdpi]=96 [xhdpi]=128 [xxhdpi]=192 [xxxhdpi]=256)
#declare -A res_icon144
#res_icon144=([mdpi]=144 [hdpi]=192 [xhdpi]=288 [xxhdpi]=384 [xxxhdpi]=576)
#declare -A res_icon256
#res_icon256=([mdpi]=256 [hdpi]=384 [xhdpi]=512 [xxhdpi]=768 [xxxhdpi]=1024)
#declare -A res_icon512
#res_icon512=([mdpi]=512 [hdpi]=768 [xhdpi]=1024 [xxhdpi]=1536 [xxxhdpi]=2048)

declare -A res_launch
res_launch=([mdpi]=48 [hdpi]=72 [xhdpi]=96 [xxhdpi]=144 [xxxhdpi]=192)
list_launch="ic_launcher.svg ic_launcher_round.svg"

declare -A res_logo
res_logo=([mdpi]=64 [hdpi]=96 [xhdpi]=128 [xxhdpi]=192 [xxxhdpi]=256)
list_logo="ic_logo.svg"

declare -A res_splash
res_splash=([mdpi]=144 [hdpi]=192 [xhdpi]=288 [xxhdpi]=384 [xxxhdpi]=576)
list_splash="ic_splash.svg"

declare -A res_supersplash
res_supersplash=([mdpi]=400 [hdpi]=600 [xhdpi]=800 [xxhdpi]=1200 [xxxhdpi]=1600)
list_supersplash="home.svg"

declare -A res_arrow
res_arrow=([mdpi]=10 [hdpi]=15 [xhdpi]=20 [xxhdpi]=30 [xxxhdpi]=40)
list_arrow="ic_spinner_arrow.svg"

declare -A res_icon
res_icon=([mdpi]=32 [hdpi]=48 [xhdpi]=64 [xxhdpi]=96 [xxxhdpi]=128)
list_icon="ic_selector.svg ic_xselector.svg ic_role.svg ic_roles.svg ic_rows_bysynset.svg ic_rows_byrole.svg ic_roles_grouped.svg ic_rows_ungrouped.svg ic_search_wnword.svg ic_search_wndefinition.svg ic_search_wnsample.svg ic_search_vnexample.svg ic_search_pbexample.svg ic_search_fnsentence.svg ic_unknown.svg ic_ok.svg ic_fail.svg ic_setup.svg ic_download.svg ic_run.svg ic_download_source.svg ic_download_dest.svg"

declare -A res_smallicon
res_smallicon=([mdpi]=16 [hdpi]=24 [xhdpi]=32 [xxhdpi]=48 [xxxhdpi]=64)
list_smallicon="ic_item.svg"

declare -A res_drawericon
res_drawericon=([mdpi]=32 [hdpi]=48 [xhdpi]=64 [xxhdpi]=96 [xxxhdpi]=128)
list_drawericon="ic_home.svg ic_search_browse.svg ic_search_text.svg ic_search_pm.svg ic_setup.svg ic_settings.svg ic_storage.svg ic_status.svg ic_sql.svg ic_help.svg ic_about.svg"

declare -A res_middleicon
res_middleicon=([mdpi]=24 [hdpi]=36 [xhdpi]=48 [xxhdpi]=72 [xxxhdpi]=96)
list_middleicon="bn_setup.svg bn_download.svg bn_info.svg"

declare -A res_domain
res_domain=([mdpi]=16 [hdpi]=24 [xhdpi]=32 [xxhdpi]=48 [xxxhdpi]=64)
list_domain="wordnet.svg verbnet.svg propbank.svg framenet.svg bnc.svg predicatematrix.svg"

declare -A res_app
res_app=([app]=512)
list_app="ic_launcher.svg"

# launcher
for svg in ${list_launch}; do
	for r in ${!res_launch[@]}; do 
		d="${dirres}/mipmap-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_launch[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_launch[$r]} > /dev/null 2> /dev/null
	done
done

# logo
for svg in ${list_logo}; do
	for r in ${!res_logo[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_logo[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_logo[$r]} > /dev/null 2> /dev/null
	done
done

# splash
for svg in ${list_splash}; do
	for r in ${!res_splash[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_splash[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_splash[$r]} > /dev/null 2> /dev/null
	done
done

# super splash
for svg in ${list_supersplash}; do
	for r in ${!res_supersplash[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_supersplash[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_supersplash[$r]} > /dev/null 2> /dev/null
	done
done

# icons
for svg in ${list_icon}; do
	for r in ${!res_icon[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_icon[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_icon[$r]} > /dev/null 2> /dev/null
	done
done

# arrows
for svg in ${list_arrow}; do
	for r in ${!res_arrow[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_arrow[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_arrow[$r]} > /dev/null 2> /dev/null
	done
done

# small icons
for svg in ${list_smallicon}; do
	for r in ${!res_smallicon[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_smallicon[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_smallicon[$r]} > /dev/null 2> /dev/null
	done
done

# drawer icons
for svg in ${list_drawericon}; do
	for r in ${!res_drawericon[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_drawericon[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_drawericon[$r]} > /dev/null 2> /dev/null
	done
done

# middle icons
for svg in ${list_middleicon}; do
	for r in ${!res_middleicon[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_middleicon[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_middleicon[$r]} > /dev/null 2> /dev/null
	done
done

# domains
for svg in ${list_domain}; do
	for r in ${!res_domain[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_domain[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_domain[$r]} > /dev/null 2> /dev/null
	done
done

# W E B

# web
d=${dirassets}/help/images
mkdir -p ${d}

svg=logo.svg
r=128
png="${svg%.svg}.png"
echo "${svg} -> ${d}/${png} @ resolution ${r}"
inkscape ${svg} --export-png=${d}/${png} -h${r} > /dev/null 2> /dev/null

svg=sqlunet.svg
r=384
png="${svg%.svg}.png"
echo "${svg} -> ${d}/${png} @ resolution ${r}"
inkscape ${svg} --export-png=${d}/${png} -h${r} > /dev/null 2> /dev/null

# A P P

# app
for svg in ${list_app}; do
	for r in ${!res_app[@]}; do 
		d=${dirapp}
		mkdir -p ${d}
		png="${svg%.svg}-app.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_app[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_app[$r]} > /dev/null 2> /dev/null
	done
done

