#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

# WORDNET RELATIONS

export core="hypernym hyponym hypernym_instance hyponym_instance troponym holonym meronym holonym_member meronym_member holonym_part meronym_part holonym_substance meronym_substance antonym"
export verb="causes caused entails entailed verbgroup"
export adj="similar attribute"
export deriv="derivation derivation_adj participle pertainym"
export both="also"
export domain="domain hasdomain domain_topic hasdomain_topic domain_region hasdomain_region domain_usage hasdomain_usage"
export role="role_agent role_bodypart role_bymeansof role_destination role_event role_instrument role_location role_material role_property role_result role_state role_undergoer role_uses role_vehicle"
export collocation="collocation"

obsoleted="synonym other domain_term hasdomain_term"

export all_relations_wn31="${core} ${verb} ${adj} ${deriv} ${both} ${domain}"
export all_relations="${all_relations_wn31} ${collocation} ${role}"
