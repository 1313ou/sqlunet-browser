#!/usr/bin/env python3
# coding=utf-8

from svgutils.compose import *
import sys
 
# Get the total number of args passed to the demo.py
argsn = len(sys.argv)
out=sys.argv[1]
in_base=sys.argv[2]
in_fore=sys.argv[3]
#print(out)
#print(in_base)
#print(in_base)

# Get the config
CONFIG["svg.file_path"] = "parts"
CONFIG["figure.save_path"] = "composite"
dim="128px"
base_scale=1.
fore_scale=1.

#Process
Figure(
    dim,
    dim,
    SVG(in_base).scale(base_scale),
    SVG(in_fore).scale(fore_scale),
).save(out)
