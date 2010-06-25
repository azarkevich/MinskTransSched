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
	public short[] getTimes(int day)
	{
		short[] times = m_times[day];
		
		if(times == null)
		{
			if(day == Calendar.SATURDAY || day == Calendar.SUNDAY)
				times = m_times[Schedule.HOLIDAY];
			else
				times = m_times[Schedule.WORKDAY];
		}
		
		if(times == null)
			times = m_times[Schedule.ALLDAY];
		
		if(times == null)
			times = m_zeroTimes;
		
		return times;
	}

	public void setTimes(int day, short[] times)
	{
		m_times[day] = times;
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
