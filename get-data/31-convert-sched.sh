#!/bin/bash

# forward & backward file paths
f=$(dirname $1)/fwd-$(basename $1)
b=$(dirname $1)/bwd-$(basename $1)

cat $1 | sed -nre '
/Контрольные пункты/ {
	s/<table/\n<table/g
	p
}
' | sed -re '
1,2 d
3~3 d
/times_namestop/ {
	s/^.*<a href=.*>([^<]*)<\/a>.*$/\\busstop \1/
	n
}
'> $(dirname $1)/a~

cat $(dirname $1)/a~ | sed -nre '1,/Обратное следование/ p' > $f
cat $(dirname $1)/a~ | sed -re  '1,/Обратное следование/ d' > $b~

rm $(dirname $1)/a~

# revert backward file
./32-revert-file.py $b~ > $b
rm $b~

for s in $f $b; do
	cat $s | sed -re '
s/<tr/\n<tr/g
' | sed -re '
/<table/ d
s/^.*<b>([0-9]*)<\/b><\/td>/\1 /
s/<a[^>]*> ?([0-9]*)<\/a>/\1 /g
s/<[^>]*>//g
' > $s+
	rm $s
	mv $s+ $s
done
