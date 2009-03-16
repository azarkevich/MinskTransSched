package com.mts;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.OM.*;

import com.options.Options;


public class ScheduleBuilder
{
	Filter filter;
	public ScheduleBuilder(Filter filter)
	{
		this.filter = filter;
	}
	
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
	public boolean showFull = false;
	
	boolean isShowTimeDiff()
	{
		return showTimeDiff;
	}

	public String GetScheduleText(BusStop busStop)
	{
		StringBuffer sb = new StringBuffer();

		Calendar cal = GetCalendar();
		
		int now = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
		if(now > 0 && now < TransSched.dayEnd)
		{
			// set previous day of week
			Date d = cal.getTime();
			d.setTime(d.getTime() - 24*60*60*1000L);
			cal.setTime(d);
			
			now += 24*60;
		}
		int beginWindow = now + WindowShift;
		int endWindow = beginWindow + WindowSize;

		String busStopName = busStop == null ? "<нет остановки>" : busStop.name;  
		sb.append(GetUserDayTypeString(cal));

		sb.append(" ");
		sb.append(FormatXTime(now, ":"));
		
		sb.append(" ");
		
		if(busStop != null && busStop.favorite)
			sb.append("* ");
		
		sb.append(busStopName);

		if(showDescription)
			sb.append("\n" + busStop.description);
		
		if(showDescription)
			sb.append("\nКонец дня: " + FormatXTime(TransSched.dayEnd, ":"));

		if(busStop == null)
			return sb.toString();

		if(showFull)
		{
			sb.append("\nПолное расписание\n");
		}
		else
		{
			sb.append("\nОкно: ");
			if(showDescription)
				sb.append("Размер: ");
			FormatTimeDiff(endWindow - beginWindow, sb);
	
			sb.append("; ");
			if(showDescription)
				sb.append("Сдвиг: ");
			if(WindowShift > 0)
				sb.append("+");
			FormatTimeDiff(WindowShift, sb);
			
			sb.append(" ");
			if(showDescription)
				sb.append("Диапазон: ");
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
		
		Schedule[] busOnStation = filter.FilterIt(busStop.schedules);
		
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

				// shift schedule
				minTime += schedShift;

				if(minTime < beginWindow && !showFull)
					continue;

				if(minTime <= endWindow || showFull)
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
				Schedule sched = busOnStation[i];
				sb.append(sched.bus.name);
				sb.append(": ");
				if(showDescription)
				{
					sb.append("\nМаршр.: ");
					sb.append(sched.bus.startRoute == null ? "неизв." : sched.bus.startRoute.name);
					sb.append(" / ");
					sb.append(sched.bus.endRoute == null ? "неизв." : sched.bus.endRoute.name);
					
					String schedFrom = GetSchedFrom(sched, cal);
					if(schedFrom.compareTo("") != 0)
					{
						sb.append("\nРасп.: ");
						sb.append(schedFrom);
					}
					sb.append("\n");
				}
				
				short[] times = GetSchedTimes(sched, cal);
				calcFirstLastIndexes(times, beginWindow, endWindow);
				
				if(showFull)
				{
					int currentHour = -1;
					for (int j = firstIndex; j < aboveLastIndex; j++)
					{
						int time = times[j] + schedShift;
						int hour = (time / 60) % 24;
						if(hour != currentHour)
						{
							sb.append("\n ");
							sb.append(FormatNum(hour));
							currentHour = hour;
							sb.append(":");
						}
						sb.append(" ");
						sb.append(FormatNum(time % 60));
					}
				}
				else
				{
					for (int j = firstIndex; j < aboveLastIndex; j++)
					{
						if(times[j] > endWindow)
							sb.append(">>");
	
						FormatBusTime(now, times[j] + schedShift, sb);
					}
				}
				sb.append("\n");
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}

	int firstIndex = 0;
	int aboveLastIndex = 0;
	void calcFirstLastIndexes(short[] times, int beginTime, int endTime)
	{
		if(showFull)
		{
			firstIndex = 0;
			aboveLastIndex = times.length;
			return;
		}

		for (firstIndex = 0; firstIndex < times.length; firstIndex++)
		{
			if(times[firstIndex] + schedShift >= beginTime)
				break;
		}
		if(firstIndex == times.length)
		{
			aboveLastIndex = firstIndex;
			return;
		}
		
		for (aboveLastIndex = firstIndex; aboveLastIndex < times.length; aboveLastIndex++)
		{
			if(times[aboveLastIndex] + schedShift > endTime)
				break;
		}
		if(aboveLastIndex == firstIndex)
			aboveLastIndex++;
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
	
	public int UserDayType = DAY_AUTO;

	private String GetUserDayTypeString(Calendar cal)
	{
		if(UserDayType == DAY_AUTO)
		{
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
