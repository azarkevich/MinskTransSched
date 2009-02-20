package com.mts;
import java.io.*;
import java.util.Vector;


public class ScheduleLoader
{
	public BusStop[] busStops;
	private Bus[] buses;
	private Vector schedules;
	
	void Debug(String x)
	{
		busStops = new BusStop[] { new BusStop() };
		
		busStops[0].name = x;
		busStops[0].schedules = new Schedule[] {};
	}
	
	void LoadBuses() throws IOException
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
			buses[i].route = dis.readUTF();
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
			byte timesCount = dis.readByte();
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
		int count = dis.readByte();
		for (int i = 0; i < count; i++)
		{
			// read data
			short bus = dis.readByte();
			short busStop = dis.readByte();
			int day = dis.readByte();
			short baseBusStop = dis.readByte();
			short shift = dis.readByte();
			
			//System.out.println("bus:" + bus + ", bs:" + busStop);
			
			// src
			Schedule baseSched = FindSchedule(bus, baseBusStop);
			
			// dst
			Schedule sched = FindSchedule(bus, busStop);
			if(sched == null)
			{
				sched = new Schedule();

				sched.bus = FindBus(bus);
				sched.busStop = FindBusStop(busStop);
				schedules.addElement(sched);
			}
			
			// generate times:
			short[] srcTimes = baseSched.getTimes(day);
			short[] dstTimes = new short[srcTimes.length];
			for (int j = 0; j < srcTimes.length; j++)
			{
				dstTimes[j] = (short)(srcTimes[j] + shift);
			}
			
			sched.setTimes(day, dstTimes);
			sched.setFrom(day, baseSched.busStop.name + " +" + shift + "m");
		}
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

			LoadBuses();
			LoadBusStops();
			LoadSchedules();
			LoadDerivedSchedules();

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
}
