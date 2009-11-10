#!/bin/bash

cat tmp/index.html | sed -rne '/numberstop/ {
	s/<tr[^>]*>/\r\n/g
	p
}
' | sed -re '
1 d
s/^<td[^>]*>([^<]*)<\/td>/#bus=\1 #/
s/^(.*)route=([0-9]*)/#route=\2 #\1/
s/day=([0-9]*)/#day=\1#/g
s/^[^#]*#//
s/#[^#]*#//g
s/#[^#]*$//g
s/([^ ])day=/\1 /g
' > tmp/index
