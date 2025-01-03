#!/bin/bash

source "./lib-artwork.sh"

icon_list="syntagnet.svg sncollocation.svg sncollocation1.svg sncollocation2.svg sndefinition1.svg sndefinition2.svg"
asset_list="closed.svg open.svg pointer.svg"
asset_list_syntagnet="syntagnet.svg sncollocation.svg"
asset_list_xnet="member.svg roleclass.svg role.svg rolex.svg sentence.svg definition.svg sample.svg"

make_res "${icon_list}" 16

make_clear_asset "${asset_list}" 16 "${dirassets}/images"
make_clear_asset "${asset_list_syntagnet}" 16 "${dirassets}/images/syntagnet"
make_clear_asset "${asset_list_xnet}" 16 "${dirassets}/images/xnet"

check

