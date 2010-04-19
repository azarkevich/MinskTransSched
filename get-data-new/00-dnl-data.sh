#!/bin/bash

mkdir -p tmp
cd tmp

rm -f *

wget http://www.minsktrans.by/city/minsk/stops.txt
wget http://www.minsktrans.by/city/minsk/routes.txt
wget http://www.minsktrans.by/city/minsk/times.txt
