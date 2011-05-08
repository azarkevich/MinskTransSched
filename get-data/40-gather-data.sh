#!/bin/bash

cd .tmp/
sed -nre '/tr=bus/p' all.txt > all.buses.txt

cat all.buses.txt | grep ';route=143;rname=Зеленый бор - ДС Карбышева;'
