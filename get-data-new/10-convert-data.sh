#!/bin/bash

cd tmp

cat stops.txt  | dd bs=1 skip=3 2> /dev/null | cut -d ";" -f 1,4,5,8 > stops2.txt
cat routes.txt | dd bs=1 skip=3 2> /dev/null | cut -d ";" -f 1,4,11,12,13,15 > routes2.txt
cat times.txt  | dd bs=1 skip=3 2> /dev/null > times2.txt
