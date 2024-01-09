#!/bin/bash

#find . -name "artwork" -type d
find . \( -type d -o -type l \) -name "artwork*"
