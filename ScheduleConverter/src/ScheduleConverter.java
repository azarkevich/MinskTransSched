import java.io.*;
import java.nio.charset.*;
import java.util.Arrays;
import java.util.Vector;

public class ScheduleConverter
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		new ScheduleConverter().Convert(args);
	}
	
	void Convert(String[] args)
	{
		if(args.length == 0)
		{
			ShowHelp();
			return;
		}

		try{
			Vector<ScheduleFile> sheduleFiles = new Vector<ScheduleFile>();

			Charset c = Charset.forName("UTF-8");
			
			for (int i = 1; i < args.length; i++)
			{
				String arg = args[i];
				
				if(arg.startsWith("-c"))
				{
					i++;
					if(i == args.length)
						throw new Exception("Argument -c not supplied with parameter.");
					c = Charset.forName(args[i]);
					continue;
				}
				
				ScheduleFile sched = new ScheduleFile();
				sched.fileCharset = c;
				sched.fileName = arg;
				sheduleFiles.add(sched);
			}
			
			Vector<BusSchedule> schedules = new Vector<BusSchedule>();
			for (int i = 0; i < sheduleFiles.size(); i++)
			{
				ScheduleFile schedFile = sheduleFiles.get(i);
				ReadRawStrings(schedFile);
				System.out.println("Readed " + schedFile.fileName);
				ParseScheduleFile(schedFile, schedules);
			}

			// write schedules
			OutputStream os = new FileOutputStream(args[0], false);
			DataOutputStream dos = new DataOutputStream(os);
			
			for (int i = 0; i < schedules.size(); i++)
			{
				BusSchedule sched = schedules.get(i);
				WriteSchedule(dos, sched);
				System.out.println("Write " + sched.bus + " at " + sched.busStop + " DIR:" + sched.direction + " DAYS:" + sched.days);
			}

			dos.flush();
			dos.close();
			os.flush();
			os.close();
		}
		catch(Exception ex)
		{
			ShowHelp();
			System.out.println("Exception: " + ex.getMessage());
		}
	}
	
	void ShowHelp()
	{
		System.out.println("Usage: <executable name> output_file file1 file2 ...");
	}
	
	void ReadRawStrings(ScheduleFile sched) throws IOException
	{
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(sched.fileName),
						sched.fileCharset));
		
		sched.rawStrings = new Vector<String>();
		while(true)
		{
			String line = br.readLine();
			if(line == null)
				break;
			
			sched.rawStrings.add(line);
		}
	}
	
	void ParseScheduleFile(ScheduleFile schedFile, Vector<BusSchedule> schedules) throws Exception
	{
		String busName = null;
		String busStopName = null;
		Short days = 256;	// work days by default
		Short direction = 0;
		String firstBusStop = "";
		String lastBusStop = "";

		for (int lineNo = 0; lineNo < schedFile.rawStrings.size(); lineNo++)
		{
			try{
				String s = schedFile.rawStrings.elementAt(lineNo);
				s = s.replaceAll("\\s+", " ").trim();
				if(s.length() == 0)
					continue;
				
				if(s.startsWith("\\busstop"))
				{
					if(s.matches("\\\\busstop\\{[fF]\\}.+"))
						direction = 1;
					else if(s.matches("\\\\busstop\\{[bB]\\}.+"))
						direction = -1;
					else
						direction = 0;
					busStopName = s.replaceAll("^\\\\\\S+\\s+", "").trim();
					continue;
				}
				if(s.startsWith("\\bus"))
				{
					busName = s.replaceAll("^\\\\\\S+\\s+", "").trim();
					continue;
				}
				if(s.startsWith("\\days"))
				{
					days = ParseDays(s.replaceAll("^\\\\\\S+\\s+", "").trim());
					continue;
				}
				if(s.startsWith("\\first"))
				{
					firstBusStop = s.replaceAll("^\\\\\\S+\\s+", "").trim();
					continue;
				}
				if(s.startsWith("\\last"))
				{
					lastBusStop = s.replaceAll("^\\\\\\S+\\s+", "").trim();
					continue;
				}
				
				if(busName == null)
					throw new Exception("No bus specified");

				if(busStopName == null)
					throw new Exception("No bus stop specified");
				
				BusSchedule sched = FindBusSched(schedules, busName, busStopName, direction, days);
				sched.firstBusStop = firstBusStop;
				sched.lastBusStop = lastBusStop;
				ParseTimeLine(sched, s);
			}
			catch(Exception ex)
			{
				throw new Exception("Error in " + schedFile.fileName + ":" + lineNo + "\n" + ex.getMessage(), ex);
			}
		}
	}
	
	BusSchedule FindBusSched(Vector<BusSchedule> schedules, String bus, String busStop, Short direction, Short days)
	{
		for (int i = 0; i < schedules.size(); i++)
		{
			BusSchedule sched = schedules.elementAt(i); 
			if(bus.compareTo(sched.bus) == 0 
					&& busStop.compareTo(sched.busStop) == 0 
					&& sched.direction == direction
					&& sched.days == days)
				return sched;
		}
		BusSchedule sched = new BusSchedule();
		sched.bus = bus;
		sched.busStop = busStop;
		sched.direction = direction;
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
					days |= 1;
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
	
	void ParseTimeLine(BusSchedule sched, String s) throws Exception
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

	void WriteSchedule(DataOutputStream dos, BusSchedule sched) throws IOException
	{
		// format:
		// bus: modified UTF-8
		// busStop: modified UTF-8
		// firstBusStop: modified UTF-8
		// lastBusStop: modified UTF-8
		// days: short. (bit field, bit 0 - holiday, 1 - sunday ... bit 6 - saturday, bit 7 - workday)
		// direction: short
		// times count: short
		// times: short[]
		// ... next schedule
		
		dos.writeUTF(sched.bus);
		dos.writeUTF(sched.busStop);

		dos.writeUTF(sched.firstBusStop);
		dos.writeUTF(sched.lastBusStop);

		dos.writeShort(sched.days);
		dos.writeShort(sched.direction);
		
		dos.writeShort((short)sched.times.size());
		
		for (int i = 0; i < sched.times.size(); i++)
		{
			dos.writeShort(sched.times.get(i));
		}
	}
}


