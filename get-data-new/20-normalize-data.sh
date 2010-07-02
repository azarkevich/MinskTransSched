#!/bin/bash

cd tmp

cat routes2.txt | gawk -F ";" '
	$1!="" {
		ref = $1
	}
	$2!="" {
		type = $2
	}
	##{ printf ("id=%s;ref=%s;type=%s;work=%s;route=%s;stops=%s\n", $5, ref, type, $4, $3, $6) }
	{ printf ("%s;%s;%s;%s;%s;%s\n", ref, type, $3, $4, $5, $6) }
' > routes3.txt

cat stops2.txt | gawk -F ";" '
	$2!="" {
		street = $2
	}
	$3!="" {
		name = $3
	}
	{ printf ("%s;%s;%s;%s\n", $1, street, name, $4) }
' > stops3.txt
