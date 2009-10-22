#!/bin/bash

mkdir -p tmp
# wget http://minsktrans.by/scity.php -O - > tmp/index.html
sed -rne '/numberstop/ {
	s/<tr[^>]*>/\n/g
	p
}
' tmp/index.html > tmp/index-str.html
