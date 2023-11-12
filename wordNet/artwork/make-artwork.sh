#!/bin/bash

source "./lib-artwork.sh"

relation_list='
agent
also
antonym
attribute
bodypart
bymeansof
caused
causes
derivation
destination
domain_member_region
domain_member
domain_member_topic
domain_region
domain
domain_topic
entailed
entails
event
exemplified
exemplifies
hypernym
hyponym
instance_hypernym
instance_hyponym
instrument
location
material
member_holonym
member_meronym
other
part_holonym
participle
part_meronym
pertainym
property
relations
result
similar
state
substance_holonym
substance_meronym
undergoer
uses
vehicle
verb_group
'
extra_relation_list='
adjderived
domain_member_term
domain_term
holonym
meronym
pos
pos_a
pos_n
pos_r
pos_s
pos_v
'

top_list="up.svg down.svg"
icon_list="wordnet.svg synset.svg synsetmember.svg domain.svg morph.svg adjposition.svg verbtemplate.svg verbframe.svg error.svg"
link_list="ic_link_relation.svg"
settings_list="ic_settings_wordnet.svg"
search_list="ic_search_wnword.svg ic_search_wndefinition.svg ic_search_wnsample.svg"

asset_list=""
for r in ${relation_list} ${extra_relation_list}; do
  asset_list="${asset_list} ${r}.svg"
done

make_res "${top_list}" 20
make_res "${icon_list}" 16
make_res "${link_list}" 16
make_res "${settings_list}" 24
make_res "${search_list}" 32

make_clear_asset "${asset_list}" 16 "${dirassets}/images/wordnet"

#generated elsewhere

for r in ${relation_list}; do
  touch "ic_${r}.svg"
 	for k in ${resnames}; do
  	d="${dirres}/drawable-${k}"
  	f="${d}/ic_${r}.png"
  	[ -e "${f}" ] || echo -e "${R}${f} does not exist${Z}"
  	touch "${f}"
 	done
done

check
