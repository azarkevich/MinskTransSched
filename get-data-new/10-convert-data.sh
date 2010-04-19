#!/bin/bash

cd tmp

cat stops.txt  | dd bs=1 skip=3 2> /dev/null | cut -d ";" -f 1,4,5,8 > stops2.txt
cat times.txt  | dd bs=1 skip=3 2> /dev/null > times2.txt

cat routes.txt | dd bs=1 skip=3 2> /dev/null | cut -d ";" -f 1,4,11,12,13,15 | gawk -F ";" '
	$1!="" {
		ref = $1
	}
	$2!="" {
		type = $2
	}
	##{ printf ("id=%s;ref=%s;type=%s;work=%s;route=%s;stops=%s\n", $5, ref, type, $4, $3, $6) }
	{ printf ("%s;%s;%s;%s;%s;%s\n", ref, type, $3, $4, $5, $6) }
' > routes2.txt
