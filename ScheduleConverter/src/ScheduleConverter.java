import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class ScheduleConverter
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		new ScheduleConverter().Convert(args);
	}
	
	Vector<Bus> buses = null;
	Vector<BusStop> busStops = null;
	Vector<Schedule> schedules = new Vector<Schedule>();
	Vector<DerivedSchedule> derSchedules = new Vector<DerivedSchedule>();

	void Convert(String[] args)
	{
		try{
			if(args.length == 0)
			{
				ShowHelp();
				return;
			}

			String outDir = null;

			Charset c = Charset.forName("UTF-8");
			
			for (int i = 0; i < args.length; i++)
			{
				String arg = args[i];
				
				if(arg.compareTo("-c") == 0)
				{
					i++;
					if(i == args.length)
						throw new Exception("Argument -c not supplied with parameter.");
					c = Charset.forName(args[i]);
					continue;
				}

				if(arg.compareTo("-o") == 0)
				{
					i++;
					if(i == args.length)
						throw new Exception("Argument -o not supplied with parameter.");
					outDir = args[i];
					continue;
				}

				if(buses == null)
				{
					buses = new Vector<Bus>();
					CsvReader r = new CsvReader(arg, ';', c);
					r.readHeaders();
					while(r.readRecord())
					{
						Bus b = new Bus();
						b.id = Integer.parseInt(r.get("id"));

						if(b.id > Byte.MAX_VALUE)
							throw new Exception("Bus ID exceed store size");

						b.name = r.get("name");
						b.route = r.get("route");
						
						buses.add(b);
					}
					System.out.println("Buses: " + arg);
					continue;
				}

				if(busStops == null)
				{
					busStops = new Vector<BusStop>();
					CsvReader r = new CsvReader(arg, ';', c);
					r.readHeaders();
					while(r.readRecord())
					{
						BusStop bs = new BusStop();
						bs.id = Integer.parseInt(r.get("id"));
						if(bs.id > Byte.MAX_VALUE)
							throw new Exception("BusStop ID exceed store size");
						bs.name = r.get("name");
						bs.officialName = r.get("officialName");
						bs.description = r.get("description");
						
						busStops.add(bs);
					}
					System.out.println("BusStops: " + arg);
					continue;
				}
				
				try
				{
					ParseScheduleFile(arg, c);
					System.out.println("Schedule: " + arg);
				}
				catch(Exception ex)
				{
					throw new Exception("File " + arg + "\n" + ex.toString(), ex);
				}
			}
			
			if(buses == null)
				throw new Exception("Buses CSV not provided.");

			if(busStops == null)
				throw new Exception("Bus stops CSV not provided.");

			// remove unused busstops && check duplicates
			for (int i = 0; i < busStops.size(); i++)
			{
				BusStop bs = busStops.get(i);
				int idsCount = 0;
				int namesCount = 0;
				for (int j = 0; j < busStops.size(); j++)
				{
					BusStop bsO = busStops.get(j);
					if(bs.id == bsO.id)
						idsCount++;
					if(bs.name.compareTo(bsO.name) == 0)
						namesCount++;
				}

				if(idsCount > 1)
					throw new Exception("Bus Stop ID:" + bs.id + ", Name:" + bs.name + " duplicated by ID");

				if(namesCount > 1)
					throw new Exception("Bus Stop ID:" + bs.id + ", Name:" + bs.name + " duplicated by Name");

				boolean used = false;
				for (int j = 0; j < schedules.size(); j++)
				{
					if(schedules.get(j).busStop == bs.id)
					{
						used = true;
						break;
					}
				}
				
				if(used == false)
				{
					for (int j = 0; j < derSchedules.size(); j++)
					{
						if(derSchedules.get(j).busStop == bs.id || derSchedules.get(j).baseBusStop == bs.id)
						{
							used = true;
							break;
						}
					}
				}

				if(!used)
				{
					System.out.println("Remove unused: " + busStops.get(i).name);
					busStops.remove(i);
					i--;
				}
			}
			
			// remove unused buses && check duplicates
			for (int i = 0; i < buses.size(); i++)
			{
				Bus b = buses.get(i);
				int idsCount = 0;
				int namesCount = 0;
				for (int j = 0; j < buses.size(); j++)
				{
					Bus bO = buses.get(j);
					if(b.id == bO.id)
						idsCount++;
					if(b.name.compareTo(bO.name) == 0)
						namesCount++;
				}
				
				if(idsCount > 1)
					throw new Exception("Bus Stop ID:" + b.id + ", Name:" + b.name + " duplicated by ID");

				if(namesCount > 1)
					throw new Exception("Bus Stop ID:" + b.id + ", Name:" + b.name + " duplicated by Name");

				boolean used = false;
				for (int j = 0; j < schedules.size(); j++)
				{
					if(schedules.get(j).bus == b.id)
					{
						used = true;
						break;
					}
				}
				if(!used)
				{
					buses.remove(i);
					i--;
				}
			}
			
			// fix 'overnight' times: 23.20, 23.55, 0.10 now are 1400, 1435, 10 
			// but should be 1400, 1435, 1450 (0.10 is are same day, but line over 24 hours... 24.10)
			for (int schedIndex = 0; schedIndex < schedules.size(); schedIndex++)
			{
				Schedule sched = schedules.get(schedIndex);
				boolean alreadyFixed = false;
				for (int i = 1; i < sched.times.size(); i++)
				{
					// check if need change fixer
					int curTime = sched.times.get(i);
					int prevTime = sched.times.get(i - 1);
					if(curTime < prevTime)
					{
						if(alreadyFixed)
							throw new Exception("Times already fixed!");
						alreadyFixed = true;
						// fix all next times:
						for (int j = i; j < sched.times.size(); j++)
						{
							sched.times.set(j, sched.times.get(j) + 24*60);
						}
					}
				}
			}
			
			//sort schedules by busStops then by bus
			Collections.sort(schedules);
			
			System.out.println("DATA VALID.");

			if(outDir != null)
			{
				// write 
				WriteBuses(outDir + "/buses");
				WriteBusStops(outDir + "/busStops");
				WriteSchedules(outDir + "/scheds");
				WriteDerivedSchedules(outDir + "/dscheds");
				System.out.println("DATA STORED.");
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception: " + ex.getMessage());
		}
	}
	
	void ShowHelp()
	{
		System.out.println("Usage: <executable> <busstops.csv> <buses.csv> sched_file1 sched_file2 ...");
	}
	
	Vector<String> ReadAllStrings(String file, Charset c) throws IOException
	{
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), c));
		
		Vector<String> ret = new Vector<String>();
		while(true)
		{
			String line = br.readLine();
			if(line == null)
				break;
			
			ret.add(line);
		}
		return ret;
	}

	void ParseScheduleFile(String file, Charset c) throws Exception
	{
		int bus = -1;
		int busStop = -1;
		int day = -1;	// work days by default

		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(file), c));
		String line;
		while((line = lnr.readLine()) != null)
		{
			try{
				line = line.replaceAll("\\s+", " ").trim();
				if(line.length() == 0)
					continue;

				if(line.startsWith("#"))
					continue;
				
				if(line.startsWith("\\busstop"))
				{
					// reset day
					day = -1;
					
					String busStopName = line.replaceAll("^\\\\\\S+\\s+", "").trim();
					BusStop bs = FindBusStop(busStopName);
					busStop = bs.id;
					continue;
				}
				
				if(line.startsWith("\\bus"))
				{
					// reset busStop & day
					busStop = -1;
					day = -1;
					
					String busName = line.replaceAll("^\\\\\\S+\\s+", "").trim();
					Bus b = FindBus(busName);
					bus = b.id;
					continue;
				}
				
				if(line.startsWith("\\day"))
				{
					day = ParseDay(line.replaceAll("^\\\\\\S+\\s+", "").trim());
					continue;
				}
				
				if(bus == -1)
					throw new Exception("No bus specified");

				if(line.startsWith("\\derive"))
				{
					String s = line.replaceAll("^\\\\\\S+\\s+", "").trim();
					String vals[] = s.split(";");
					
					// dst_busstop;src_busstop;timeshift;days
					
					busStop = FindBusStop(vals[0]).id;
					BusStop srcBusStop = FindBusStop(vals[1]);
					int shift = Integer.parseInt(vals[2]);
					String[] days = vals[3].split(" ");

					if(shift > Byte.MAX_VALUE)
						throw new Exception("shift exceed store size");

					for (int d = 0; d < days.length; d++)
					{
						day = ParseDay(days[d]);

						DerivedSchedule ds = new DerivedSchedule();
						ds.bus = bus;
						ds.busStop = busStop;
						ds.baseBusStop = srcBusStop.id;
						ds.shift = shift;
						ds.day = day;
						derSchedules.add(ds);
					}

					// reset busstop and day
					busStop = -1;
					day = -1;
					
					continue;
				}

				if(busStop == -1)
					throw new Exception("No bus stop specified");

				if(line.startsWith("\\from"))
				{
					Schedule sched = FindSchedule(schedules, bus, busStop, day);
					String from = line.replaceAll("^\\\\\\S+\\s+", "").trim();
					if(from.equalsIgnoreCase("site"))
					{
						sched.schedFrom = Schedule.SCHED_FROM_MINSK_TRANS_SITE;
					}
					else if(from.equalsIgnoreCase("busstop"))
					{
						sched.schedFrom = Schedule.SCHED_FROM_BUSSTOP;
					}
					else
					{
						throw new Exception("Unknonwn 'from' value:" + from);
					}
					continue;
				}

				if(day == -1)
					throw new Exception("No day specified");

				if(line.startsWith("\\copy"))
				{
					String busStopName = line.replaceAll("^\\\\\\S+\\s+", "").trim();
					BusStop bsCopy = FindBusStop(busStopName);
					Schedule src = FindSchedule(schedules, bus, bsCopy.id, day);
					Schedule dst = FindSchedule(schedules, bus, busStop, day);
					for (int i = 0; i < src.times.size(); i++)
					{
						dst.times.add(src.times.get(i));
					}
					continue;
				}

				if(line.startsWith("\\shift"))
				{
					short shift = Short.parseShort(line.replaceAll("^\\\\\\S+\\s+", "").trim()); 
					Schedule sched = FindSchedule(schedules, bus, busStop, day);
					for (int i = 0; i < sched.times.size(); i++)
					{
						int newTime = sched.times.get(i) + shift;
						if(newTime < 0)
							throw new Exception("Neative time when shifting");
						sched.times.set(i, newTime);
					}
					continue;
				}
				
				Schedule sched = FindSchedule(schedules, bus, busStop, day);
				ParseTimeLine(sched, line);
			}
			catch(Exception ex)
			{
				int lineNo = lnr.getLineNumber();
				throw new Exception("Error in " + file + ":" + lineNo + "\n" + ex.getMessage(), ex);
			}
		}
	}
	
	Bus FindBus(String busName) throws Exception
	{
		for (int i = 0; i < buses.size(); i++)
		{
			if(busName.compareTo(buses.get(i).name) == 0)
				return buses.get(i);
		}
		throw new Exception("Can't find bus '" + busName + "'");
	}
	
	BusStop FindBusStop(String busStopName) throws Exception
	{
		for (int i = 0; i < busStops.size(); i++)
		{
			if(busStopName.compareTo(busStops.get(i).name) == 0)
				return busStops.get(i);
		}
		throw new Exception("Can't find bus stop '" + busStopName + "'");
	}
	
	BusStop FindBusStop(int busStopId) throws Exception
	{
		for (int i = 0; i < busStops.size(); i++)
		{
			if(busStopId == busStops.get(i).id)
				return busStops.get(i);
		}
		throw new Exception("Can't find bus stop #" + busStopId);
	}
	
	Schedule FindSchedule(Vector<Schedule> schedules, int bus, int busStop, int day)
	{
		for (int i = 0; i < schedules.size(); i++)
		{
			Schedule sched = schedules.elementAt(i); 
			if(bus == sched.bus && busStop == sched.busStop && sched.day == day)
				return sched;
		}
		Schedule sched = new Schedule();
		sched.bus = bus;
		sched.busStop = busStop;
		sched.day = day;
		schedules.add(sched);
		return sched;
	}
	
	Short ParseDay(String s) throws Exception
	{
		if(s.compareTo("w") == 0 || s.compareTo("W") == 0)
			return Schedule.WORKDAY;
		else if(s.compareTo("h") == 0 || s.compareTo("H") == 0)
			return Schedule.HOLIDAY;

		return Short.parseShort(s);
	}
	
	Short ParseDirection(String s) throws Exception
	{
		if(s.compareTo("f") == 0 || s.compareTo("F") == 0)
			return 1;
		if(s.compareTo("b") == 0 || s.compareTo("B") == 0)
			return -1;
		if(s.compareTo("na") == 0 || s.compareTo("NA") == 0 || s.compareTo("") == 0)
			return 0;
		throw new Exception("Incorrect direction: " + s);
	}
	
	void ParseTimeLine(Schedule sched, String s) throws Exception
	{
		String[] strTimes = s
			.replaceAll("\\s+|,|;|/", " ")
			.replaceAll("\\.", ":")
			.split(" ");
		
		int hour = 0;
		
		if(strTimes[0].contains(":") == false)
		{
			// if first number in line not has '.' or ':', this is a hour only, not a bus time
			hour = Integer.parseInt(strTimes[0]);
			strTimes = Arrays.copyOfRange(strTimes, 1, strTimes.length);
		}

		for (int i = 0; i < strTimes.length; i++)
		{
			String time = strTimes[i];
			
			int minute = 0;
			if(time.contains(":"))
			{
				String[] timePair = time.split(":");
				if(timePair.length != 2)
					throw new Exception("Invalid time: " + time);
				hour = Integer.parseInt(timePair[0]);
				minute = Integer.parseInt(timePair[1]);
			}
			else
			{
				minute = Integer.parseInt(time);
			}
			if(hour > 23 || hour < 0 || minute > 59 || minute < 0)
				throw new Exception("Invalid time: " + time);
			
			sched.times.add(hour*60 + minute);
		}
	}

	void WriteBuses(String file) throws Exception
	{
		// format:
		// bus count: short
		//
		// bus id: short
		// name: UTF-8
		// route: UTF-8
		// ...
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, false));
		if(buses.size() > Byte.MAX_VALUE)
			throw new Exception("buses.size() exceed store size");
		dos.writeByte(buses.size());
		for (int i = 0; i < buses.size(); i++)
		{
			Bus bus = buses.get(i);
			dos.writeByte(bus.id);
			dos.writeUTF(bus.name);
			dos.writeUTF(bus.route);
		}
		dos.flush();
		dos.close();
	}

	void WriteBusStops(String file) throws Exception
	{
		// format:
		// busStop count: short
		//
		// id: byte
		// name: UTF-8
		// description: UTF-8
		// ...
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, false));
		if(busStops.size() > Byte.MAX_VALUE)
			throw new Exception("busStops.size() exceed store size");
		dos.writeByte(busStops.size());
		for (int i = 0; i < busStops.size(); i++)
		{
			BusStop busStop = busStops.get(i);
			dos.writeByte(busStop.id);
			dos.writeUTF(busStop.name);
			dos.writeUTF(busStop.description);
		}
		dos.flush();
		dos.close();
	}
	
	String DayToString(short day)
	{
		String[] daysStr = new String[] { "", "Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Раб", "Вых" };
		return daysStr[day];
	}

	void WriteSchedules(String file) throws Exception
	{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, false));
		
		if(schedules.size() > Byte.MAX_VALUE)
			throw new Exception("schedules.size() exceed store size");
		
		dos.writeByte(schedules.size());

		for (int i = 0; i < schedules.size(); i++)
		{
			Schedule sched = schedules.get(i);

			dos.writeByte(sched.bus);
			dos.writeByte(sched.busStop);
			dos.writeByte(sched.day);
			dos.writeByte(sched.schedFrom);
			
			//System.out.println(sched.busStop + "/" + sched.bus + "/" + DayToString(sched.day));
			
			if(sched.times.size() > Byte.MAX_VALUE)
				throw new Exception("Value exceed store size");

			dos.writeByte(sched.times.size());
			
			for (int j = 0; j < sched.times.size(); j++)
			{
				dos.writeShort(sched.times.get(j));
			}
		}

		dos.flush();
		dos.close();
	}

	void WriteDerivedSchedules(String file) throws Exception
	{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, false));
		
		if(derSchedules.size() > Byte.MAX_VALUE)
			throw new Exception("derSchedules.size() exceed store size");
		
		dos.writeByte(derSchedules.size());

		for (int i = 0; i < derSchedules.size(); i++)
		{
			DerivedSchedule sched = derSchedules.get(i);

			dos.writeByte(sched.bus);
			dos.writeByte(sched.busStop);
			dos.writeByte(sched.day);
			dos.writeByte(sched.baseBusStop);
			dos.writeByte(sched.shift);
		}

		dos.flush();
		dos.close();
	}
}


