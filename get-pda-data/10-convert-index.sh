#!/bin/bash

cat tmp/index.html | sed -re 's/&(route=[0-9]+)/\n\1\n/g' | sed -nre 's/^route=([0-9]+)$/\1/
T
p' | /usr/bin/sort -n | uniq > tmp/index
