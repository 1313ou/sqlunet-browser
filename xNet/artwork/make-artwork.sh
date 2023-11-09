#!/bin/bash

source "./lib-artwork.sh"

icon_list="ic_collapsed.svg ic_expanded.svg"
things_list="definition.svg members.svg member.svg pos.svg relation.svg
roleclass.svg roles.svg role.svg 
sample.svg sentence.svg"
storage_list="ic_storage*.svg"
domains_list="bnc.svg wordnet.svg verbnet.svg propbank.svg framenet.svg predicatematrix.svg syntagnet.svg"
assets_list="${domains_list} item.svg pos.svg domain.svg synset.svg synsetmember.svg members.svg member.svg definition.svg roleclass.svg roles.svg role.svg rolex.svg sentence.svg sample.svg relation.svg"

make_res "${storage_list}" 24
make_res "${icon_list}" 16
make_res "${things_list}" 16 h

make_clear_asset "${assets_list}" 16 "${dirassets}/images/xnet"
make_help "${domains_list}" 24
make_help "sqlunet.svg" 500
make_help "roles.svg member.svg" 32

check
