#!/bin/bash

source "./lib-artwork.sh"

icon_list="ic_collapsed.svg ic_expanded.svg ic_leaf.svg"
icon2_list="error.svg info.svg item.svg"
things_list="definition.svg domain.svg link.svg members.svg member.svg pos.svg relation.svg
role1.svg role2.svg roleclass.svg roles.svg role.svg rolex2.svg rolex.svg 
sample.svg sentence.svg
synsetmember.svg synset.svg"
storage_list="ic_storage*.svg"
domains_list="bnc.svg wordnet.svg verbnet.svg propbank.svg framenet.svg predicatematrix.svg"
assets_list="${domains_list} item.svg pos.svg domain.svg synset.svg synsetmember.svg members.svg member.svg definition.svg roleclass.svg roles.svg role.svg rolex.svg rolex2.svg sentence.svg sample.svg relation.svg"

make_res "${storage_list}" 32
make_res "${icon_list}" 16
make_res "${icon2_list}" 16
make_res "${things_list}" 16 h
make_res "${domains_list}" 16

make_icon "${assets_list}" 16 "${dirassets}/images/xnet"
make_help "${domains_list}" 24
make_help "sqlunet.svg" 500
make_help "roles.svg member.svg" 32

check
