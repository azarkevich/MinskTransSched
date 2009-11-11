#!/bin/bash

mkdir -p tmp
echo "Get index"
wget -q http://minsktrans.by/scity.php -O - > tmp/index.html
