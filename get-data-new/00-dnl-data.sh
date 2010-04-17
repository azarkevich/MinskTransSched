#!/bin/bash

mkdir -p tmp
cd tmp

rm -f *

wget http://www.minsktrans.by/city/minsk/stops.txt
wget http://www.minsktrans.by/city/minsk/routes.txt
wget http://www.minsktrans.by/city/minsk/times.txt

cat stops.txt | cut -d ";" -f 1,4,5,8 > stops2.txt
cat routes.txt | cut -d ";" -f 1,4,8,11,12,13,15 > routes2.txt

cd ..
