#!/bin/bash

mkdir -p tmp
echo "Get index"
wget -q http://www.minsktrans.by/city/#minsk/bus -O - > tmp/index.html
