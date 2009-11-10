#!/bin/bash

rm tmp/route-* -fR

cat tmp/index | while read l; do

	route=${l#route=}
	route=${route%% *}
	
	bus=${l#* bus=}
	bus=${bus%% *}
	
	days=${l#* day=}
	
	mkdir -p tmp/route-$route
	echo -n $bus > tmp/route-$route/name

	for day in $days; do
		wget -O tmp/route-$route/sched.$day "http://minsktrans.by/scity.php?day=$day&route=$route"
	done
	
	break

done

