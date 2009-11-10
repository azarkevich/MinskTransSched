#!/bin/bash

for r in tmp/route-*; do
	route=${r##*-}
	
	for s in $r/sched.*; do
		./31-convert-sched.sh $s
	done
	
done