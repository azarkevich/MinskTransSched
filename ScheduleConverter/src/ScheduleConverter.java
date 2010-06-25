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
		int status = new ScheduleConverter().Convert(args);
		System.exit(status);
	}
	
	Vector<Bus> buses = null;
	Vector<BusStop> stops = null;
	Vector<Schedule> schedules = new Vector<Schedule>();
	Vector<DerivedSchedule> derSchedules = new Vector<DerivedSchedule>();
	Vector<FilterDef> filters = new Vector<FilterDef>();
	Integer dayEnd;

	int Convert(String[] args)
	{
		try{
			if(args.length == 0)
			{
				ShowHelp();
				return -1;
			}

			String plainText = null; 
			String outDir = null;

			for (int i = 0; i < args.length; i++)
			{
				String arg = args[i];
				
				if(arg.compareTo("-c") == 0)
				{
					i++;
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
				
				if(arg.compareTo("-p") == 0)
				{
					i++;
					if(i == args.length)
						throw new Exception("Argument -o not supplied with parameter.");
					plainText = args[i];
					continue;
				}
			}
			
			// load stops
			LoadStops(args);
			
			// load transport
			LoadTransport(args);
			
			// load fiulters
			LoadFilters(args);
			
			// load schedultes
			LoadSchedules(args);
			
			if(buses == null)
				throw new Exception("Buses CSV not provided.");

			if(stops == null)
				throw new Exception("Stops CSV not provided.");
			
			// check stops duplicates
			for (int i = 0; i < stops.size(); i++)
			{
				BusStop bs = stops.get(i);
				int idsCount = 0;
				int namesCount = 0;
				for (int j = 0; j < stops.size(); j++)
				{
					BusStop bsO = stops.get(j);
					if(bs.id == bsO.id)
						idsCount++;
					if(bs.name.compareTo(bsO.name) == 0)
						namesCount++;
				}

				if(idsCount > 1)
					throw new Exception("Stop ID:" + bs.id + ", Name:" + bs.name + " duplicated by ID");

				if(namesCount > 1)
					throw new Exception("Stop ID:" + bs.id + ", Name:" + bs.name + " duplicated by Name");
			}
			
			// check buses duplicates
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
			Collections.sort(buses);
			Collections.sort(stops);
			
			// find min and max time
			int minTime = Integer.MAX_VALUE;
			int maxTime = Integer.MIN_VALUE;
			for (int i = 0; i < schedules.size(); i++)
			{
				Schedule sched = schedules.get(i);
				int curMinTime = sched.times.get(0);
				int curMaxTime = sched.times.get(sched.times.size() - 1);
				if(maxTime < curMaxTime)
					maxTime = curMaxTime;
				if(minTime > curMinTime)
					minTime = curMinTime;
			}
			
			if(maxTime == Integer.MIN_VALUE || minTime == Integer.MAX_VALUE)
			{
				throw new Exception("Can't find min/max time");
			}
			
			// day border. at day border begin new day. 
			int daylen = 24 * 60;
			dayEnd = maxTime;
			if(dayEnd > daylen)
			{
				dayEnd = (maxTime + 1) % daylen;
				
				if(dayEnd > minTime)
				{
					throw new Exception("Mim/Max times overlapped. Can't guess day end");
				}
			}
			else
			{
				dayEnd = 1;
			}
			
			System.out.println("Day end: " + (dayEnd / 60) + "." + (dayEnd % 60));

			System.out.println("DATA VALID.");

			if(outDir != null)
			{
				// write 
				WriteBuses(outDir + "/buses");
				WriteBusStops(outDir + "/busStops");
				WriteSchedules(outDir + "/scheds");
				WriteDerivedSchedules(outDir + "/dscheds");
				WriteFilters(outDir + "/filters");
				WriteSettings(outDir + "/settings");
				System.out.println("DATA STORED.");
			}
			
			// generate DB like text
			if(plainText != null)
			{
				PrintWriter pw = new PrintWriter(plainText, "CP1251");
				pw.println("День: 0 - все дни, 8 - рабочие дни, 9 - выходные дни. 1, 2-7 вс, пн-сб");
				pw.println("Время: минуты от начала суток.");
				pw.println(
						"Остановка(id)"
						+ ";Остановка(название)"
						+ ";Остановка(описание)"
						+ ";Автобус(id)"
						+ ";Автобус(название)"
						+ ";День"
						+ ";Время"
						+ ";Время(HH:MM)"
						+ ";Базовая остановка(id)"
						+ ";Базовая остановка(название)"
						+ ";Сдвиг"
					);

				for (int schedIndex = 0; schedIndex < schedules.size(); schedIndex++)
				{
					Schedule sched = schedules.get(schedIndex);
					
					BusStop stop = FindBusStop(sched.busStop);
					Bus bus = FindBus(sched.bus);
					
					for (int i = 0; i < sched.times.size(); i++)
					{
						int time = sched.times.get(i);
						pw.println(
								stop.id
								+ ";" + stop.name
								+ ";" + stop.description
								+ ";" + bus.id
								+ ";" + bus.name
								+ ";" + sched.day
								+ ";" + time
								+ ";" + (time / 60)%24 + ":" + time % 60
								+ ";"
								+ ";"
								+ ";"
							);
					}
				}
				for (int i = 0; i < derSchedules.size(); i++)
				{
					DerivedSchedule sched = derSchedules.get(i);
					BusStop stop = FindBusStop(sched.busStop);
					BusStop baseStop = FindBusStop(sched.baseBusStop);
					Bus bus = FindBus(sched.bus);
					int shift = sched.shift;

					Schedule baseSched = FindSchedule(schedules, bus.id, sched.baseBusStop, sched.dayFrom);
					for (int j = 0; j < baseSched.times.size(); j++)
					{
						int time = baseSched.times.get(j) + shift;
						pw.println(
								stop.id
								+ ";" + stop.name
								+ ";" + stop.description
								+ ";" + bus.id
								+ ";" + bus.name
								+ ";" + sched.dayTo
								+ ";" + time
								+ ";" + (time / 60)%24 + ":" + time % 60
								+ ";" + baseStop.id
								+ ";" + baseStop.name
								+ ";" + shift
							);
					}
				}
				generateSQL();
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception: " + ex.getMessage());
			return -2;
		}
		return 0;
	}

	private void generateSQL() throws FileNotFoundException,
			UnsupportedEncodingException, Exception
	{
		// create SQL
		PrintWriter t = new PrintWriter("data.sql", "UTF-8");
		t.println("DELETE FROM [times]");
		t.println("DELETE FROM [schedules]");
		t.println("DELETE FROM [base_schedules]");
		t.println("DELETE FROM [transport]");
		t.println("DELETE FROM [stops]");
		t.println("");

		t.println("-- stops --");

		// create stops
		for (int i = 0; i < stops.size(); i++)
		{
			BusStop bs = stops.get(i);
			
			t.println("INSERT INTO [stops]([id], [name], [note]) VALUES (" 
					+ bs.id + ", "
					+ "N'" + bs.name.replaceAll("'", "''") + "', "
					+ "N'" + bs.description.replaceAll("'", "''") + "'"
					+ ")");
		}
		t.println("");
		
		t.println("-- transport --");
		for (int i = 0; i < buses.size(); i++)
		{
			Bus b = buses.get(i);
			
			BusStop start_stop = FindBusStop(b.startRoute);
			BusStop end_stop = FindBusStop(b.endRoute);

			t.println("INSERT INTO [transport]([id],[transport_type],[name],[first_stop_id],[last_stop_id]) VALUES(" 
					+ b.id + ", "
					+ "1, "
					+ "N'" + b.name.replaceAll("'", "''") + "', "
					+ start_stop.id + ", "
					+ end_stop.id
					+ ")");
		}
		t.println("");

		t.println("-- base_schedules --");
		for (int schedIndex = 0; schedIndex < schedules.size(); schedIndex++)
		{
			Schedule sched = schedules.get(schedIndex);
			
			Bus bus = FindBus(sched.bus);
			
			sched.base_sched_id = 10*schedIndex + sched.day;
			
			t.println("INSERT INTO [base_schedules]([id], [transport_id]) VALUES("+ sched.base_sched_id + "," + bus.id + ")");

			for (int i = 0; i < sched.times.size(); i++)
			{
				int time = sched.times.get(i);
				
				t.println("INSERT INTO [times]([base_schedule_id], [time]) VALUES("+ sched.base_sched_id + "," + time + ")");
			}
		}
		t.println("");

		t.println("-- schedules --");
		for (int schedIndex = 0; schedIndex < schedules.size(); schedIndex++)
		{
			Schedule sched = schedules.get(schedIndex);

			BusStop stop = FindBusStop(sched.busStop);
			Bus bus = FindBus(sched.bus);
			
			t.println("INSERT INTO [schedules]([day],[stop_id],[transport_id],[base_schedule_id],[time_shift],[note]) VALUES(" 
					+ sched.day + ","
					+ stop.id + ","
					+ bus.id + ","
					+ sched.base_sched_id + ","
					+ "0,"
					+ "'from site'"
					+ ")");
		}

		t.println("-- derived schedules --");
		for (int i = 0; i < derSchedules.size(); i++)
		{
			DerivedSchedule sched = derSchedules.get(i);
			BusStop stop = FindBusStop(sched.busStop);
			Bus bus = FindBus(sched.bus);
			int shift = sched.shift;

			Schedule baseSched = FindSchedule(schedules, bus.id, sched.baseBusStop, sched.dayFrom);
			
			t.println("INSERT INTO [schedules]([day],[stop_id],[transport_id],[base_schedule_id],[time_shift],[note]) VALUES(" 
					+ sched.dayTo + ","
					+ stop.id + ","
					+ bus.id + ","
					+ baseSched.base_sched_id + ","
					+ "+" + shift + ","
					+ "'shift'"
					+ ")");
		}
		t.close();
	}

	private void LoadSchedules(String[] args) throws Exception
	{
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

			// load filters
			if(arg.endsWith(".txt"))
			{
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
		}
	}

	private void LoadFilters(String[] args) throws Exception
	{
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
			
			// load filters
			if(arg.endsWith(".flt"))
			{
				ParseFiltersFile(arg, c);
				System.out.println("Filters: " + arg);
			}
		}
	}

	private void LoadTransport(String[] args) throws Exception,
			FileNotFoundException, IOException
	{
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
			
			if(arg.endsWith(".tr"))
			{
				buses = new Vector<Bus>();
				CsvReader r = new CsvReader(arg, ';', c);
				r.readHeaders();
				while(r.readRecord())
				{
					Bus b = new Bus();
					b.name = r.get("name");
					String start = r.get("start");
					if(start != null && start.compareTo("") != 0)
					{
						BusStop startBS = FindBusStop(start);
						if(startBS == null)
							throw new Exception("Undefined start route: " + start);
						b.startRoute = startBS.id;
					}
					String end = r.get("end");
					if(end != null && end.compareTo("") != 0)
					{
						BusStop endBS = FindBusStop(end);
						if(endBS == null)
							throw new Exception("Undefined end route: " + end);
						
						b.endRoute = endBS.id;
					}
					buses.add(b);
				}
				System.out.println("Buses: " + arg);
				continue;
			}
		}
	}

	private void LoadStops(String[] args) throws Exception,
			FileNotFoundException, IOException
	{
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
			
			if(arg.endsWith(".stop"))
			{
				stops = new Vector<BusStop>();
				CsvReader r = new CsvReader(arg, ';', c);
				r.readHeaders();
				while(r.readRecord())
				{
					BusStop bs = new BusStop();
					bs.name = r.get("name");
					bs.officialName = r.get("officialName");
					bs.description = r.get("description");
					String region = r.get("region");
					if(region != null && region.isEmpty() == false)
					{
						FilterDef fd = FindFilter(region);
						fd.stops.add(bs);
					}
					stops.add(bs);
				}
				System.out.println("BusStops: " + arg);
				continue;
			}
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
					String[] daysFrom = vals[3].split(" ");
					String[] daysTo = daysFrom;
					if(vals.length > 4)
						daysTo = vals[4].split(" ");

					if(shift > Byte.MAX_VALUE)
						throw new Exception("shift exceed store size");

					for (int d = 0; d < daysFrom.length; d++)
					{
						int dayFrom = ParseDay(daysFrom[d]);
						int dayTo = ParseDay(daysTo[d]);

						DerivedSchedule ds = new DerivedSchedule();
						ds.bus = bus;
						ds.busStop = busStop;
						ds.baseBusStop = srcBusStop.id;
						ds.shift = shift;
						ds.dayFrom = dayFrom;
						ds.dayTo = dayTo;
						derSchedules.add(ds);
					}

					// reset busstop and day
					busStop = -1;
					day = -1;
					
					continue;
				}

				if(busStop == -1)
					throw new Exception("No bus stop specified");

				if(day == -1)
					throw new Exception("No day specified");

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
	
	void ParseFiltersFile(String file, Charset c) throws Exception
	{
		FilterDef f = null;

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
				
				if(line.startsWith("\\filter"))
				{
					String name = line.replaceAll("^\\\\\\S+\\s+", "").trim();
					f = FindFilter(name);
					continue;
				}

				if(f == null)
					throw new Exception("Filter name not defined");

				if(line.startsWith("\\stop"))
				{
					String name = line.replaceAll("^\\\\\\S+\\s+", "").trim();
					BusStop bs = FindBusStop(name);
					f.stops.add(bs);
					continue;
				}
				
				if(line.startsWith("\\bus"))
				{
					String buses = line.replaceAll("^\\\\\\S+\\s+", "").trim();
					String[] busArr = buses.split("\\s");
					for (int i = 0; i < busArr.length; i++)
					{
						Bus b = FindBus(busArr[i]);
						f.transport.add(b);
					}
					continue;
				}
			}
			catch(Exception ex)
			{
				int lineNo = lnr.getLineNumber();
				throw new Exception("Error in " + file + ":" + lineNo + "\n" + ex.getMessage(), ex);
			}
		}
	}

	Bus FindBus(int id) throws Exception
	{
		for (int i = 0; i < buses.size(); i++)
		{
			if(buses.get(i).id == id)
				return buses.get(i);
		}
		throw new Exception("Can't find bus #" + id);
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
		for (int i = 0; i < stops.size(); i++)
		{
			if(busStopName.compareTo(stops.get(i).name) == 0)
				return stops.get(i);
		}
		throw new Exception("Can't find bus stop '" + busStopName + "'");
	}
	
	FilterDef FindFilter(String name) throws Exception
	{
		for (int i = 0; i < filters.size(); i++)
		{
			if(name.compareTo(filters.get(i).name) == 0)
				return filters.get(i);
		}
		FilterDef fd = new FilterDef();
		fd.name = name;
		filters.add(fd);
		return fd;
	}

	BusStop FindBusStop(int busStopId) throws Exception
	{
		for (int i = 0; i < stops.size(); i++)
		{
			if(busStopId == stops.get(i).id)
				return stops.get(i);
		}
		throw new Exception("Can't find bus stop #" + busStopId);
	}
	
	Schedule FindSchedule(Vector<Schedule> schedules, int bus, int busStop, int day) throws Exception
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
		if(day == -1)
			throw new Exception("day is -1");
		schedules.add(sched);
		return sched;
	}
	
	Short ParseDay(String s) throws Exception
	{
		if(s.compareTo("a") == 0)
			return Schedule.ALLDAY;
		if(s.compareTo("w") == 0)
			return Schedule.WORKDAY;
		else if(s.compareTo("h") == 0)
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
			dos.writeByte(bus.startRoute);
			dos.writeByte(bus.endRoute);
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
		if(stops.size() > Byte.MAX_VALUE)
			throw new Exception("busStops.size() exceed store size");
		dos.writeByte(stops.size());
		for (int i = 0; i < stops.size(); i++)
		{
			BusStop busStop = stops.get(i);
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
			
			if(sched.times.size() > 255)
				throw new Exception("Times count exceed store size");

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
		
		if(derSchedules.size() > Short.MAX_VALUE)
			throw new Exception("derSchedules.size() exceed store size");
		
		dos.writeShort(derSchedules.size());

		for (int i = 0; i < derSchedules.size(); i++)
		{
			DerivedSchedule sched = derSchedules.get(i);

			dos.writeByte(sched.bus);
			dos.writeByte(sched.busStop);
			dos.writeByte(sched.dayFrom);
			dos.writeByte(sched.dayTo);
			dos.writeByte(sched.baseBusStop);
			dos.writeByte(sched.shift);
		}

		dos.flush();
		dos.close();
	}

	void WriteFilters(String file) throws Exception
	{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, false));
		
		if(filters.size() > Byte.MAX_VALUE)
			throw new Exception("filters.size() exceed store size");
		
		dos.writeByte(filters.size());

		for (int i = 0; i < filters.size(); i++)
		{
			FilterDef fd = filters.get(i);

			dos.writeUTF(fd.name);

			dos.writeShort(fd.transport.size());
			for (int j = 0; j < fd.transport.size(); j++)
			{
				dos.writeShort(fd.transport.get(j).id);
			}
			dos.writeShort(fd.stops.size());
			for (int j = 0; j < fd.stops.size(); j++)
			{
				dos.writeShort(fd.stops.get(j).id);
			}
		}

		dos.flush();
		dos.close();
	}

	void WriteSettings(String file) throws Exception
	{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, false));
		
		dos.writeShort(dayEnd);

		dos.flush();
		dos.close();
	}
}


