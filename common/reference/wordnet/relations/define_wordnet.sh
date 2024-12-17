#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

# WORDNET

export objects="sense synset word pos"
export pos="pos_n pos_v pos_a pos_s pos_r"
export labels="root category words relations"
export features="feature_symmetric feature_semantic feature_lexical pos_n  pos_v pos_a pos_s pos_r"

export all_wordnet="${objects} ${pos} ${labels} ${features}"