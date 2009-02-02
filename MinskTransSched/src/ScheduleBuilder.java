import java.util.Calendar;
import java.util.TimeZone;

public class ScheduleBuilder
{
	public static String join(String[] s, String delimiter) {
	    if (s.length == 0)
	    	return "";

	    StringBuffer buffer = new StringBuffer(s[0]);
	    for (int i = 1; i < s.length; i++)
	    {
	    	buffer.append(delimiter).append(s[i]);
		}
	    return buffer.toString();
	}
	
	private String FormatNum(int d)
	{
		return (d < 10 ? "0" : "") + d;
	}
	
	private String FormatXTime(int xTime, String sep)
	{
		int hi = (xTime / 60) % 24;
		int lo = xTime % 60;
		return FormatNum(hi) + sep + FormatNum(lo);
	}
	
	
	Calendar GetCalendar()
	{
		return Calendar.getInstance(TimeZone.getDefault());
	}
	
	public boolean showDescription = false;

	public String GetScheduleText()
	{
		if(Station == null)
			return "<not set station>";
		
		StringBuffer sb = new StringBuffer();

		Calendar cal = GetCalendar();

		int now = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
		int beginWindow = now + WindowShift;
		int endWindow = beginWindow + WindowSize;

//		sb.append(cal.get(Calendar.SECOND) + "\n\n\n\n\n\n\n\n");
		sb.append(GetUserDayTypeString() + " " + FormatXTime(now, ":") + ", " + Station.name);
		if(showDescription)
			sb.append("\n" + Station.description);
//		sb.append("\n\n\n\n\n\n\n\n");
		sb.append("\nОкно: ");
		FormatTimeDiff(endWindow - beginWindow, sb);
//		sb.append("\n\n\n\n\n\n\n\n");
		sb.append(" [" + FormatXTime(beginWindow, ":") + "; " + FormatXTime(endWindow, ":") + "]\n");
		sb.append("\n");
//		sb.append("\n\n\n\n\n\n\n\n");
		
		Schedule[] busOnStation = Station.schedules;
		
		for (int i = 0; i < busOnStation.length; i++)
		{
			boolean schedEmpty = true;
			Schedule sched = busOnStation[i];
			sb.append(sched.bus.name);
			sb.append(": ");
			
			short[] times = GetBusTimes(sched, cal);
			for (int j = 0; j < times.length; j++)
			{
				if(times[j] < beginWindow)
					continue;

				if(times[j] <= endWindow)
				{
					FormatBusTime(now, times[j], sb);
					
					schedEmpty = false;
				}
				else
				{
					if(schedEmpty)
					{
						sb.append(">>");
						FormatBusTime(now, times[j], sb);
					}
					break;
				}
			}
			sb.append("\n\n");
		}
		
		return sb.toString();
	}
	
	short[] GetBusTimes(Schedule sched, Calendar cal)
	{
		if(UserDayType == DAY_AUTO)
		{
			return sched.GetTimes(cal.get(Calendar.DAY_OF_WEEK));
		}
		else if(UserDayType == DAY_HOLIDAY)
		{
			return sched.GetTimes(Schedule.HOLIDAY);
		}
		else if(UserDayType == DAY_WORK)
		{
			return sched.GetTimes(Schedule.WORKDAY);
		}
		return sched.GetTimes(Schedule.WORKDAY);
	}
	
	private void FormatTimeDiff(int diff, StringBuffer sb)
	{
		if(diff < 0)
		{
			sb.append("-");
			diff = -diff;
		}
		int hi = diff / 60;
		if(hi > 0)
			sb.append(hi + "h");
		sb.append(FormatNum(diff % 60) + "m");
	}
	
	private void FormatBusTime(int now, int stop, StringBuffer sb)
	{
		sb.append(FormatXTime(stop, ":"));
		sb.append("[");
		
		int diff = stop - now;
		FormatTimeDiff(diff, sb);
		
		sb.append("] ");
	}
	
	public static final int DAY_AUTO = 0;
	public static final int DAY_WORK = 1;
	public static final int DAY_HOLIDAY = 2;

	// in minutes
	public static final int DEFAULT_WINDOW_SIZE = 30;
	public static final int DEFAULT_WINDOW_SHIFT = -5;

	public int WindowSize = DEFAULT_WINDOW_SIZE;
	public int WindowShift = DEFAULT_WINDOW_SHIFT;
	
	public BusStop Station;
	
	public int UserDayType = DAY_AUTO;

	private String GetUserDayTypeString()
	{
		if(UserDayType == DAY_AUTO)
		{
			Calendar cal = GetCalendar();
			switch (cal.get(Calendar.DAY_OF_WEEK))
			{
			case 1:
				return "Вс";
			case 2:
				return "Пн";
			case 3:
				return "Вт";
			case 4:
				return "Ср";
			case 5:
				return "Чт";
			case 6:
				return "Пт";
			case 7:
				return "Сб";
			}
		}
		
		if(UserDayType == DAY_WORK)
			return "Буд";
		if(UserDayType == DAY_HOLIDAY)
			return "Вых";
		
		return "?";
	}
	
	public void ShiftDayType()
	{
		if(UserDayType == DAY_AUTO)
			UserDayType = DAY_WORK;
		else if(UserDayType == DAY_WORK)
			UserDayType = DAY_HOLIDAY;
		else if(UserDayType == DAY_HOLIDAY)
			UserDayType = DAY_AUTO;
	}
}
