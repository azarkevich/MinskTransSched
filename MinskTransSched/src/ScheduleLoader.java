import java.io.*;
import java.util.Calendar;
import java.util.Vector;

public class ScheduleLoader
{
	public BusStop[] Stops;
	
	void Debug(String x)
	{
		Stops = new BusStop[] { new BusStop() };
		
		Stops[0].Name = x;
		Stops[0].Schedules = new BusSchedule[] {};
	}

	public void Load()
	{
		try{
			DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("scheds"));

			try
			{
				while(true)
				{
					String bus = dis.readUTF();
					String busStop = dis.readUTF();
					String firstBusStop = dis.readUTF();
					String lastBusStop = dis.readUTF();
					short days = dis.readShort();
					short direction = dis.readShort();
					
					if(direction == -1)
						busStop = busStop + "_B";

					if(direction == 1)
						busStop = busStop + "_F";

					short[] times = new short[dis.readShort()];
					for (int i = 0; i < times.length; i++)
					{
						times[i] = dis.readShort();
					}
					
					AddSchedule(bus, busStop, days, times);
				}
			}
			catch(EOFException eof)
			{
			}

			Stops = new BusStop[stopsCache.size()];
			for (int i = 0; i < stopsCache.size(); i++)
			{
				Stops[i] = (BusStop)stopsCache.elementAt(i);
			}

			// analyze times
			for (int s = 0; s < Stops.length; s++)
			{
				BusStop stop = Stops[s];
				for (int i = 0; i < stop.Schedules.length; i++)
				{
					BusSchedule sched = stop.Schedules[i];

					int[] workdayIndexes = new int[] { 
							BusSchedule.WORKDAY,
							Calendar.MONDAY,
							Calendar.TUESDAY,
							Calendar.WEDNESDAY,
							Calendar.THURSDAY,
							Calendar.FRIDAY};

					int[] holidayIndexes = new int[] { 
							BusSchedule.HOLIDAY,
							Calendar.SUNDAY,
							Calendar.SATURDAY};
					
					// guess workday times
					short[] workTimes = new short[] {};
					for (int j = 0; j < workdayIndexes.length; j++)
					{
						if(sched.Times[workdayIndexes[j]] != null)
						{
							workTimes = sched.Times[workdayIndexes[j]];
							break;
						}
					}
					// guess holiday times
					short[] holidayTimes = new short[0];
					for (int j = 0; j < holidayIndexes.length; j++)
					{
						if(sched.Times[holidayIndexes[j]] != null)
						{
							holidayTimes = sched.Times[holidayIndexes[j]];
							break;
						}
					}
					
					// fill empty workdays
					for (int j = 0; j < workdayIndexes.length; j++)
					{
						if(sched.Times[workdayIndexes[j]] == null)
							sched.Times[workdayIndexes[j]] = workTimes;
					}

					// fill empty holidays
					for (int j = 0; j < holidayIndexes.length; j++)
					{
						if(sched.Times[holidayIndexes[j]] == null)
							sched.Times[holidayIndexes[j]] = holidayTimes;
					}
				}
			}
		}
		catch(Exception ex)
		{
			Debug(ex.getMessage());
		}
	}
	
	Vector busesCache = new Vector();
	Vector stopsCache = new Vector();

	BusInfo FindBusInfo(String name)
	{
		for (int i = 0; i < busesCache.size(); i++)
		{
			BusInfo bi = (BusInfo)busesCache.elementAt(i);
			if(bi.Name.compareTo(name) == 0)
				return bi;
		}
		BusInfo bi = new BusInfo();
		bi.Name = name;
		busesCache.addElement(bi);
		return bi;
	}
	
	BusStop FindBusStop(String name)
	{
		for (int i = 0; i < stopsCache.size(); i++)
		{
			BusStop stop = (BusStop)stopsCache.elementAt(i);
			if(stop.Name.compareTo(name) == 0)
				return stop;
		}
		BusStop stop = new BusStop();
		stop.Name = name;
		stopsCache.addElement(stop);
		return stop;
	}
	
	BusSchedule FindSchedule(BusStop stop, BusInfo bus)
	{
		if(stop.Schedules == null)
		{
			BusSchedule sched = new BusSchedule();
			sched.Bus = bus;
			stop.Schedules = new BusSchedule[] {sched};
			return sched;
		}
		for (int i = 0; i < stop.Schedules.length; i++)
		{
			BusSchedule sched = stop.Schedules[i];
			if(sched.Bus.Name.compareTo(bus.Name) == 0)
				return sched;
		}
		BusSchedule sched = new BusSchedule();
		sched.Bus = bus;
		BusSchedule[] oldScheds = stop.Schedules; 
		stop.Schedules = new BusSchedule[oldScheds.length + 1];
		for (int i = 0; i < oldScheds.length; i++)
			stop.Schedules[i] = oldScheds[i];			
		stop.Schedules[oldScheds.length] = sched;			
		return sched;
	}

	void AddSchedule(String bus, String busStop, short days, short[] times)
	{
		BusInfo bi = FindBusInfo(bus);
		BusStop stop = FindBusStop(busStop);
		BusSchedule sched = FindSchedule(stop, bi);
		for (int i = 0; i < 9; i++)
		{
			int bit = (1 << i);
			if((days & bit) == bit)
				sched.Times[i] = times;
		}
	}
}
