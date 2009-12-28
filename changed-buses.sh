#!/bin/bash

FILE=data/Minsk/base/inet-base.txt

LINES=$(svn diff $FILE --diff-cmd diff -x '-C 0' 	\
	| iconv -f cp1251 -t utf-8		\
	| sed -nre '/^--- [0-9]+/p' 		\
	| sed -re 's/--- ([0-9]+(,[0-9]+)?).*/\1/')

rm /tmp/buses

function find_bus
{
	sed -nre "1,$1 p" $FILE | sed -nre '/\\bus[ \t]/ p' | tail -n 1 | sed -re 's/\\bus[ \t]//' >> /tmp/buses
}

for l in $LINES; do
	if [[ $l =~ ([0-9]+),([0-9]+) ]];
	then
		for (( c=${BASH_REMATCH[1]}; c<=${BASH_REMATCH[2]}; c++ )); do
			find_bus $c
		done
	else
		find_bus $l
	fi
done


VER_MAJOR=$(sed -nre '/version.major/p' build.properties | sed -re 's/.*=//')
VER_MINOR=$(sed -nre '/version.minor/p' build.properties | sed -re 's/.*=//')

echo "$VER_MAJOR.$VER_MINOR:"
echo -n "	Changed buses: "

for bus in $(uniq /tmp/buses); do
	echo -n "$bus "
done

echo