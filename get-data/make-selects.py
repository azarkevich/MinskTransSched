#!/usr/bin/python 
# coding=utf-8

import codecs
import cPickle
import optparse
import itertools
import sys
import re

# set default encoding
reload(sys)
sys.setdefaultencoding('utf-8')

enc = sys.stdout.encoding
if enc == None:
	enc = 'utf-8'
sys.stdout = codecs.getwriter(enc)(sys.stdout, errors='replace')

class Route:
	None

class Stop:
	None

class Routes:
	None

def main():

	with open("tmp/data.bin") as f:
		root = cPickle.load(f)

	all_routes = root["routes"]
	all_stops = root["stops"]

	for (tr, rname), routes in all_routes.by_route.items():
		for route in routes:
			print '#', tr, rname, '(' + str(route.id) + ')', route.name

			for sid in route.stops:
				stop = all_stops[sid]
				print str(route.id) + ':' + str(stop.id) + ':' + stop.name
			print
main()