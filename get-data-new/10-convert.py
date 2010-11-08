#!/usr/bin/python 
# coding=utf-8

import codecs
import cPickle

class Route:
	def __init__(self, id, route, name, trans, days, stops):
		self.id = int(id)
		self.route = route
		self.name = name
		self.trans = trans
		self.days = days
		self.stops = stops

class Stop:
	def __init__(self, id, name, street):
		self.id = int(id)
		self.name = name
		self.street = street

class Routes:
	def __init__(self, routes_by_id):
		self.by_id = routes_by_id
		# gen by route
		self.by_route = {}
		for id in self.by_id:
			r = self.by_id[id]
			key = (r.trans, r.route)
			list = []
			if key in self.by_route:
				list = self.by_route[key]
			else:
				self.by_route[key] = list

			list.append(r)

		self.by_trans = {}
		for key in self.by_route:
			(trans, route) = key
			rr = self.by_route[key]
			dict = {}
			if trans in self.by_trans:
				dict = self.by_trans[trans]
			else:
				self.by_trans[trans] = dict

			dict[route] = rr

# convert stop id from list to normalized form (remove e, x prefixes and convert to int)
def convert_stop(stop):
	if stop[0] == 'e' or stop[0] == 'x':
		stop = stop[1:]
	return int(stop)

# read routes from file
def read_routes():
	global routes

	f = codecs.open('tmp/routes.txt', 'rb', 'utf-8-sig')
	
	text = f.read().replace('\r', '<CR>')
	lines = filter(lambda x: x.strip() != '', text.split('\n'))

	col_route = 0
	col_trans = 3
	col_name = 10
	col_days = 11
	col_id = 12
	col_stops = 14

	route=''
	trans=''
	name=''
	days=''

	rr = {}

	for line in lines[1:]:
		row = line.split(';')
		if row[col_route] != '':
			route=row[col_route]
	
		if row[col_trans] != '':
			trans=row[col_trans]

		if row[col_name] != '':
			name = row[col_name]

		if row[col_days] != '':
			days = row[col_days]

		id = row[col_id]
		stops = row[col_stops].split(',')
		stops = filter(lambda x: x != '', stops)
		stops = map(convert_stop, stops)
		#print id, name.encode('utf-8'), stops

		rr[id] = Route(int(id), route.encode('utf-8'), name.encode('utf-8'), trans.encode('utf-8'), days.encode('utf-8'), stops)

	routes = Routes(rr)

# read stops from file
def read_stops():
	global stops

	f = codecs.open('tmp/stops.txt', 'rb', 'utf-8-sig')
	
	text = f.read().replace('\r', '<CR>')
	lines = filter(lambda x: x.strip() != '', text.split('\n'))

	col_id = 0
	col_street = 3
	col_name = 4

	street=''
	name=''

	stops = {}

	for line in lines[1:]:
		row = line.split(';')
#		print line
#		print row
#		print
		id = int(row[col_id])

		if row[col_street] != '':
			street = row[col_street]
	
		if row[col_name] != '':
			name = row[col_name]

		s = street
		if s == '0':
			s = ''

		#print name.encode('utf-8'), '|', s.encode('utf-8')
		stops[id] = Stop(id, name.encode('utf-8'), s)

# decode references from '[key1, width1, key2, width2, key3]' to [(key1, (begin1, end1)), (key2, (begin2, end2), (key3, (begin3, end3)))]
# where (beginX, endX] indexes in referenced list
def decode_ref(lst, ref_lst):
	refs = []
	start_index = 0
	end_index = 0
	i = 0
	while i < len(lst):
		item = lst[i]
		i = i + 1
		start_index = end_index
		if i < len(lst):
			end_index = end_index + int(lst[i])
			i = i + 1
		else:
			end_index = len(ref_lst)
		refs.append((item.encode('utf-8'), (start_index, end_index)))

	return refs

# split list by empty items
def split_by_space(lst):
	ret = []
	cur = []
	for i in lst:
		if i == '\r' or i == '\n':
			continue
		if i == '':
			ret.append(cur)
			cur = []
		else:
			cur.append(i)
	if len(cur) > 0:
		ret.append(cur)
	
	return ret

# split list by references
def split_by_refs(lst, refs):
	ret = {}
	for (ref, (b, e)) in refs:
		ret[ref] = lst[b:e]
	return ret

# strange '-5' fix
def fix_deltas(deltas):
	dt = 5
	fixed_deltas = []
	for (delta, bounds) in deltas:
		dt = dt + int(delta) - 5
		fixed_deltas.append((dt, bounds))
	return fixed_deltas

# read times file
def read_times():

	f = codecs.open('tmp/times.txt', 'rb', 'utf-8-sig')
	
	text = f.read().replace('\r', '<CR>')
	lines = filter(lambda x: x.strip() != '', text.split('\n'))

	for line in lines:
		row = line.split(',')

		# route
		r = routes.by_id[row[0]]
		#print r.route, r.name, r.id

		#print "LINE:", line
		#print "ROW:", row

		ll = split_by_space(row[1:])

		# split single list into sublists
		
		# first - timetable of starts from initial stop. Stored as diffs
		timetable = []
		x = 0
		for c in ll[0]:
			x = x + int(c)
			timetable.append(x)
		#print timetable

		# references of days into timetable
		day_refs = decode_ref(ll[3], timetable)
		
		# all other - differences between stops. add first element as zero difference and zip with stop id
		diffs = map(lambda x: decode_ref(x, timetable), ll[4:])
		diffs.insert(0, [])
		diffs = map(fix_deltas, diffs)
		diffs = zip(r.stops, diffs)

		#print "RouteId:", route_id
		#print "Timetable:", timetable
		#print "DayRefs:", day_refs
		#print "Diffs:"
		#for i in diffs:
		#	print i


		# calc timetables for each stop:
		r.timetables = {}
		for (stop, diffs_list) in diffs:
			#print stop, stops[stop].name
			#print "TT BEFORE:", len(timetable), timetable
			#print "PARTS:"
			#for (k,p) in split_by_refs(timetable, diffs_list).items():
			#	print k,p
			#print "PARTS$"
			for (d, (b, e)) in diffs_list:
				#print "DIFF:", d, "FOR:", b, e
				for i in range(b, e):
					timetable[i] = timetable[i] + int(d)
			#print "TT AFTER :", len(timetable), timetable
			#print "PARTS:"
			#for (k,p) in split_by_refs(timetable, diffs_list).items():
			#	print k,p
			#print "PARTS$"
			#print
			# split timetable to days
			r.timetables[stop] = split_by_refs(timetable, day_refs)

		#print r.timetables

read_routes()
read_stops()
read_times()

root = {}
root["stops"] = stops
root["routes"] = routes

f = open("tmp/data.bin", 'wb')
cPickle.dump(root, f)
