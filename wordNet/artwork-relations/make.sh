#!/bin/bash

set -e

./make-artwork.sh
./make-artwork-test.sh
./copy-artwork-to-reference.sh
