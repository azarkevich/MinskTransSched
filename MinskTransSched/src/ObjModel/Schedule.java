package ObjModel;
import java.util.Calendar;



public class Schedule
{
	public Bus bus;
	public BusStop busStop;

	// indexes according to Calendar.SUNDAY ... Calendar.SATURDAY, 8 for work days, 9 for holidays, 0 for alldays 
	short[][] m_times = new short[10][];
	String[] m_from = new String[10];
	
	short[] m_zeroTimes = new short[0];
	
	public short[] getTimesRaw(int day)
	{
		return m_times[day];
	}
	
	public short[] getTimesExact(int day)
	{
		short[] times = m_times[day];
		
		if(times == null)
		{
			if(day == Calendar.SATURDAY || day == Calendar.SUNDAY)
				times = m_times[Schedule.HOLIDAY];
			else if(day == Calendar.MONDAY || day == Calendar.TUESDAY || day == Calendar.WEDNESDAY || day == Calendar.THURSDAY || day == Calendar.FRIDAY)
				times = m_times[Schedule.WORKDAY];

			if(times == null)
				times = m_times[Schedule.ALLDAY];
		}
		
		return times;
	}

	public short[] getTimes(int day)
	{
		short[] times = getTimesExact(day);
		
		if(times == null)
			times = m_zeroTimes;
		
		return times;
	}
	
	public static String getDayName(int day)
	{
		if(day == Schedule.ALLDAY)
			return "ALL";
		else if(day == Schedule.WORKDAY)
			return "WORKDAY";
		else if(day == Schedule.HOLIDAY)
			return "HOLIDAY";
		else if(day == Calendar.MONDAY)
			return "pn";
		else if(day == Calendar.TUESDAY)
			return "vt";
		else if(day == Calendar.WEDNESDAY)
			return "sr";
		else if(day == Calendar.THURSDAY)
			return "ch";
		else if(day == Calendar.FRIDAY)
			return "pt";
		else if(day == Calendar.SATURDAY)
			return "sb";
		else if(day == Calendar.SUNDAY)
			return "vs";
		
		return "?";
	}

	public void setTimes(int day, short[] times)
	{
		m_times[day] = times;
	}
	
	public void LogTimes()
	{
		for (int i = 0; i < m_times.length; i++)
		{
			short[] timesRaw = m_times[i];
			short[] times = getTimesExact(i);
			System.out.print("Day=" + getDayName(i));
			
			if(timesRaw == null)
				System.out.print(" RawTimes=<null>");
			else
				System.out.print(" RawTimes=" + timesRaw.length);
			
			if(times == null)
				System.out.print(" ExactTimes=<null>");
			else
				System.out.print(" ExactTimes=" + times.length);
			
			System.out.println();
		}
	}
	
	public static final byte SCHED_FROM_UNKNOWN = 0; 
	public static final byte SCHED_FROM_MINSK_TRANS_SITE = 1; 
	public static final byte SCHED_FROM_BUSSTOP = 2; 

	public String getFrom(int day)
	{
		return m_from[day];
	}
	
	public void setFrom(int day, String schedFrom)
	{
		m_from[day] = schedFrom;
	}

	public static final int ALLDAY = 0;
	public static final int WORKDAY = 8;
	public static final int HOLIDAY = 9;
}
