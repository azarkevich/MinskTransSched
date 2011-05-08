#!/usr/bin/env python

import codecs
import cPickle
import sys


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

def gen_rawtimetable(tt):
	tt = map(to_hm, filter(lambda x: x >= 0, tt))

	rtt = ''
	for (h, m, orig) in tt:
		rtt += '%02d:%02d ' % (h, m)

	return rtt
	
def dump(root):
	all_routes = root["routes"]
	all_stops = root["stops"]

	for (rid, route) in all_routes.by_id.items():
		print 'rec=route;transport=' + route.trans + ';id=' + rid + ';route=' + route.route + ';name=' + route.name
		for sid in route.stops:
			stop = all_stops[sid]
			print 'rec=stop;id=' + str(sid) + ';name=' + stop.name + ';street=' + stop.street
			for (days, tt) in route.timetables[sid].items():
				times = gen_rawtimetable(tt)
				print 'rec=timetable;days=' + days + ';times=' + times

	return

def main():
	# set default encoding
	reload(sys)
	sys.setdefaultencoding('utf-8')

	# set stdout encoding
	if sys.stdout.encoding != 'utf-8':
		sys.stdout = codecs.getwriter('utf-8')(sys.stdout, errors='replace')

	with open("data.bin") as f:
		root = cPickle.load(f)
		dump(root)
		
main()
