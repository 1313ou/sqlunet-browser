#!/bin/bash

source ./define_colors.sh
source define_relations.sh

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirdest=../../common/reference/wordnet/relations/artwork
dirsrc=composite

declare -A trans=(
['pos_a']='pos_a'
['pos_n']='pos_n'
['pos_r']='pos_r'
['pos_s']='pos_s'
['pos_v']='pos_v'
['pos']='pos'

['adjderived']='derivation_adj'
['domain_member_region']='hasdomain_region'
['domain_member']='hasdomain'
['domain_member_term']='hasdomain_term'
['domain_member_topic']='hasdomain_topic'
['domain_term']='domain_term'
['exemplified']='hasdomain_usage'
['exemplifies']='domain_usage'
['instance_hypernym']='hypernym_instance'
['instance_hyponym']='hyponym_instance'
['member_holonym']='holonym_member'
['member_meronym']='meronym_member'
['part_holonym']='holonym_part'
['part_meronym']='meronym_part'
['substance_holonym']='holonym_substance'
['substance_meronym']='meronym_substance'
['verb_group']='verbgroup'

['role_roles']='role_roles'
['relations']='relations'
['other']=''
)

#or k in "${!trans[@]}"
#o
# echo "$k -> ${trans[$k]}"
#one

function process(){
  local dir=$1
  shift
  local p=$1
  shift
  local args=$@
  for f in $args; do
    b=$(basename "${f}")
    t="$b"
    if [[ ${trans[${b}]+_} ]]; then
        t=${trans[${b}]}
    else
       r=${b}
    fi
    if [ -e "${dir}/${p}${t}.svg" ]; then
      cp "${dirsrc}/${b}.svg" "${dir}/${p}${t}.svg"
    else
      echo -e "${R}not copied ${p}${t}${Z}"
    fi
  done
}

echo -e "${Y}copy to reference${Z} ${K}${dirdest}${Z}"
mkdir -p ${dirdest}
process "${dirdest}" "" ${poses}
process "${dirdest}/relations" "" ${rels}
process "${dirdest}/relations" "role_" ${morphs}
