package com.mts;
import java.util.Calendar;
import java.util.TimeZone;

import com.options.Options;


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
	public boolean showTimeDiff = true;
	public boolean showBusFlow = false;
	
	public void toggleFullSched()
	{
		showFull = (showFull + 1) % 3;
		
		// SCHED_FULL_SIMPLE same as SCHED_FULL_NOTIME if showTimeDiff == false,
		// so toggle again
		if(showFull == SCHED_FULL_SIMPLE && showTimeDiff == false)
			toggleFullSched();
	}
	
	public static final int SCHED_FULL_NONE = 0; 
	public static final int SCHED_FULL_SIMPLE = 1; 
	public static final int SCHED_FULL_NOTIME = 2; 

	public int showFull = SCHED_FULL_NONE;
	
	boolean isFullSchedule()
	{
		return (showFull != SCHED_FULL_NONE);
	}

	boolean isShowTimeDiff()
	{
		return (showFull != SCHED_FULL_NOTIME) && showTimeDiff;
	}

	public String GetScheduleText()
	{
		if(Station == null)
			return "<not set station>";
		
		StringBuffer sb = new StringBuffer();

		Calendar cal = GetCalendar();
		
		int now = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
		int beginWindow = now + WindowShift;
		int endWindow = beginWindow + WindowSize;

		sb.append(GetUserDayTypeString() + " " + FormatXTime(now, ":") + ", " + (Station.bookmarked ? "* " : "") + Station.name);
		
		if(isFullSchedule())
		{
			sb.append("\nПолное расписание\n");
		}
		else
		{
			if(showDescription)
				sb.append("\n" + Station.description);
			sb.append("\nОкно: ");
			if(showDescription)
				sb.append("Размер:");
			FormatTimeDiff(endWindow - beginWindow, sb);
	
			sb.append("; ");
			if(showDescription)
				sb.append("Сдвиг:");
			if(WindowShift > 0)
				sb.append("+");
			FormatTimeDiff(WindowShift, sb);
			
			sb.append(" ");
			if(showDescription)
				sb.append("Диапазон:");
			sb.append("[" + FormatXTime(beginWindow, ":") + "; " + FormatXTime(endWindow, ":") + "]\n");
		}
		
		if(schedShift != 0)
		{
			sb.append("Сдвиг расп.: ");
			if(schedShift > 0)
				sb.append("+");
			FormatTimeDiff(schedShift, sb);
			sb.append("\n");
		}
		
		Schedule[] busOnStation = Station.schedules;
		
		// bus flow
		if(showBusFlow)
		{
			int[] indexes = new int[busOnStation.length];
			short[][] times = new short[busOnStation.length][];
			for (int i = 0; i < indexes.length; i++)
			{
				indexes[i] = 0;
				times[i] = GetSchedTimes(busOnStation[i], cal);
			}
			
			// find minimal time:
			boolean schedEmpty = true;
			Bus lastBus = null;
			while(true)
			{
				int minIndex = -1;
				short minTime = Short.MAX_VALUE;
				Bus b = null;
				for (int i = 0; i < indexes.length; i++)
				{
					if(indexes[i] != -1 && times[i][indexes[i]] < minTime)
					{
						minTime = times[i][indexes[i]];
						minIndex = i;
						b = busOnStation[i].bus;
					}
				}
				if(minIndex == -1)
					break;

				indexes[minIndex]++;
				if(indexes[minIndex] >= times[minIndex].length)
					indexes[minIndex] = -1;

				if(minTime < beginWindow && !isFullSchedule())
					continue;

				if(minTime <= endWindow || isFullSchedule())
				{
					if(lastBus != b)
					{
						sb.append("\n");
						sb.append(b.name);
						sb.append(": ");
					}
						
					FormatBusTime(now, minTime, sb);
					
					schedEmpty = false;
				}
				else
				{
					if(schedEmpty)
					{
						sb.append(b.name);
						sb.append(": ");
						sb.append(">>");
						FormatBusTime(now, minTime, sb);
					}
					break;
				}
				lastBus = b;
			}
		}
		else
		{
			sb.append("\n");

			for (int i = 0; i < busOnStation.length; i++)
			{
				boolean schedEmpty = true;
				Schedule sched = busOnStation[i];
				sb.append(sched.bus.name);
				sb.append(": ");
				boolean needLF = false;
				if(showDescription)
				{
					if(sched.bus.route.compareTo("") != 0)
					{
						sb.append("\nМаршр.:");
						sb.append(sched.bus.route);
						needLF = true;
					}
					
					String schedFrom = GetSchedFrom(sched, cal);
					if(schedFrom.compareTo("") != 0)
					{
						sb.append("\nРасп.:");
						sb.append(schedFrom);
						needLF = true;
					}
				}
				if(needLF)
					sb.append("\n");
				
				short[] times = GetSchedTimes(sched, cal);
				for (int j = 0; j < times.length; j++)
				{
					int time = times[j] + schedShift; 
					if(time < beginWindow && !isFullSchedule())
						continue;
	
					if(time <= endWindow || isFullSchedule())
					{
						FormatBusTime(now, time, sb);
						
						schedEmpty = false;
					}
					else
					{
						if(schedEmpty)
						{
							sb.append(">>");
							FormatBusTime(now, time, sb);
						}
						break;
					}
				}
				sb.append("\n");
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
	
	short[] GetSchedTimes(Schedule sched, Calendar cal)
	{
		if(UserDayType == DAY_AUTO)
		{
			return sched.getTimes(cal.get(Calendar.DAY_OF_WEEK));
		}
		else if(UserDayType == DAY_HOLIDAY)
		{
			return sched.getTimes(Schedule.HOLIDAY);
		}
		else if(UserDayType == DAY_WORK)
		{
			return sched.getTimes(Schedule.WORKDAY);
		}
		return sched.getTimes(Schedule.WORKDAY);
	}
	
	String GetSchedFrom(Schedule sched, Calendar cal)
	{
		if(UserDayType == DAY_AUTO)
		{
			return sched.getFrom(cal.get(Calendar.DAY_OF_WEEK));
		}
		else if(UserDayType == DAY_HOLIDAY)
		{
			return sched.getFrom(Schedule.HOLIDAY);
		}
		else if(UserDayType == DAY_WORK)
		{
			return sched.getFrom(Schedule.WORKDAY);
		}
		return sched.getFrom(Schedule.WORKDAY);
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
		
		if(isShowTimeDiff())
		{
			sb.append("[");
			int diff = stop - now;
			FormatTimeDiff(diff, sb);
			
			sb.append("]");
		}
		sb.append(" ");
	}
	
	public static final int DAY_AUTO = 0;
	public static final int DAY_WORK = 1;
	public static final int DAY_HOLIDAY = 2;

	public short WindowSize = Options.defWindowSize;
	public short WindowShift = Options.defWindowShift;
	public short schedShift = 0;
	
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
