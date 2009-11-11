#!/bin/bash

rm tmp/route-* -fR

for r in $(cat tmp/index); do
	
	mkdir -p tmp/route-$r

	echo "Route: $r"

	for day in 1 3 6 7; do
		wget -q -O tmp/route-$r/index.$day "http://www.minsktrans.by/spda.php?day=$day&route=$r"
	done

done

