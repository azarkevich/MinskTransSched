import java.io.*;
import java.util.Calendar;

public class ScheduleLoader
{
	public BusStop[] busStops;
	Bus[] buses;
	Schedule[] schedules;
	
	void Debug(String x)
	{
		busStops = new BusStop[] { new BusStop() };
		
		busStops[0].name = x;
		busStops[0].schedules = new Schedule[] {};
	}
	
	void LoadBuses() throws IOException
	{
		// load buses
		DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("buses"));
		short count = dis.readShort();
		buses = new Bus[count];
		for (int i = 0; i < count; i++)
		{
			buses[i] = new Bus();
			buses[i].id = dis.readShort();
			buses[i].Name = dis.readUTF();
			buses[i].description = dis.readUTF();
		}
		dis.close();
	}
	
	static final int[] dayBitNumbers = {
		Calendar.SUNDAY,
		Calendar.MONDAY,
		Calendar.TUESDAY,
		Calendar.WEDNESDAY,
		Calendar.THURSDAY,
		Calendar.FRIDAY,
		Calendar.SATURDAY,
		Schedule.WORKDAY,
		Schedule.HOLIDAY
	};
	
	void LoadSchedules() throws Exception
	{
		// load scheds
		DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("scheds"));
		short count = dis.readShort();
		schedules = new Schedule[count];
		int freeSchedule = 0;
		for (int i = 0; i < count; i++)
		{
			short bus = dis.readShort();
			short busStop = dis.readShort();
			Schedule sched = FindSchedule(bus, busStop);
			if(sched == null)
			{
				sched = new Schedule();

				sched.bus = FindBus(bus);
				sched.busStop = FindBusStop(busStop);
				schedules[freeSchedule++] = sched;
			}
			
			short days = dis.readShort();
			
			short[] times = new short[dis.readShort()];
			for (int t = 0; t < times.length; t++)
			{
				times[t] = dis.readShort();
			}

			for (int bitIndex = 0; bitIndex < dayBitNumbers.length; bitIndex++)
			{
				int bit = dayBitNumbers[bitIndex];
				short mask = (short)(1 << bit);
				if((days & mask) == mask)
				{
					sched.setTimes(bit, times);
				}
			}
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
		for (int i = 0; i < schedules.length; i++)
		{
			if(schedules[i] != null && schedules[i].bus.id == bus && schedules[i].busStop.id == busStop)
				return schedules[i];
		}
		return null;
	}
	
	void LoadBusStops() throws IOException
	{
		// load buses
		DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("busStops"));
		short count = dis.readShort();
		busStops = new BusStop[count];
		for (int i = 0; i < count; i++)
		{
			busStops[i] = new BusStop();
			busStops[i].id = dis.readShort();
			busStops[i].name = dis.readUTF();
			busStops[i].officialName = dis.readUTF();
			busStops[i].description = dis.readUTF();
		}
		dis.close();
	}
	
	public void Load()
	{
		try{
			LoadBuses();
			LoadBusStops();
			LoadSchedules();

			for (int s = 0; s < schedules.length; s++)
			{
				if(schedules[s] != null)
				{
					schedules[s].NormalizeDays();
				}
			}

			// link scheds to busstops
			for (int bs = 0; bs < busStops.length; bs++)
			{
				// calc count of schedules for it
				BusStop stop = busStops[bs];
				int count = 0;
				for (int s = 0; s < schedules.length && schedules[s] != null; s++)
				{
					if(schedules[s] != null && schedules[s].busStop == stop)
						count++;
				}
				stop.schedules = new Schedule[count];
				count = 0;
				for (int s = 0; s < schedules.length && schedules[s] != null; s++)
				{
					if(schedules[s].busStop == stop)
					{
						stop.schedules[count++] = schedules[s];
					}
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
