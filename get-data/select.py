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
def print_rawtimetable(tt, is_dump):
	tt = map(to_hm, filter(lambda x: x >= 0, tt))

	last_hour = -1
	for (h, m, orig) in tt:
		if last_hour != -1 and last_hour != h:
			print
		if last_hour != h:
			last_hour = h
			if h < 10:
				h = '0' + str(h)
			print h, ':' ,
		if m < 10:
			m = '0' + str(m)
		print m, 
	print

	
def print_timetable(route, stop, is_dump):
	tt = route.timetables[stop.id]
	if is_dump:
		for days in tt:
			if days == '1234567':
				print '\\day w;h'
			if days == '12345':
				print '\\day w'
			if days == '67':
				print '\\day h'
		
			day_tt = tt[days]
			
			print_rawtimetable(day_tt, is_dump)
	else:
		print '\t\tDays:', tt.keys()

def get_name(id, default):
	id = int(id)
	if id in names_mapping:
		return names_mapping[id]
	return default
	
def print_route(route, is_dump, output):
	if is_dump:
		print '\\' + route.trans, unicode(route.route, 'utf-8').encode('cp866')
	else:
		if 'ri' in output:
			print 'ID:', route.id
		if 'rt' in output:
			print 'Type:', route.trans
		if 'rn' in output:
			print 'Name:', unicode(route.route, 'utf-8')
		if 'rd' in output:
			print 'Desc:', unicode(get_name(route.id, route.name), 'utf-8')

def print_stop(stop, is_dump, output):
	if is_dump:
		print '\\busstop', unicode(get_name(stop.id, stop.name), 'utf-8')
	else:
		if 'si' in output:
			print '\tID:', stop.id
		if 'sn' in output:
			print '\tName:', unicode(get_name(stop.id, stop.name), 'utf-8')

def select(root, froutes, fstops, ftrans, is_dump, output):
	all_routes = root["routes"]
	all_stops = root["stops"]
	for r in all_routes.by_id:
		if froutes != None and r not in froutes:
			continue
		route = all_routes.by_id[r]

		if ftrans != None and route.trans not in ftrans:
			continue

		if options.route_names_rx != None and options.route_names_rx.match(route.route) == None:
			continue

		print_route(route, is_dump, output)

		if is_dump or 's' in output:
			for s in route.stops:
				if fstops != None and s not in fstops:
					continue
				stop = all_stops[s]
				print_stop(stop, is_dump, output)
				print_timetable(route, stop, is_dump)

def main():
	op = optparse.OptionParser()

	opts = optparse.OptionGroup(op, 'Select conditions', 'Used for select entities from DB. Use \',\' separator for specify multiple conditions by OR, or specify condition multiple times')
	opts.add_option('-r', '--route', action='append', dest='route_ids', metavar='IDs', help='Specify route id')
	opts.add_option('', '--route-name-rx', action='store', dest='route_names_rx', metavar='Names', help='Specify route name regex (25, 113.* ...)')
	opts.add_option('-s', '--stop', action='append', dest='stop_ids', metavar='IDs', help='Specify stop id')
	opts.add_option('-t', '--transport', action='append', dest='trans_names', metavar='Names', help='Specify transport names (tram, bus, trol)')
	opts.add_option('-o', '--output', action='store', dest='output', metavar='filter', help='''Specify output filter. Not for Dump mode. 
ri, si - show route/stop ID, 
rd - show route description, 
rn - show route name,
rt - show transport type
s - show stops,
sn - show stop name,
ss - show stop street,
d - show active days,
t - show timetable;
default=ri,si,rn,rd,rt,s
''', default="ri,si,rn,rd,s,sn")
	
	#op.add_option(None, '--mappings-file', action='append', dest='mappings_file', metavar='file', help='Specify file with mappings. Each line is <ID NewName>')
	op.add_option('-m', '--map-name', nargs=2, action='append', dest='names_mapping', help='Specify ID and new Name for this entity', default = [])
	op.add_option_group(opts)

	op.add_option('-d', '--dump', action='store_true', dest='is_dump', default=False, help='Dump data for MinskTransSched')

	global options
	(options, args) = op.parse_args()

	if options.route_ids != None:
		options.route_ids = list(itertools.chain(*[ids.split(',') for ids in options.route_ids]))
		options.route_ids = list(set(options.route_ids))
	if options.stop_ids != None:
		options.stop_ids = list(itertools.chain(*[ids.split(',') for ids in options.stop_ids]))
		options.stop_ids = [int(s) for s in list(set(options.stop_ids))]
	if options.trans_names != None:
		options.trans_names = list(itertools.chain(*[ids.split(',') for ids in options.trans_names]))
		options.trans_names = list(set([x.lower() for x in options.trans_names]))
	if options.route_names_rx != None:
		options.route_names_rx = re.compile(options.route_names_rx)

	options.output = [x.lower() for x in options.output.split(',')]

#	print options.route_ids, options.stop_ids, options.trans_names

	global names_mapping
	names_mapping = dict([(int(id), name) for (id, name) in options.names_mapping])

	with open("tmp/data.bin") as f:
		root = cPickle.load(f)

	select(root, options.route_ids, options.stop_ids, options.trans_names, options.is_dump, options.output)
		
main()