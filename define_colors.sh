#!/bin/bash

# ANSI COLORS

export BLACK='\u001b[30m'
export RED='\u001b[31m'
export GREEN='\u001b[32m'
export BLUE='\u001b[34m'
export YELLOW='\u001b[33m'
export MAGENTA='\u001b[35m'
export CYAN='\u001b[36m'
export WHITE='\u001b[37m'

export BG_BLACK='\u001b[40m'
export BG_RED='\u001b[41m'
export BG_GREEN='\u001b[42m'
export BG_YELLOW='\u001b[43m'
export BG_BLUE='\u001b[44m'
export BG_MAGENTA='\u001b[45m'
export BG_CYAN='\u001b[46m'
export BG_WHITE='\u001b[47m'

export LIGHT_BLACK='\u001b[90m'
export LIGHT_RED='\u001b[91m'
export LIGHT_GREEN='\u001b[92m'
export LIGHT_YELLOW='\u001b[93m'
export LIGHT_BLUE='\u001b[94m'
export LIGHT_MAGENTA='\u001b[95m'
export LIGHT_CYAN='\u001b[96m'
export LIGHT_WHITE='\u001b[97m'

export LIGHT_BG_BLACK='\u001b[100m'
export LIGHT_BG_RED='\u001b[101m'
export LIGHT_BG_GREEN='\u001b[102m'
export LIGHT_BG_YELLOW='\u001b[103m'
export LIGHT_BG_BLUE='\u001b[104m'
export LIGHT_BG_MAGENTA='\u001b[105m'
export LIGHT_BG_CYAN='\u001b[106m'
export LIGHT_BG_WHITE='\u001b[107m'

export BOLD='\u001b[1m'
export STOP_BOLD='\u001b[21m'
export UNDERLINE='\u001b[4m'
export STOP_UNDERLINE='\u001b[24m'
export BLINK='\u001b[5m'

export RESET='\u001b[0m'

export R=$RED
export G=$GREEN
export B=$BLUE
export Y=$YELLOW
export M=$MAGENTA
export C=$CYAN
export W=$WHITE

export Rl=$LIGHT_RED
export Gl=$LIGHT_GREEN
export Bl=$LIGHT_BLUE
export Yl=$LIGHT_YELLOW
export Ml=$LIGHT_MAGENTA
export Cl=$LIGHT_CYAN
export Wl=$LIGHT_WHITE

export bR=$BG_RED
export bG=$BG_GREEN
export bB=$BG_BLUE
export bY=$BG_YELLOW
export bM=$BG_MAGENTA
export bC=$BG_CYAN
export bW=$BG_WHITE

export E=$BOLD
export ZE=$STOP_BOLD

export Z=$RESET

export RERR='2> >(while read line; do echo -e "${R}$line${Z}" >&2; done)'



