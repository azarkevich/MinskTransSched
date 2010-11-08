#!/usr/bin/python 
# coding=utf-8

import codecs
import cPickle

class Route:
	None


class Stop:
	None

class Routes:
	None

f = open("tmp/data.bin")
root = cPickle.load(f)

routes = root["routes"]
stops = root["stops"]

# convert time to (hour, minutes)
def to_hm(t):
	h = t / 60
	m = t - h * 60
	if h > 23:
		h = h - 24
	return (h, m)

# print timetable
def print_timetable(tt):
	tt = map(to_hm, tt)

	last_hour = -1
	for (h, m) in tt:
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

# print route
def print_route(r):
	print r.route, r.name, '(', r.id, ')'
	#print r.stops
	for stop_id in r.stops:
		stop = stops[stop_id]
		print stop.name, stop.id
		stop_timetables = r.timetables[stop_id]
		for days in stop_timetables.keys():
			print ">", days
			print_timetable(stop_timetables[days])

route_filter = '4070'

if route_filter != None:
	print_route(routes.by_id[route_filter])

#for r in routes.by_route[("bus", "515")]:
	#print r.id, r.name
	#print_sched(r)
	#break

