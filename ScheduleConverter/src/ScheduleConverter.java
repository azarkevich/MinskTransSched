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

	void Convert(String[] args)
	{
		try{
			if(args.length == 0)
			{
				ShowHelp();
				return;
			}

			String outDir = ".";

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
						b.id = Short.parseShort(r.get("id"));
						b.name = r.get("name");
						b.description = r.get("description");
						
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
						bs.id = Short.parseShort(r.get("id"));
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

			// write 
			WriteBuses(outDir + "/buses");
			WriteBusStops(outDir + "/busStops");
			WriteSchedules(outDir + "/scheds");
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
		short bus = -1;
		short busStop = -1;
		short days = 0x100;	// work days by default

		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(file), c));
		String line;
		while((line = lnr.readLine()) != null)
		{
			try{
				line = line.replaceAll("\\s+", " ").trim();
				if(line.length() == 0)
					continue;
				
				if(line.startsWith("\\busstop"))
				{
					String busStopName = line.replaceAll("^\\\\\\S+\\s+", "").trim();
					BusStop bs = FindBusStop(busStopName);
					busStop = bs.id;
					continue;
				}
				if(line.startsWith("\\bus"))
				{
					String busName = line.replaceAll("^\\\\\\S+\\s+", "").trim();
					Bus b = FindBus(busName);
					bus = b.id;
					continue;
				}
				if(line.startsWith("\\days"))
				{
					days = ParseDays(line.replaceAll("^\\\\\\S+\\s+", "").trim());
					continue;
				}

				if(bus == -1)
					throw new Exception("No bus specified");

				if(busStop == -1)
					throw new Exception("No bus stop specified");
				
				Schedule sched = FindSchedule(schedules, bus, busStop, days);
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
	
	Schedule FindSchedule(Vector<Schedule> schedules, short bus, short busStop, short days)
	{
		for (int i = 0; i < schedules.size(); i++)
		{
			Schedule sched = schedules.elementAt(i); 
			if(bus == sched.bus && busStop == sched.busStop && sched.days == days)
				return sched;
		}
		Schedule sched = new Schedule();
		sched.bus = bus;
		sched.busStop = busStop;
		sched.days = days;
		schedules.add(sched);
		return sched;
	}
	
	Short ParseDays(String s) throws Exception
	{
		String[] daysArray = s
			.replaceAll("\\s*-\\s*", "-")
			.replaceAll("\\s+|,|;|/", " ")
			.replaceAll("\\s+", " ")
			.split(" ");
		
		Short days = 0;
		for (int i = 0; i < daysArray.length; i++)
		{
			String day = daysArray[i];
			// day range?
			if(day.contains("-"))
			{
				String dayRange[] = day.split("-");
				if(dayRange.length != 2)
					throw new Exception("Incorrect day range: " + day);
				
				for (int d = Integer.parseInt(dayRange[0]); d <= Integer.parseInt(dayRange[1]); d++)
				{
					days |= (1 << (d - 1));
				}
			}
			else
			{
				if(day.compareTo("w") == 0 || day.compareTo("W") == 0)
					days |= (1 << 8);
				else if(day.compareTo("h") == 0 || day.compareTo("H") == 0)
					days |= (1 << 9);
				else
					days |= (1 << (Integer.parseInt(day) - 1));
			}
		}
		
		return days;
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
			
			sched.times.add((short)(hour*60 + minute));
		}
	}

	void WriteBuses(String file) throws IOException
	{
		// format:
		// bus count: short
		//
		// bus id: short
		// name: UTF-8
		// description: UTF-8
		// ...
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, false));
		dos.writeShort((short)buses.size());
		for (int i = 0; i < buses.size(); i++)
		{
			Bus bus = buses.get(i);
			dos.writeShort(bus.id);
			dos.writeUTF(bus.name);
			dos.writeUTF(bus.description);
		}
		dos.flush();
		dos.close();
	}

	void WriteBusStops(String file) throws IOException
	{
		// format:
		// busStop count: short
		//
		// id: short
		// name: UTF-8
		// official name: UTF-8
		// description: UTF-8
		// ...
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, false));
		dos.writeShort((short)busStops.size());
		for (int i = 0; i < busStops.size(); i++)
		{
			BusStop busStop = busStops.get(i);
			dos.writeShort(busStop.id);
			dos.writeUTF(busStop.name);
			dos.writeUTF(busStop.officialName);
			dos.writeUTF(busStop.description);
		}
		dos.flush();
		dos.close();
	}

	void WriteSchedules(String file) throws IOException
	{
		// format:
		// schedules count: short
		//
		// bus id: short
		// busStop id: short
		// days: short. (bit flags: bit 0 - not used, bit  1 - Sunday ... bit 7 - Saturday, bit 8 - workday, bit 9 - holiday)
		// times count: short
		// times: short[]
		// ...
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, false));
		dos.writeShort((short)schedules.size());

		for (int i = 0; i < schedules.size(); i++)
		{
			Schedule sched = schedules.get(i);

			dos.writeShort(sched.bus);
			dos.writeShort(sched.busStop);
			dos.writeShort(sched.days);
			
			dos.writeShort((short)sched.times.size());
			
			for (int j = 0; j < sched.times.size(); j++)
			{
				dos.writeShort(sched.times.get(j));
			}
		}

		dos.flush();
		dos.close();
	}
}


