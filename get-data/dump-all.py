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
		for sid in route.stops:
			stop = all_stops[sid]
			for (days, tt) in route.timetables[sid].items():
				print 'tr=%s;rid=%s;route=%s;rname=%s;sid=%d;sname=%s;street=%s;days=%s;times=%s' % \
					(route.trans, rid, route.route, route.name, sid, stop.name, stop.street, days, gen_rawtimetable(tt))

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
