#!/bin/bash

source "../../make-artwork-lib.sh"

smallicon_list="*.svg"
icon_list="ic_storage*.svg"
list_assets="bnc.svg wordnet.svg verbnet.svg propbank.svg framenet.svg"
list_assets="${list_assets} item.svg pos.svg domain.svg synset.svg synsetmember.svg members.svg member.svg definition.svg roleclass.svg roles.svg role.svg rolex.svg rolex2.svg sentence.svg sample.svg relation.svg"

make_res "${smallicon_list}" 16
make_res "${icon_list}" 32

make_assets "${list_assets}" 16

