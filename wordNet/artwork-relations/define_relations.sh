#!/bin/bash

declare -A relations
export relations=(

[gen]="
relations"

[sem]="
hypernym
hyponym
troponym
instance_hypernym
instance_hyponym
holonym
meronym
part_holonym
part_meronym
member_holonym
member_meronym
substance_holonym
substance_meronym
causes
caused
entails
entailed
attribute
similar
verb_group"

[lex]="
antonym
participle
pertainym
derivation
adjderived"

[collocation]="
collocation"

[both]="
also
other"

[dom]="
domain
domain_member
domain_topic
domain_member_topic
domain_region
domain_member_region
domain_term
domain_member_term
exemplifies
exemplified"

[role]="
state
result
event
property
location
destination
agent
undergoer
uses
instrument
bymeansof
material
vehicle
bodypart
all_roles"

[pos]="
pos_n
pos_v
pos_a
pos_r
pos_s
pos"
)

export poses="${relations['pos']}"
export roles="${relations['role']}"
export rels="
${relations['gen']}
${relations['sem']}
${relations['lex']}
${relations['both']}
${relations['dom']}
${relations['collocation']}
"