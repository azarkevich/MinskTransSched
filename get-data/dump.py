#!/usr/bin/python 
# coding=utf-8

import codecs
import cPickle
import optparse
import itertools
import sys
import os

# set default encoding
reload(sys)
sys.setdefaultencoding('utf-8')

class Route:
	None

class Stop:
	None

class Routes:
	None

# convert time to (hour, minutes)
def to_hm(t):
	if t < 0:
		return (0, 0, t)
	h = t / 60
	m = t - h * 60
	if h > 23:
		h = h - 24
	return (h, m, t)

# print timetable
def print_rawtimetable(tt):
	tt = map(to_hm, filter(lambda x: x >= 0, tt))

	last_hour = -1
	for (h, m, orig) in tt:
		if last_hour != -1 and last_hour != h:
			print
		if last_hour != h:
			last_hour = h
			if h < 10:
				h = '0' + str(h)
			print h ,
		if m < 10:
			m = '0' + str(m)
		print m, 
	print
	
def print_timetable(route, stop):
	tt = route.timetables[stop.id]
	for days in tt:
		if days == '1234567':
			print '\\day w;h'
		if days == '12345':
			print '\\day w'
		if days == '67':
			print '\\day h'
	
		day_tt = tt[days]
		
		print_rawtimetable(day_tt)
	
def dump(root, path):
	all_routes = root["routes"]
	all_stops = root["stops"]
	with open(path) as f:
		for l in [l.strip() for l in f.readlines()]:
			if len(l) == 0 or l.startswith('#'):
				continue
			a = l.split(':')
			if len(a) != 3:
				print 'Wrong line:', l
				exit()
			rid, sid, sname = a

			# select 
			route = all_routes.by_id[rid]
			stop = all_stops[int(sid)]

			if sname == None or len(sname) == 0:
				sname = stop.name

			print '\\' + route.trans, route.route
			print '\\busstop', sname

			print_timetable(route, stop)
	


def main():
	op = optparse.OptionParser(usage='%prog [options] dump.dat ...')

	op.add_option('-e', '--encoding', action='store', dest='encoding', help='Output encoding')

	(options, args) = op.parse_args()
	if len(args) == 0:
		op.print_help()
		exit()

	# set encoding
	enc = sys.stdout.encoding
	if enc == None:
		enc = 'utf-8'
	if options.encoding != None:
		enc = options.encoding
	sys.stdout = codecs.getwriter(enc)(sys.stdout, errors='replace')

	with open(".tmp/data.bin") as f:
		root = cPickle.load(f)

	for a in args:
		if os.path.isfile(a) == False:
			print 'Can\'t find inputfile ' + a
		else:
			dump(root, a)
		
main()