package resources;
import java.io.*;
import java.util.Vector;

import ObjModel.*;
import options.OptionsStoreManager;


public class ScheduleLoader
{
	public BusStop[] busStops;
	public Bus[] buses;
	public FilterDef[] filters;
	private Vector schedules;
	public int dayEnd;
	
	void Debug(String x)
	{
		busStops = new BusStop[] { new BusStop() };
		
		busStops[0].name = x;
		busStops[0].schedules = new Schedule[] {};
	}
	
	void LoadBuses() throws Exception
	{
		// load buses
		DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("/buses"));
		short count = dis.readByte();
		buses = new Bus[count];
		for (int i = 0; i < count; i++)
		{
			buses[i] = new Bus();
			buses[i].id = dis.readByte();
			buses[i].name = dis.readUTF();
			//System.out.println("Do:" + buses[i].name);
			byte startRoute = dis.readByte();
			if(startRoute != -1)
				buses[i].startRoute = FindBusStop(startRoute);
			byte endRoute = dis.readByte();
			if(endRoute != -1)
				buses[i].endRoute = FindBusStop(endRoute);
		}
		dis.close();
	}
	
	void LoadBusStops() throws IOException
	{
		// load buses
		DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("/busStops"));
		int count = dis.readByte();
		busStops = new BusStop[count];
		for (int i = 0; i < count; i++)
		{
			busStops[i] = new BusStop();
			busStops[i].id = dis.readByte();
			busStops[i].name = dis.readUTF();
			busStops[i].description = dis.readUTF();
			
			//System.out.println("Read bs:" + busStops[i].id);
		}
		dis.close();
	}

	void LoadSchedules() throws Exception
	{
		// load 'scheds'
		DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("/scheds"));
		short count = dis.readByte();
		for (int i = 0; i < count; i++)
		{
			// read data
			short bus = dis.readByte();
			short busStop = dis.readByte();
			int day = dis.readByte();
			byte schedFrom = dis.readByte();
			int timesCount = dis.readUnsignedByte();
			short[] times = new short[timesCount];
			for (int t = 0; t < times.length; t++)
			{
				times[t] = dis.readShort();
			}
			
			// parse
			Schedule sched = FindSchedule(bus, busStop);
			if(sched == null)
			{
				sched = new Schedule();

				sched.bus = FindBus(bus);
				sched.busStop = FindBusStop(busStop);
				schedules.addElement(sched);
			}
			
			if(schedFrom == Schedule.SCHED_FROM_MINSK_TRANS_SITE)
			{
				sched.setFrom(day, "minsktrans.by");
			}
			else if(schedFrom == Schedule.SCHED_FROM_BUSSTOP)
			{
				sched.setFrom(day, "С остановки");
			}

			sched.setTimes(day, times);
		}
		dis.close();
	}
	
	void LoadDerivedSchedules() throws Exception
	{
		// load 'dscheds'
		DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("/dscheds"));
		int count = dis.readShort();
		for (int i = 0; i < count; i++)
		{
			// read data
			short bus = dis.readByte();
			short busStop = dis.readByte();
			int dayFrom = dis.readByte();
			int dayTo = dis.readByte();
			short baseBusStop = dis.readByte();
			short shift = dis.readByte();

			// src
			Schedule srcSched = FindSchedule(bus, baseBusStop);
			if(srcSched == null)
				continue;

			// dst
			Schedule dstSched = FindSchedule(bus, busStop);
			if(dstSched == null)
			{
				dstSched = new Schedule();

				dstSched.bus = FindBus(bus);
				dstSched.busStop = FindBusStop(busStop);
				schedules.addElement(dstSched);
			}
			
			// generate times:
			short[] srcTimes = srcSched.getTimes(dayFrom);
			short[] dstTimes = new short[srcTimes.length];
			for (int j = 0; j < srcTimes.length; j++)
			{
				dstTimes[j] = (short)(srcTimes[j] + shift);
			}

			dstSched.setTimes(dayTo, dstTimes);
			dstSched.setFrom(dayTo, srcSched.busStop.name + " +" + shift + "m");
		}
		dis.close();
	}
	
	void LoadFilters() throws Exception
	{
		// load 'filters'
		DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("/filters"));
		int count = dis.readByte();
		filters = new FilterDef[count];
		for (int i = 0; i < count; i++)
		{
			filters[i] = OptionsStoreManager.readFilterDef(dis);
		}
		dis.close();
	}
	
	void LoadSettings() throws Exception
	{
		// load 'settings'
		DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("/settings"));
		dayEnd = dis.readShort();
		dis.close();
	}

	Bus FindBus(short id) throws Exception
	{
		for (int i = 0; i < buses.length; i++)
		{
			if(buses[i].id == id)
				return buses[i];
		}
		throw new Exception("Unknown bus: " + id);
	}
	
	BusStop FindBusStop(short id) throws Exception
	{
		for (int i = 0; i < busStops.length; i++)
		{
			if(busStops[i].id == id)
				return busStops[i];
		}
		throw new Exception("Unknown bus stop: " + id);
	}
	
	Schedule FindSchedule(short bus, short busStop)
	{
		for (int i = 0; i < schedules.size(); i++)
		{
			Schedule sched = (Schedule)schedules.elementAt(i);
			if(sched != null && sched.bus.id == bus && sched.busStop.id == busStop)
				return sched;
		}
		return null;
	}
	
	public void Load()
	{
		try{
			schedules = new Vector();

			LoadBusStops();
			LoadBuses();
			LoadSchedules();
			LoadDerivedSchedules();
			LoadSettings();
			
			for (int i = 0; i < schedules.size(); i++)
			{
				Schedule sched = (Schedule)schedules.elementAt(i);
				sched.NormalizeDays();
			}
			
			// link scheds to busstops
			for (int bs = 0; bs < busStops.length; bs++)
			{
				// calc count of schedules for it
				BusStop stop = busStops[bs];
				int count = 0;
				for (int s = 0; s < schedules.size(); s++)
				{
					Schedule sched = (Schedule)schedules.elementAt(s);
					if(sched.busStop == stop)
						count++;
				}
				stop.schedules = new Schedule[count];
				count = 0;
				for (int s = 0; s < schedules.size(); s++)
				{
					Schedule sched = (Schedule)schedules.elementAt(s);
					if(sched.busStop == stop)
						stop.schedules[count++] = sched;
				}
			}
		}
		catch(Exception ex)
		{
			Debug(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void LoadFiltersPub()
	{
		try{
			LoadFilters();
		}
		catch(Exception ex)
		{
			Debug(ex.getMessage());
			ex.printStackTrace();
		}
	}
}
