package com.mts;

import java.util.Hashtable;
import java.util.Vector;

public class Filter
{
	Hashtable busesFilter;
	Hashtable busStopsFilter;

	public Filter()
	{
	}

	public Filter(Hashtable busesFilter, Hashtable busStopsFilter)
	{
		this.busesFilter = busesFilter;
		this.busStopsFilter = busStopsFilter;
	}

	public void setBusesFilter(Bus[] buses)
	{
		if(buses == null)
		{
			busesFilter = null;
			return;
		}
		
		busesFilter = new Hashtable(buses.length);
		for (int i = 0; i < buses.length; i++)
		{
			busesFilter.put(buses[i], buses[i]);
		}
	}
	
	public void setBusStopsFilter(BusStop[] busStops)
	{
		if(busStops == null)
		{
			busStopsFilter = null;
			return;
		}
		
		busStopsFilter = new Hashtable(busStops.length);
		for (int i = 0; i < busStops.length; i++)
		{
			busStopsFilter.put(busStops[i], busStops[i]);
		}
	}
	
	public BusStop[] FilterIt(BusStop[] all)
	{
		return FilterIt(all, false);
	}
	
	public BusStop[] FilterIt(BusStop[] all, boolean onlyFavorites)
	{
		Vector v = new Vector();
		for (int i = 0; i < all.length; i++)
		{
			BusStop bs = all[i];
			if(onlyFavorites && bs.favorite == false)
				continue;
			
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
