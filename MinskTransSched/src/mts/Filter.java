package mts;

import java.util.Hashtable;
import java.util.Vector;

import om.*;


public class Filter
{
	BusStop[] busStops;
	Hashtable busStopsFilter;

	Bus[] buses;
	Hashtable busesFilter;

	public boolean isEmpty()
	{
		return busStops == null && buses == null;
	}

	public void setBusesFilter(Bus[] filter)
	{
		buses = filter;
		filteredBusesHash = null;
		if(buses == null || buses.length == 0)
		{
			busesFilter = null;
			buses = null;
			return;
		}
		
		busesFilter = new Hashtable(buses.length);
		for (int i = 0; i < buses.length; i++)
		{
			if(busesFilter.containsKey(buses[i]) == false)
				busesFilter.put(buses[i], buses[i]);
		}
	}
	
	public void setBusStopsFilter(BusStop[] filter)
	{
		busStops = filter;
		filteredBusesHash = null;
		if(busStops == null || busStops.length == 0)
		{
			busStopsFilter = null;
			busStops = null;
			return;
		}
		
		busStopsFilter = new Hashtable(busStops.length);
		for (int i = 0; i < busStops.length; i++)
		{
			if(busStopsFilter.containsKey(busStops[i]) == false)
				busStopsFilter.put(busStops[i], busStops[i]);
		}
	}
	
	Hashtable filteredBusesHash;

	public Hashtable getFilteredBusesHash()
	{
		if(filteredBusesHash != null)
			return filteredBusesHash;
		
		// put all buses which can appear in schedule with current filter
		BusStop[] bss = FilterIt(TransSched.allBusStopsArray);
		filteredBusesHash = new Hashtable();
		for (int i = 0; i < bss.length; i++)
		{
			BusStop bs = bss[i];
			Schedule[] filteredScheds = FilterIt(bs.schedules);
			for (int j = 0; j < filteredScheds.length; j++)
			{
				Bus b = filteredScheds[j].bus;
				if(filteredBusesHash.containsKey(b) == false)
					filteredBusesHash.put(b, b);
			}
		}
		return filteredBusesHash;
	}
	
	public BusStop[] FilterIt(BusStop[] all)
	{
		Vector v = new Vector();
		for (int i = 0; i < all.length; i++)
		{
			BusStop bs = all[i];
			
			if(busStopsFilter != null && !busStopsFilter.containsKey(bs))
				continue;

			boolean add = true;
			if(busesFilter != null)
			{
				add = false;
				for (int j = 0; j < bs.schedules.length; j++)
				{
					if(busesFilter.containsKey(bs.schedules[j].bus))
					{
						add = true;
						break;
					}
				}
			}
			if(add)
				v.addElement(bs);
		}
		
		BusStop[] ret = new BusStop[v.size()];
		v.copyInto(ret);
		return ret;
	}
	
	public BusStop[] getFavorites(BusStop[] all)
	{
		Vector v = new Vector();
		for (int i = 0; i < all.length; i++)
		{
			BusStop bs = all[i];
			if(bs.favorite == false)
				continue;

			v.addElement(bs);
		}
		
		BusStop[] ret = new BusStop[v.size()];
		v.copyInto(ret);
		return ret;
	}
	
	public Bus[] getFavorites(Bus[] all)
	{
		Vector v = new Vector();
		for (int i = 0; i < all.length; i++)
		{
			Bus b = all[i];
			if(b.favorite == false)
				continue;

			v.addElement(b);
		}
		
		Bus[] ret = new Bus[v.size()];
		v.copyInto(ret);
		return ret;
	}
	
	public Schedule[] FilterIt(Schedule[] all)
	{
		Vector v = new Vector();
		for (int i = 0; i < all.length; i++)
		{
			Schedule sched = all[i];
			if(busesFilter != null && !busesFilter.containsKey(sched.bus))
				continue;
			v.addElement(sched);
		}
		
		Schedule[] ret = new Schedule[v.size()];
		v.copyInto(ret);
		return ret;
	}
}
