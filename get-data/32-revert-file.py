#!/usr/bin/python

import sys
import codecs

sys.stdout = codecs.getwriter('cp1251')(sys.stdout)

def revert(lines):
	# strip \n
	lines = [l.replace('\n', '') for l in lines if l != ""]
	# strip empty lines
	lines = [l for l in lines if l != ""]

	if len(lines) % 2 == 1:
		print "Not event lines count: %d" % len(lines)
		sys.exit(-1)
		
	# make pairs
	pairs = [(lines[i*2],lines[i*2+1]) for i in range(0, len(lines)/2)]
	# revert pairs
	pairs.reverse()

	return pairs

def main():
	if len(sys.argv) < 2:
		print "Usage: %s <input file>" % sys.argv[0]
		sys.exit(-1)

	for (a, b) in revert(codecs.open(sys.argv[1], 'rt', 'cp1251').readlines()):
		print a
		print b

main()
